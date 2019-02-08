package net.safedata.reactive.intro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AppConfig {

    private static final int PROCESSORS_COUNT = Runtime.getRuntime().availableProcessors();

    @Primary
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(PROCESSORS_COUNT / 4);
        threadPoolTaskExecutor.setMaxPoolSize(PROCESSORS_COUNT / 2);
        threadPoolTaskExecutor.setKeepAliveSeconds(10);
        threadPoolTaskExecutor.setQueueCapacity(30);
        threadPoolTaskExecutor.setThreadGroupName("reactive-intro-thread-pool-");
        threadPoolTaskExecutor.setThreadNamePrefix("reactive-intro-thread-");
        threadPoolTaskExecutor.setAwaitTerminationSeconds(3);
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }
}
