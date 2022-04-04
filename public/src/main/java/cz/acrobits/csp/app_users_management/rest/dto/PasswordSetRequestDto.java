package cz.acrobits.csp.app_users_management.rest.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PasswordSetRequestDto {
    String token;
    String password;
}
