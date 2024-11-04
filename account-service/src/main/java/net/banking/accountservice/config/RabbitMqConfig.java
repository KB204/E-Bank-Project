package net.banking.accountservice.config;

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
    @Value("${rabbitmq.queue.email.name}")
    private String emailQueue;
    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;
    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    @Bean
    public Queue transactionNotificationQueue(){
        return new Queue(emailQueue);
    }

    @Bean
    public DirectExchange transactionNotificationExchange(){
        return new DirectExchange(emailExchange);
    }

    @Bean
    public Binding emailBinding(){
        return BindingBuilder.bind(transactionNotificationQueue())
                .to(transactionNotificationExchange())
                .with(emailRoutingKey);
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
