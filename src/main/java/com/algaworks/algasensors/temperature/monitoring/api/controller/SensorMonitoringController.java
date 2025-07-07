package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorMonitoringOutPut;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sensors/{sensorId}/monitoring")
public class SensorMonitoringController {

    private final SensorMonitoringRepository sensorMonitoringRepository;

    @GetMapping
    public SensorMonitoringOutPut getDetails(@PathVariable TSID sensorId) {
        SensorMonitoring sesnorMonitoring = findByIdOrDefault(sensorId);

        return SensorMonitoringOutPut.builder()
                .id(sesnorMonitoring.getId().getValue())
                .enabled(sesnorMonitoring.getEnabled())
                .lastTemperature(sesnorMonitoring.getLastTemperature())
                .updatedAt(sesnorMonitoring.getUpdatedAt())
                .build();
    }

    @PutMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enalbe(@PathVariable TSID sensorId) {
        SensorMonitoring sesnorMonitoring = findByIdOrDefault(sensorId);
        sesnorMonitoring.setEnabled(true);
        sensorMonitoringRepository.saveAndFlush(sesnorMonitoring);
    }

    @DeleteMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable TSID sensorId) {
        SensorMonitoring sesnorMonitoring = findByIdOrDefault(sensorId);
        sesnorMonitoring.setEnabled(false);
        sensorMonitoringRepository.saveAndFlush(sesnorMonitoring);
    }

    private SensorMonitoring findByIdOrDefault(TSID sensorId) {
        return sensorMonitoringRepository
                .findById(new SensorId(sensorId))
                .orElse(SensorMonitoring.builder()
                        .id(new SensorId(sensorId))
                        .enabled(false)
                        .lastTemperature(null)
                        .build());
    }
}
