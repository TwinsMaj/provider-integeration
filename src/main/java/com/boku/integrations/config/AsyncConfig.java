package com.boku.integrations.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
	private static final int POOL_SIZE = 4;
	private static final int QUEUE_CAPACITY = 200;
	private static final int MAX_POOL_SIZE = 4;
	
	@Bean("asyncTaskExecutor")
	public Executor asyncTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(POOL_SIZE);
		taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
		taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
		taskExecutor.setThreadNamePrefix("AsyncTaskThread-");
		taskExecutor.initialize();

		return taskExecutor;
	}
}
