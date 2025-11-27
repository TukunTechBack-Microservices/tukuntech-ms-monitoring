package com.upc.tukuntechmsmonitoring.monitoring.application.service;


import com.upc.tukuntechmsmonitoring.monitoring.domain.entity.Alert;
import com.upc.tukuntechmsmonitoring.monitoring.domain.entity.VitalSignRecord;
import com.upc.tukuntechmsmonitoring.monitoring.domain.repositories.AlertRepository;
import com.upc.tukuntechmsmonitoring.monitoring.infrastructure.realtime.SseEmitterService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AlertDomainService {

    private final AlertRepository alertRepository;
    private final SseEmitterService sseEmitterService;

    public AlertDomainService(AlertRepository alertRepository,
                              SseEmitterService sseEmitterService) {
        this.alertRepository = alertRepository;
        this.sseEmitterService = sseEmitterService;
    }

    /**
     * Analiza una medición completa y genera (si aplica) una alerta de dominio.
     * El dominio (VitalSignRecord) decide si es anormal; este servicio orquesta la persistencia y emisión.
     */
    @Transactional
    public void processMeasurement(VitalSignRecord record) {
        record.generateAlertIfNeeded().ifPresent(alert -> {
            alertRepository.save(alert);
            // 🔸 Enviar en tiempo real a cuidadores suscritos
            sseEmitterService.emitAlert(alert);
        });
    }

    /**
     * Permite registrar alertas externas (por ejemplo, recibidas desde IoT o servicios externos).
     */
    @Transactional
    public void registerExternalAlert(Alert alert) {
        alertRepository.save(alert);
        sseEmitterService.emitAlert(alert);
    }
}
