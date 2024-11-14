package net.banking.loanservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Value("${rabbitmq.queue.notification.name}")
    private String notificationQueue;
    @Value("${rabbitmq.exchange.notification.name}")
    private String notificationExchange;
    @Value("${rabbitmq.binding.notification.name}")
    private String notificationRoutingKey;

    @Bean
    public Queue loanNotificationQueue(){
        return new Queue(notificationQueue);
    }

    @Bean
    public DirectExchange loanNotificationExchange(){
        return new DirectExchange(notificationExchange);
    }

    @Bean
    public Binding notificationBinding(){
        return BindingBuilder.bind(loanNotificationQueue())
                .to(loanNotificationExchange())
                .with(notificationRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
