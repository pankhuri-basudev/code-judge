# Code Judge

A mini online judge: submit Java / Python / C code, it runs against hidden test cases inside a
locked-down Docker container and returns a verdict.

## Run
Requirements: JDK 17, Maven, Docker running locally.

```bash
docker pull eclipse-temurin:17-jdk-alpine && docker pull python:3.11-alpine && docker pull gcc:13
mvn spring-boot:run
```

## API
| Method | Path | Purpose |
|---|---|---|
| GET | /api/problems | list problems |
| GET | /api/problems/{id} | problem details (test cases hidden) |
| POST | /api/problems | add a problem with test cases |
| POST | /api/submissions | submit code, returns 202 + id |
| GET | /api/submissions/{id} | poll for verdict |
| GET | /api/problems/{id}/submissions | submission history |

Try it:
```bash
curl -X POST localhost:8080/api/submissions -H 'Content-Type: application/json' -d '{
  "problemId":1,"language":"python",
  "code":"a,b=map(int,input().split())\nprint(a+b)"}'
curl localhost:8080/api/submissions/1
```
Verdicts: ACCEPTED, WRONG_ANSWER, TIME_LIMIT_EXCEEDED, RUNTIME_ERROR, COMPILATION_ERROR.

## Sandbox
Each run: no network, 256 MB RAM, 0.5 CPU, 64 PIDs, all capabilities dropped, hard timeout with `docker kill`.

## Roadmap
- [ ] JWT auth + roles (user/admin)
- [ ] RabbitMQ/Redis queue replacing the in-process thread pool
- [ ] Contests + Redis sorted-set leaderboard
- [ ] React + Monaco editor frontend
- [ ] JUnit tests, Docker Compose, deployment
