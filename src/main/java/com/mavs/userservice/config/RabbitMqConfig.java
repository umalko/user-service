package com.mavs.userservice.config;

import com.mavs.activity.model.ActivityType;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitMqConfig {

    private static final String QUEUE = "_QUEUE";

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue userNotificationRelationship() {
        return new Queue(ActivityType.NEW_USER_NOTIFICATION.name() + QUEUE, true, false, false);
    }

    @Bean
    public Queue userProfileRelationship() {
        return new Queue(ActivityType.NEW_USER_AUTH.name() + QUEUE, true, false, false);
    }
}
