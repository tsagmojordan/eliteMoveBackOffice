package com.karibu.ride_app_backend.authentication.mapper;


import com.karibu.ride_app_backend.authentication.dto.request.CreateUserRequest;
import com.karibu.ride_app_backend.authentication.dto.response.UserResponse;
import com.karibu.ride_app_backend.authentication.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


/**
 * Mapper MapStruct pour {@link User}.
 *
 * <p>
 * Le mot de passe n'est jamais inclus dans le DTO de réponse.
 * L'encodage du mot de passe est géré dans le service, pas ici.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { RoleMapper.class })
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    User toEntity(CreateUserRequest request);
}
