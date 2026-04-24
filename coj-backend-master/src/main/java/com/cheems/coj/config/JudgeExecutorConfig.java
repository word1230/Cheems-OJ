package com.cheems.coj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cheems
 * @description 判题线程池配置
 */
@Configuration
public class JudgeExecutorConfig {

    @Value("${judge.executor.pool-size}")
    private int corePoolSize;

    @Value("${judge.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${judge.executor.queue-capacity}")
    private int queueCapacity;

    @Value("${judge.sandbox.max-concurrency}")
    private int sandboxMaxConcurrency;


    @Bean("judgeExecutor")
    public ThreadPoolExecutor judgeExecutor() {
        AtomicInteger threadId = new AtomicInteger();
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("judge-worker-" + threadId.getAndIncrement());
            thread.setDaemon(false);
            return thread;
        };

        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }


    @Bean("judgeSemaphore")
    public Semaphore sandboxSemaphore() {
        return new Semaphore(sandboxMaxConcurrency);
    }


}
