package com.karibu.ride_app_backend.authentication.mapper;

import com.karibu.ride_app_backend.authentication.dto.request.RoleRequest;
import com.karibu.ride_app_backend.authentication.dto.response.RoleResponse;
import com.karibu.ride_app_backend.authentication.model.Role;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour {@link Role}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { PermissionMapper.class })
public interface RoleMapper {

    RoleResponse toResponse(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    void partialUpdate(@MappingTarget Role role, RoleRequest request);
}
