package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import io.hypersistence.tsid.TSID;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sensors/{sensorId}/alert")
public class SensorAlertController {

    private final SensorAlertRepository sensorAlertRepository;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public SensorAlertOutput getAlertInfo(@PathVariable @Nonnull TSID sensorId) {
        SensorAlert sensorAlert = findById(sensorId);
        return SensorAlertOutput.builder()
                .id(sensorAlert.getId().getValue())
                .minTemperature(sensorAlert.getMinTemperature())
                .maxTemperature(sensorAlert.getMaxTemperature())
                .build();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public SensorAlertOutput updateAlertInfo(@PathVariable @Nonnull TSID sensorId,
                                          @RequestBody SensorAlertOutput sensorAlertOutput) {
        SensorAlert sensorAlert = sensorAlertRepository.findById(new SensorId(sensorId))
                .orElse(SensorAlert.builder()
                        .id(new SensorId(sensorId))
                        .minTemperature(sensorAlertOutput.getMinTemperature())
                        .maxTemperature(sensorAlertOutput.getMaxTemperature())
                        .build());
        sensorAlert = sensorAlertRepository.save(sensorAlert);

        return SensorAlertOutput.builder()
                .id(sensorAlert.getId().getValue())
                .minTemperature(sensorAlert.getMinTemperature())
                .maxTemperature(sensorAlert.getMaxTemperature())
                .build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlertInfo(@PathVariable @Nonnull TSID sensorId) {
        SensorAlert sensorAlert = findById(sensorId);

        sensorAlertRepository.delete(sensorAlert);
    }

    private SensorAlert findById(TSID sensorId) {
        return sensorAlertRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
