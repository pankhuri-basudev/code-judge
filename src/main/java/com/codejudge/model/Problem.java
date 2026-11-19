package com.codejudge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;

@Entity
public class Problem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String title;
    @Column(length = 5000) private String description;
    private String difficulty;
    private int timeLimitSec = 2;
    // Hidden test cases are never serialized to clients.
    @JsonIgnore
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TestCase> testCases = new ArrayList<>();

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String v) { difficulty = v; }
    public int getTimeLimitSec() { return timeLimitSec; }
    public void setTimeLimitSec(int v) { timeLimitSec = v; }
    public List<TestCase> getTestCases() { return testCases; }
}
