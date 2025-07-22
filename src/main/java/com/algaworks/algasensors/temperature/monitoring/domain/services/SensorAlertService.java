package com.algaworks.algasensors.temperature.monitoring.domain.services;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class SensorAlertService {

    private final SensorAlertRepository sensorAlertRepository;

    @Transactional
    public void handleAlert(TemperatureLogData temperatureLogData) {
        sensorAlertRepository.findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(alert ->
                        alertMaxMinTemperature(alert, temperatureLogData),
                        () -> temperatureIgnored(temperatureLogData));
    }

    private void alertMaxMinTemperature(SensorAlert alert, TemperatureLogData temperatureLogData) {
        if (Objects.nonNull(alert.getMaxTemperature())
                && temperatureLogData.getValue().compareTo(alert.getMaxTemperature()) >= 0) {
            log.info("Alerting max temperature - SensorId {}, Value {}",
                    temperatureLogData.getSensorId(), temperatureLogData.getValue());
        } else if (Objects.nonNull(alert.getMinTemperature())
                && temperatureLogData.getValue().compareTo(alert.getMinTemperature()) <= 0) {
            log.info("Alerting min temperature - SensorId {}, Value {}",
                    temperatureLogData.getSensorId(), temperatureLogData.getValue());
        } else {
            temperatureIgnored(temperatureLogData);
        }
    }

    private static void temperatureIgnored(TemperatureLogData temperatureLogData) {
        log.info("Ignored alert - SensorId {}, Value {}",
                temperatureLogData.getSensorId(), temperatureLogData.getValue());
    }
}

