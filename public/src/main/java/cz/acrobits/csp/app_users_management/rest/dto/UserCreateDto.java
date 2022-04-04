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
public class UserCreateDto extends RequestDto {

    private String username;
    private String cloudId;
    private String email;
    private Long applicationId;
    private Long organizationId;



}
