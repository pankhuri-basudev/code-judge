package com.codejudge.web;

import com.codejudge.model.Submission;
import com.codejudge.repo.*;
import com.codejudge.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class SubmissionController {
    public record SubmitRequest(@NotNull Long problemId,
                                @NotBlank @Pattern(regexp = "java|python|c") String language,
                                @NotBlank @Size(max = 20000) String code) {}

    private final SubmissionRepo submissions;
    private final ProblemRepo problems;
    private final JudgeService judge;

    public SubmissionController(SubmissionRepo s, ProblemRepo p, JudgeService j) { submissions = s; problems = p; judge = j; }

    @PostMapping("/submissions") @ResponseStatus(HttpStatus.ACCEPTED)
    public Submission submit(@Valid @RequestBody SubmitRequest req) {
        if (!problems.existsById(req.problemId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "problem not found");
        Submission s = new Submission();
        s.setProblemId(req.problemId()); s.setLanguage(req.language()); s.setCode(req.code());
        s = submissions.save(s);
        judge.judge(s.getId()); // returns immediately; verdict is filled in asynchronously
        return s;
    }

    @GetMapping("/submissions/{id}") public Submission get(@PathVariable Long id) {
        return submissions.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/problems/{id}/submissions") public List<Submission> forProblem(@PathVariable Long id) {
        return submissions.findByProblemIdOrderByIdDesc(id);
    }
}
