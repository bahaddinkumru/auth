package auth.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import auth.auth.model.dto.request.UserRegisterRequest;
import auth.auth.model.dto.response.UserResponse;
import auth.auth.model.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRegisterRequest request);

    UserResponse toResponse(User user);
}
