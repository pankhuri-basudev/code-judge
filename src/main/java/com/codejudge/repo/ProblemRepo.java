package com.codejudge.repo;
import com.codejudge.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProblemRepo extends JpaRepository<Problem, Long> {}
