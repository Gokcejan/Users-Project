package cz.acrobits.csp.app_users_management.rest.dto;

import cz.acrobits.csp.core.pub.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

import static java.lang.String.format;

@Getter
@Setter
@Relation(collectionRelation = "users")
public class UserDto extends BaseDto<Long> {
    private static final Object LOCAL_TYPE_NAME = "user";
    private static final String TYPE_NAME = format("%s-%s", "app-users-management", LOCAL_TYPE_NAME);

    private String username;
    private String password;
    private String cloudId;
    private String email;
    private Long applicationId;
    private Long organizationId;


    @Override
    public String provideResourceName() {
        return TYPE_NAME;
    }
}
