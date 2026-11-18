package com.water.service;

import com.water.model.SampleStatus;

import java.util.logging.Logger;

public class WaterSampleService {
    private static final Logger logger = Logger.getLogger(WaterSampleService.class.getName());

    // Перевірка та виконання переходу в новий стан
    public SampleStatus changeSampleStatus(SampleStatus currentStatus, SampleStatus targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            throw new IllegalArgumentException("Статуси проби не можуть бути null.");
        }

        if (!isTransitionAllowed(currentStatus, targetStatus)) {
            String errorMsg = "Заборонений перехід життєвого циклу: з форми [" +
                    currentStatus.getUkrainianName() + "] у стан [" +
                    targetStatus.getUkrainianName() + "]";
            logger.warning(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        logger.info("Успішна зміна статусу проби води на: " + targetStatus.getUkrainianName());
        return targetStatus;
    }

    // Визначення бізнес-правил: допустимі переходи
    private boolean isTransitionAllowed(SampleStatus from, SampleStatus to) {
        switch (from) {
            case REGISTERED:
                // Із Зареєстровано можна перейти тільки в Дослідження або одразу в Архів (наприклад, скасування)
                return to == SampleStatus.IN_ANALYSIS || to == SampleStatus.ARCHIVED;
            case IN_ANALYSIS:
                // З Дослідження можна перейти в Перевірено або в Архів
                return to == SampleStatus.VERIFIED || to == SampleStatus.ARCHIVED;
            case VERIFIED:
                // З Перевірено проба йде в Архів
                return to == SampleStatus.ARCHIVED;
            case ARCHIVED:
                // З архіву нікуди переходити не можна (кінцевий стан)
                return false;
            default:
                return false;
        }
    }
}
