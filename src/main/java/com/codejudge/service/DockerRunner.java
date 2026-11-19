package com.codejudge.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Runs untrusted code inside a locked-down Docker container. */
@Component
public class DockerRunner {
    public record Result(int exitCode, String output, boolean timedOut) {}

    public record Lang(String image, String file, String compile, String run) {}

    static final Map<String, Lang> LANGS = Map.of(
        "java",   new Lang("eclipse-temurin:17-jdk-alpine", "Main.java", "javac Main.java", "java -Xmx128m Main"),
        "python", new Lang("python:3.11-alpine", "Main.py", null, "python Main.py"),
        "c",      new Lang("gcc:13", "Main.c", "gcc Main.c -O2 -o main", "./main"));

    @Value("${judge.workdir}") private String workdir;

    public Path prepare(Lang lang, String code) throws IOException {
        Path dir = Files.createDirectories(Path.of(workdir)).resolve(UUID.randomUUID().toString());
        Files.createDirectories(dir);
        Files.writeString(dir.resolve(lang.file()), code);
        dir.toFile().setWritable(true, false); // container user must write compiled output
        return dir;
    }

    public Result exec(Lang lang, Path dir, String shellCmd, String stdin, int timeoutSec) throws Exception {
        String name = "judge-" + UUID.randomUUID();
        List<String> cmd = List.of("docker", "run", "--rm", "-i", "--name", name,
            "--network", "none", "--memory", "256m", "--cpus", "0.5", "--pids-limit", "64",
            "--cap-drop", "ALL", "--security-opt", "no-new-privileges",
            "-v", dir.toAbsolutePath() + ":/code", "-w", "/code",
            lang.image(), "sh", "-c", shellCmd);
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        try (OutputStream os = p.getOutputStream()) {
            if (stdin != null) os.write(stdin.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {}
        // Read output on a separate thread so a chatty program can't block us.
        StringBuilder out = new StringBuilder();
        Thread reader = new Thread(() -> {
            try (var r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                char[] buf = new char[1024]; int n;
                while ((n = r.read(buf)) > 0 && out.length() < 20000) out.append(buf, 0, n);
            } catch (IOException ignored) {}
        });
        reader.start();
        boolean done = p.waitFor(timeoutSec, TimeUnit.SECONDS);
        if (!done) {
            new ProcessBuilder("docker", "kill", name).start().waitFor(5, TimeUnit.SECONDS);
            p.destroyForcibly();
            return new Result(-1, out.toString(), true);
        }
        reader.join(1000);
        return new Result(p.exitValue(), out.toString(), false);
    }
}
