package com.codejudge.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Submission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long problemId;
    private String language;          // java | python | c
    @Column(length = 20000) private String code;
    private String verdict = "PENDING"; // PENDING, ACCEPTED, WRONG_ANSWER, TIME_LIMIT_EXCEEDED, RUNTIME_ERROR, COMPILATION_ERROR, INTERNAL_ERROR
    @Column(length = 2000) private String detail;
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public Long getProblemId() { return problemId; }
    public void setProblemId(Long v) { problemId = v; }
    public String getLanguage() { return language; }
    public void setLanguage(String v) { language = v; }
    public String getCode() { return code; }
    public void setCode(String v) { code = v; }
    public String getVerdict() { return verdict; }
    public void setVerdict(String v) { verdict = v; }
    public String getDetail() { return detail; }
    public void setDetail(String v) { detail = v; }
    public Instant getCreatedAt() { return createdAt; }
}
