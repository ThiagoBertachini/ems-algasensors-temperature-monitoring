package com.algaworks.algasensors.temperature.monitoring.domain.services;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemperatureMonitoringService {

    private final SensorMonitoringRepository sensorMonitoringRepository;
    private final TemperatureLogRepository temperatureLogRepository;

    @Transactional
    public void processTemperatureReadings(TemperatureLogData temperatureLogData) {

        sensorMonitoringRepository.findById(
                        new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(
                        sensor -> handleSensorMonitoring(sensor, temperatureLogData),
                        () -> logIgnoredTemperature(temperatureLogData));

    }

    private void handleSensorMonitoring(SensorMonitoring sensor, TemperatureLogData temperatureLogData) {
        if (sensor.isEnabled()) {
            sensor.setLastTemperature(temperatureLogData.getValue());
            sensor.setUpdatedAt(OffsetDateTime.now());
            sensorMonitoringRepository.save(sensor);

            TemperatureLog temperatureLog = TemperatureLog.builder()
                    .id(new TemperatureLogId(temperatureLogData.getId()))
                    .registeredAt(sensor.getUpdatedAt())
                    .value(temperatureLogData.getValue())
                    .sensorId(new SensorId(temperatureLogData.getSensorId()))
                    .build();
            temperatureLogRepository.save(temperatureLog);
            log.info("Temperature updated - SensorId {}, Value {}",
                    temperatureLogData.getSensorId(),
                    temperatureLogData.getValue());
        } else {
            logIgnoredTemperature(temperatureLogData);
        }
    }

    private void logIgnoredTemperature(TemperatureLogData temperatureLogData) {
        log.info("ignored temperature - SensorId {}, Value {}",
                temperatureLogData.getSensorId(),
                temperatureLogData.getValue());
    }
}
