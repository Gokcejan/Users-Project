package cz.acrobits.csp.app_users_management.rest;

import cz.acrobits.csp.app_users_management.rest.dto.PasswordResetRequestDto;
import cz.acrobits.csp.app_users_management.rest.dto.PasswordSetRequestDto;
import cz.acrobits.csp.app_users_management.service.PasswordOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PasswordResetRequestRESTController implements PasswordResetRequestRESTInterface {

    @Autowired
    private PasswordOperations passwordOperations;

    @Override
    public ResponseEntity createPasswordResetRequest(PasswordResetRequestDto passwordResetRequestDto) {

        passwordOperations.createPasswordResetRequest(passwordResetRequestDto);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity createPasswordResetByAdmin(@PathVariable("userId") Long userId) {
        passwordOperations.createPasswordResetRequestByAdmin(userId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity setPassword(PasswordSetRequestDto passwordSet) {

        passwordOperations.setPassword(passwordSet);

        return ResponseEntity.ok().build();
    }


}
