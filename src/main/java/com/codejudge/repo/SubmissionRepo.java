package com.codejudge.repo;
import com.codejudge.model.Submission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findByProblemIdOrderByIdDesc(Long problemId);
}
