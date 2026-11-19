package com.codejudge;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
    @Bean("judgeExecutor")
    Executor judgeExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(2); ex.setMaxPoolSize(2); ex.setQueueCapacity(200);
        ex.setThreadNamePrefix("judge-");
        return ex;
    }
}
