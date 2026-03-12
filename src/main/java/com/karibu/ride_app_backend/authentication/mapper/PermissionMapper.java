package com.karibu.ride_app_backend.authentication.mapper;


import com.karibu.ride_app_backend.authentication.dto.request.PermissionRequest;
import com.karibu.ride_app_backend.authentication.dto.response.PermissionResponse;
import com.karibu.ride_app_backend.authentication.model.Permission;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour {@link Permission}.
 *
 * <p>
 * Les méthodes {@code toResponse} et {@code toEntity} assurent
 * la séparation stricte entre entité JPA et DTO REST.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Permission toEntity(PermissionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void partialUpdate(@MappingTarget Permission permission, PermissionRequest request);
}
