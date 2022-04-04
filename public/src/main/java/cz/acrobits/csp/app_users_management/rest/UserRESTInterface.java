package cz.acrobits.csp.app_users_management.rest;

import cz.acrobits.csp.app_users_management.rest.dto.UserAuthenticateDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserCreateDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserDto;
import cz.acrobits.csp.app_users_management.rest.dto.UserPatchDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface UserRESTInterface {

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    ResponseEntity createUser(@RequestBody UserCreateDto createDto);

    @RequestMapping(method = RequestMethod.GET, value = "/users/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable("userId") Long userId);

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    PagedModel<EntityModel<UserDto>> getUsers(@RequestParam Map<String, String> queryParams);

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
    ResponseEntity deleteUser(@PathVariable("userId") Long userId);

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PATCH)
    ResponseEntity patchUser(@PathVariable("userId") Long userId, @RequestBody UserPatchDto dto);

    @RequestMapping(value = "/users/authenticate", method = RequestMethod.POST)
    ResponseEntity authenticateUser(@RequestBody UserAuthenticateDto authenticateDto);

    @RequestMapping(value = "/users/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.POST)
    ResponseEntity importUsers(@RequestBody MultipartFile file,
                               @RequestParam("cloudId") String cloudId,
                               @RequestParam("applicationId") Long applicationId,
                               @RequestParam("organizationId") Long organizationId);

}
