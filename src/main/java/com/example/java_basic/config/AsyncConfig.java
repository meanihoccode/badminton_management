package com.example.java_basic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Số lượng luồng chạy luôn luôn được giữ lại
        executor.setMaxPoolSize(5); // Số lượng luồng tối đa được tạo ra
        executor.setQueueCapacity(100); // Kích thước hàng đợi
        executor.setThreadNamePrefix("EmailAsyncThread-");
        executor.initialize();
        return executor;
    }
}
