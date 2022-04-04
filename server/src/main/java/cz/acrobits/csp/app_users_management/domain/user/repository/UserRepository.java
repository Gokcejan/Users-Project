package cz.acrobits.csp.app_users_management.domain.user.repository;

import cz.acrobits.csp.app_users_management.domain.user.model.User;
import cz.acrobits.csp.core.jpa.repository.JpaSoftDeleteGenericRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaSoftDeleteGenericRepository<User, Long>, JpaSpecificationExecutor<User> {


    Optional<User> findOneById(Long id);
    Optional<User> findOneByEmail(String email);
    Optional<User> findOneByUsernameAndCloudId(String username, String cloudId);




}
