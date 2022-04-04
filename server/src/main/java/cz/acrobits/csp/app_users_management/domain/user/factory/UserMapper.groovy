package cz.acrobits.csp.app_users_management.domain.user.factory


import cz.acrobits.csp.app_users_management.domain.user.model.User
import cz.acrobits.csp.app_users_management.rest.dto.UserCreateDto
import cz.acrobits.csp.app_users_management.rest.dto.UserDto
import cz.acrobits.csp.app_users_management.rest.dto.UserPatchDto
import cz.acrobits.csp.core.common.mapper.CreateMapper
import cz.acrobits.csp.core.common.mapper.ReadMapper
import cz.acrobits.csp.core.common.servicelayer.exceptions.BadRequestException
import cz.acrobits.csp.security.core.UserIdentityProvider
import cz.acrobits.csp.usermanagement.UserRESTInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static cz.acrobits.csp.app_users_management.core.PatchMappingUtils.mapField
import static cz.acrobits.csp.core.common.rest.hateoas.ControllerLinkBuilderExtensions.linkToService
import static cz.acrobits.csp.core.pub.MicroserviceAliases.USER_MANAGEMENT
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn


@Component
class UserMapper implements ReadMapper<User, UserDto>, CreateMapper<User, UserCreateDto> {
//created_by relaci přidělat

    @Autowired
    private UserIdentityProvider userIdentityProvider

    @Override
    UserDto toDto(User user) {
        UserDto dto = new UserDto()

        this.mapCommonFields(user, dto)
        dto.username = user.username
        dto.cloudId = user.cloudId
        dto.email = user.email
        dto.applicationId = user.applicationId
        dto.organizationId = user.organizationId

        dto.add(linkToService(USER_MANAGEMENT, methodOn(UserRESTInterface.class).getUser(user.createdBy), "createdBy"))

        dto
    }

    @Override
    User toEntity(UserCreateDto userCreateDto) {
        User user = new User()

        user.username = userCreateDto.username
        user.cloudId = userCreateDto.cloudId
        user.email = userCreateDto.email
        user.applicationId = userCreateDto.applicationId
        user.organizationId = userCreateDto.organizationId
        user.createdBy = userIdentityProvider.getCurrentIdentity()

        user
    }

    User toEntityFromCsv(String line, String cloudId, Long applicationId, Long organizationId) {
        String[] parts = line.split(",")
        if (!parts.size().equals(2)) {
            throw new BadRequestException("Invalid row: ${line}")
        }

        User user = new User()

        user.username = parts[0]
        user.cloudId = cloudId
        user.email = parts[1]
        user.applicationId = applicationId
        user.organizationId = organizationId
        user.createdBy = userIdentityProvider.getCurrentIdentity()

        user
    }

    void mapPatch(UserPatchDto dto, User user) {
        mapField("username", "username", dto, user)
        mapField("email", "email", dto, user)
    }

}
