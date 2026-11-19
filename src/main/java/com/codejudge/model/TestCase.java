package com.codejudge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class TestCase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(length = 5000) private String input;
    @Column(length = 5000) private String expectedOutput;
    @JsonIgnore @ManyToOne private Problem problem;

    public String getInput() { return input; }
    public void setInput(String v) { input = v; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String v) { expectedOutput = v; }
    public Problem getProblem() { return problem; }
    public void setProblem(Problem v) { problem = v; }
}
