package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.services.SensorAlertService;
import com.algaworks.algasensors.temperature.monitoring.domain.services.TemperatureMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_ALERTING;
import static com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_PROCESS_TEMPERATURE;

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitMQListener {

    private final TemperatureMonitoringService temperatureMonitoringService;
    private final SensorAlertService sensorAlertService;

    @RabbitListener(queues = QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3")
    public void handleProcessTemperature(@Payload TemperatureLogData temperatureLogData) {
        temperatureMonitoringService.processTemperatureReadings(temperatureLogData);
    }

    @RabbitListener(queues = QUEUE_ALERTING, concurrency = "2-3")
    public void handleAlerting(@Payload TemperatureLogData temperatureLogData) {
        // temperatureMonitoringService.processTemperatureReadings(temperatureLogData);
        sensorAlertService.handleAlert(temperatureLogData);
        log.info("Alerting - SensorId {}, Value {}",
                temperatureLogData.getSensorId(), temperatureLogData.getValue());
    }
}
