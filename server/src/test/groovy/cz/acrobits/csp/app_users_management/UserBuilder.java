package cz.acrobits.csp.app_users_management;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.acrobits.csp.security.core.IUser;
import cz.acrobits.csp.security.core.Membership;
import cz.acrobits.csp.usermanagement.security.Role;
import cz.acrobits.csp.security.core.User;

public class UserBuilder {


    private Long currentUserId = 80L;
    private Role currentUserGlobalRole = null;
    private Role currentUserMembershipRole = Role.CLIENT_EMPLOYEE;
    private Long currentUserMembershipOrganization = 1L;

    public IUser build() {

        Membership membership = new Membership();
        membership.setId(1L);
        membership.setOrganizationId(currentUserMembershipOrganization);
        List<Role> membershipRoles = new ArrayList<>();
        if (currentUserMembershipRole != null) {
            membershipRoles.add(currentUserMembershipRole);
        }
        membership.setRoles(membershipRoles);

        User user = new User();
        user.setId(currentUserId);
        user.setFirstName("John");
        user.setLastName("Brown");
        user.setUsername("userB");
        List<Role> roles = new ArrayList<>();
        if (currentUserGlobalRole != null) {
            roles.add(currentUserGlobalRole);
        }
        user.setGlobalRoles(roles);
        user.setOrganizationMemberships(Collections.singletonList(membership));

        return user;
    }


    public UserBuilder with(Long userId, Role globalRole, Role membershipRole, Long organizationId) {
        currentUserId = userId;
        currentUserGlobalRole = globalRole;
        currentUserMembershipRole = membershipRole;
        currentUserMembershipOrganization = organizationId;
        return this;
    }

    public UserBuilder with(Role membershipRole, Long organizationId) {
        currentUserMembershipRole = membershipRole;
        currentUserMembershipOrganization = organizationId;
        return this;
    }

    public UserBuilder with(Long userId, Role globalRole) {
        currentUserId = userId;
        currentUserGlobalRole = globalRole;
        return this;
    }

    public UserBuilder withId(Long userId) {
        currentUserId = userId;
        return this;
    }

    public UserBuilder setupCurrentUser(Role globalRole) {
        currentUserGlobalRole = globalRole;
        return this;
    }
}
