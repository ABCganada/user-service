package com.service.userservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue inspectionRequestQueue() {
        return new Queue("inspection-request-queue", true);
    }
}
