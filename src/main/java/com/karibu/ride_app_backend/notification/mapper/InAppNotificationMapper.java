package com.karibu.ride_app_backend.notification.mapper;

import com.karibu.ride_app_backend.notification.dto.response.InAppNotificationResponse;
import com.karibu.ride_app_backend.notification.model.InAppNotification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper MapStruct pour les notifications In-App.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InAppNotificationMapper {

    /**
     * Convertit une entité Notification en DTO pour le frontend.
     */
    InAppNotificationResponse toResponse(InAppNotification entity);
}
