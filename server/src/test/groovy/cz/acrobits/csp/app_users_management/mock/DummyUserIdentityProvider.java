package cz.acrobits.csp.app_users_management.mock;

import cz.acrobits.csp.security.core.IUser;
import cz.acrobits.csp.security.core.Membership;
import cz.acrobits.csp.security.core.User;
import cz.acrobits.csp.security.core.UserIdentityProvider;
import cz.acrobits.csp.usermanagement.security.Role;

import java.util.Arrays;
import java.util.Collections;

public class DummyUserIdentityProvider implements UserIdentityProvider {

    public Long getCurrentIdentity() {

        return 80L;
    }

    @Override
    public IUser getCurrentUser() {
        Membership membership = new Membership();
        membership.setId(1L);
        membership.setOrganizationId(1L);
        membership.setRoles(Collections.singletonList(Role.BACKOFFICE_ADMIN));

        User user = new User();
        user.setId(80L);
        user.setFirstName("John");
        user.setLastName("Brown");
        user.setUsername("johnbrown");
        user.setGlobalRoles(Collections.singletonList(Role.BACKOFFICE_ADMIN));
        user.setOrganizationMemberships(Arrays.asList(membership));

        return user;
    }

    public boolean currentUserIdentityExists() {
        return true;
    }
}
