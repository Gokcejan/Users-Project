package cz.acrobits.csp.app_users_management.domain.user.repository;

import cz.acrobits.csp.app_users_management.domain.user.model.User;
import cz.acrobits.csp.core.jpa.query.UrlToJpaQueryMapper;
import org.springframework.stereotype.Component;

@Component
public class UserUrlToJpaQueryMapper extends UrlToJpaQueryMapper<User> {

    public UserUrlToJpaQueryMapper() {

        registerMapping("applicationId", Long::valueOf,
                (query, root, cb, paramSet) -> root.get("applicationId").in(paramSet));

        registerMapping("username",
                (query, root, cb, paramSet) -> root.get("username").in(paramSet));
    }
}
