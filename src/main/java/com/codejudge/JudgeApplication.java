package com.codejudge;

import com.codejudge.model.*;
import com.codejudge.repo.ProblemRepo;
import java.util.List;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JudgeApplication {
    public static void main(String[] args) { SpringApplication.run(JudgeApplication.class, args); }

    @Bean
    CommandLineRunner seed(ProblemRepo repo) {
        return args -> {
            if (repo.count() > 0) return;
            Problem p = new Problem();
            p.setTitle("Sum of Two Numbers");
            p.setDescription("Read two integers from one line and print their sum.");
            p.setDifficulty("EASY");
            p.setTimeLimitSec(2);
            for (String[] t : new String[][]{{"1 2\n", "3"}, {"-5 10\n", "5"}, {"100 250\n", "350"}}) {
                TestCase tc = new TestCase();
                tc.setInput(t[0]); tc.setExpectedOutput(t[1]); tc.setProblem(p);
                p.getTestCases().add(tc);
            }
            repo.save(p);
        };
    }
}
