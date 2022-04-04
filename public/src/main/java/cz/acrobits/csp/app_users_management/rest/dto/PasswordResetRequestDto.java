package cz.acrobits.csp.app_users_management.rest.dto;

import cz.acrobits.csp.core.pub.dto.RequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDto extends RequestDto {

    private String email;

}
