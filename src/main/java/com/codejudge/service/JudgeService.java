package com.codejudge.service;

import com.codejudge.model.*;
import com.codejudge.repo.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class JudgeService {
    private final SubmissionRepo submissions;
    private final ProblemRepo problems;
    private final DockerRunner docker;

    public JudgeService(SubmissionRepo s, ProblemRepo p, DockerRunner d) { submissions = s; problems = p; docker = d; }

    /** Runs on a small thread pool so submissions never block the HTTP request. */
    @Async("judgeExecutor")
    public void judge(Long submissionId) {
        Submission sub = submissions.findById(submissionId).orElseThrow();
        Path dir = null;
        try {
            Problem problem = problems.findById(sub.getProblemId()).orElseThrow();
            DockerRunner.Lang lang = DockerRunner.LANGS.get(sub.getLanguage());
            dir = docker.prepare(lang, sub.getCode());

            if (lang.compile() != null) {
                var c = docker.exec(lang, dir, lang.compile(), null, 30);
                if (c.timedOut() || c.exitCode() != 0) {
                    finish(sub, "COMPILATION_ERROR", trim(c.output()));
                    return;
                }
            }
            int i = 0;
            for (TestCase tc : problem.getTestCases()) {
                i++;
                var r = docker.exec(lang, dir, lang.run(), tc.getInput(), problem.getTimeLimitSec() + 5);
                if (r.timedOut()) { finish(sub, "TIME_LIMIT_EXCEEDED", "Test " + i); return; }
                if (r.exitCode() != 0) { finish(sub, "RUNTIME_ERROR", "Test " + i + ": " + trim(r.output())); return; }
                if (!normalize(r.output()).equals(normalize(tc.getExpectedOutput()))) {
                    finish(sub, "WRONG_ANSWER", "Failed on test " + i); return;
                }
            }
            finish(sub, "ACCEPTED", "Passed " + i + " tests");
        } catch (Exception e) {
            finish(sub, "INTERNAL_ERROR", e.getMessage());
        } finally {
            cleanup(dir);
        }
    }

    private void finish(Submission s, String verdict, String detail) {
        s.setVerdict(verdict); s.setDetail(detail); submissions.save(s);
    }
    private static String normalize(String s) { return s == null ? "" : s.replace("\r", "").stripTrailing(); }
    private static String trim(String s) { return s == null ? "" : s.substring(0, Math.min(s.length(), 1500)); }
    private static void cleanup(Path dir) {
        if (dir == null) return;
        try (Stream<Path> w = Files.walk(dir)) { w.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
        catch (Exception ignored) {}
    }
}
