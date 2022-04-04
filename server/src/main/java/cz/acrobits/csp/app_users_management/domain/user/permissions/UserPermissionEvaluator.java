package cz.acrobits.csp.app_users_management.domain.user.permissions;

import cz.acrobits.csp.app_users_management.domain.user.model.User;
import cz.acrobits.csp.app_users_management.service.UserService;
import cz.acrobits.csp.security.core.IUser;
import cz.acrobits.csp.security.core.permissions.EntityPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserPermissionEvaluator extends EntityPermissionEvaluator {

    @Autowired
    private UserService userService;

    public UserPermissionEvaluator() {
        super(User.class);
    }


    @Override
    public boolean canCreate(IUser executingUser, Map<String, Object> permissionsContext) {

        Long organizationId = (Long) permissionsContext.get("organizationId");

        return executingUser.isMemberOfOrganization(organizationId);


    }

    @Override
    public boolean canRead(IUser executingUser, Map<String, Object> permissionsContext) {

        Long userId = (Long) permissionsContext.get("userId");
        User user = userService.findUser(userId);
        return executingUser.hasAnyOfGlobalRoles() || executingUser.isMemberOfOrganization(user.getOrganizationId());

    }

    @Override
    public boolean canDelete(IUser executingUser, Map<String, Object> permissionsContext) {

        Long userId = (Long) permissionsContext.get("userId");
        User user = userService.findUser(userId);
        return executingUser.hasAnyOfGlobalRoles() || executingUser.isMemberOfOrganization(user.getOrganizationId());


        // return hasAnyOfBackofficeRolesWithUpdatePermission(executingUser);
    }

    //private boolean hasAnyOfBackofficeRolesWithUpdatePermission(IUser executingUser) {
    // return executingUser.hasOneOfGlobalRoles(
    //  Role.BACKOFFICE_ADMIN,
    // Role.BACKOFFICE_ORGANIZATION_ACCOUNTANT,
    //  Role.BACKOFFICE_ORGANIZATION_MANAGER,
    //  Role.BACKOFFICE_ORGANIZATION_SALES_MANAGER
    // );
    // }


}
