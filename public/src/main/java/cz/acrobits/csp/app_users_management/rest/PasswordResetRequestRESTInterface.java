package cz.acrobits.csp.app_users_management.rest;

import cz.acrobits.csp.app_users_management.rest.dto.PasswordResetRequestDto;

import cz.acrobits.csp.app_users_management.rest.dto.PasswordSetRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface PasswordResetRequestRESTInterface {

    @RequestMapping(value = "/users/password-reset", method = RequestMethod.POST)
    ResponseEntity createPasswordResetRequest(@RequestBody PasswordResetRequestDto createDto);

    @RequestMapping(value = "/users/{userId}/password-reset", method = RequestMethod.POST)
    ResponseEntity createPasswordResetByAdmin(@PathVariable("userId") Long userId);

    @RequestMapping(value = "/users/password-set", method = RequestMethod.POST)
    ResponseEntity setPassword(@RequestBody PasswordSetRequestDto passwordSet);

}
