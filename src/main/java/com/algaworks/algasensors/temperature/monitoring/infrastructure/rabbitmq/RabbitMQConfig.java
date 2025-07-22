package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private static final String PROCESS_TEMPERATURE = "temperature-monitoring.process-temperature.v1";
    public static final String QUEUE_PROCESS_TEMPERATURE = PROCESS_TEMPERATURE + ".q";
    public static final String DLQ_PROCESS_TEMPERATURE = PROCESS_TEMPERATURE + ".dlq";
    public static final String QUEUE_ALERTING = "temperature-monitoring.alerting.v1.q";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue queueProcessTempertature() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "");
        args.put("x-dead-letter-routing-key", DLQ_PROCESS_TEMPERATURE);

        return QueueBuilder.durable(QUEUE_PROCESS_TEMPERATURE)
                .withArguments(args).build();
    }

    @Bean
    public Queue dlqProcessTempertature() {
        return QueueBuilder.durable(DLQ_PROCESS_TEMPERATURE).build();
    }

    @Bean
    public Queue queueAlerting() {
        return QueueBuilder.durable(QUEUE_ALERTING).build();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public Binding bindingProcessTemperature() {
        return BindingBuilder
                .bind(queueProcessTempertature())
                .to(fanoutExchange());
    }

    @Bean
    public Binding bindingAlerting() {
        return BindingBuilder
                .bind(queueAlerting())
                .to(fanoutExchange());
    }

    public FanoutExchange fanoutExchange() {
        return ExchangeBuilder
                .fanoutExchange("temperature-processsing.temperature-received.v1.e")
                .build();
    }
}
