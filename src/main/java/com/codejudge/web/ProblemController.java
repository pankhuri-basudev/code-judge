package com.codejudge.web;

import com.codejudge.model.*;
import com.codejudge.repo.ProblemRepo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    public record TestCaseDto(@NotNull String input, @NotNull String expectedOutput) {}
    public record ProblemRequest(@NotBlank String title, @NotBlank String description, String difficulty,
                                 @Min(1) @Max(10) Integer timeLimitSec, @NotEmpty List<@Valid TestCaseDto> testCases) {}

    private final ProblemRepo repo;
    public ProblemController(ProblemRepo repo) { this.repo = repo; }

    @GetMapping public List<Problem> list() { return repo.findAll(); }

    @GetMapping("/{id}") public Problem get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Problem create(@Valid @RequestBody ProblemRequest req) {
        Problem p = new Problem();
        p.setTitle(req.title()); p.setDescription(req.description());
        p.setDifficulty(req.difficulty() == null ? "MEDIUM" : req.difficulty());
        if (req.timeLimitSec() != null) p.setTimeLimitSec(req.timeLimitSec());
        for (TestCaseDto t : req.testCases()) {
            TestCase tc = new TestCase();
            tc.setInput(t.input()); tc.setExpectedOutput(t.expectedOutput()); tc.setProblem(p);
            p.getTestCases().add(tc);
        }
        return repo.save(p);
    }
}
