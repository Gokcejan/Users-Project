package cz.acrobits.csp.app_users_management.rest.dto;

import cz.acrobits.csp.core.pub.dto.PatchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPatchDto extends PatchDto {

    private String username;
    private String email;
}
