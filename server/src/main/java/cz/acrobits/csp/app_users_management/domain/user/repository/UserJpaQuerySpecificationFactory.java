package cz.acrobits.csp.app_users_management.domain.user.repository;

import cz.acrobits.csp.app_users_management.domain.user.model.User;
import cz.acrobits.csp.core.jpa.query.JpaQuerySpecificationFactory;
import org.springframework.stereotype.Service;

@Service
public class UserJpaQuerySpecificationFactory extends JpaQuerySpecificationFactory<User> {
}
