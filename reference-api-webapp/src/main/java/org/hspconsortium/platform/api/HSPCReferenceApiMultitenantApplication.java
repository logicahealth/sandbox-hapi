package org.hspconsortium.platform.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity(debug = false)
@SpringBootApplication
public class HSPCReferenceApiMultitenantApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(HSPCReferenceApiMultitenantApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HSPCReferenceApiMultitenantApplication.class);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorFactoryBean scheduledExecutorService() {
        ScheduledExecutorFactoryBean b = new ScheduledExecutorFactoryBean();
        b.setPoolSize(5);
        return b;
    }

    @Bean(name="hapiJpaTaskExecutor")
    public AsyncTaskExecutor taskScheduler() {
        ConcurrentTaskScheduler retVal = new ConcurrentTaskScheduler();
        retVal.setConcurrentExecutor(scheduledExecutorService().getObject());
        retVal.setScheduledExecutor(scheduledExecutorService().getObject());
        return retVal;
    }

}
