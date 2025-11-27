package com.upc.tukuntechmsmonitoring.monitoring.application.commands;

public record CreateVitalSignCommand(
        Long patientId,
        Long deviceId,
        Integer heartRate,
        Integer oxygenLevel,
        Double temperature
) {}