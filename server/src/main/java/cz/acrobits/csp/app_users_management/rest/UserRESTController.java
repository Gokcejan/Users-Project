package cz.acrobits.csp.app_users_management.rest;

import cz.acrobits.csp.app_users_management.rest.dto.UserAuthenticateDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserCreateDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserPatchDto;
import cz.acrobits.csp.app_users_management.service.UserService;
import cz.acrobits.csp.core.common.rest.pagination.ServiceDiscoveryPagedResourcesAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Map;

import static cz.acrobits.csp.core.common.rest.hateoas.ControllerLinkBuilderExtensions.resourceLocation;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
public class UserRESTController implements UserRESTInterface {

    @Autowired
    private UserService userService;
    @Autowired
    private ServiceDiscoveryPagedResourcesAssembler<UserDto> pageAssembler;

    @Override
    public ResponseEntity createUser(UserCreateDto createDto) {
        UserDto user = userService.createUser(createDto);
        URI location = resourceLocation("app-users-management",
                methodOn(UserRESTController.class).getUser(user.getIdentity().getId()));
        return ResponseEntity.created(location).body(user);
    }

    @Override
    public ResponseEntity<UserDto> getUser(@PathVariable("userId") Long userId) {
        UserDto userDto = userService.getUser(userId);
        return ResponseEntity.ok(userDto);
    }

    @Override
    public PagedModel getUsers(@RequestParam Map<String, String> queryParams) {
        Page<UserDto> usersPage = userService.getUsersPage(queryParams);
        return pageAssembler.toResource(usersPage);
    }

    @Override
    public ResponseEntity deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity patchUser(@PathVariable Long userId, @RequestBody UserPatchDto dto) {

        UserDto userDto = userService.patchIssue(userId, dto);
        return ResponseEntity.ok(userDto);
    }

    @Override
    public ResponseEntity authenticateUser(UserAuthenticateDto authenticateDto) {

        if (userService.authenticateUser(authenticateDto)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @Override
    public ResponseEntity importUsers(MultipartFile file, String cloudId, Long applicationId, Long organizationId) {
        userService.importUsers(file, cloudId, applicationId, organizationId);
        return ResponseEntity.noContent().build();
    }

}
