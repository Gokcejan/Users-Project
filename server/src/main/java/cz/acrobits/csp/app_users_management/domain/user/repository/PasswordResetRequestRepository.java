package cz.acrobits.csp.app_users_management.domain.user.repository;

import cz.acrobits.csp.app_users_management.domain.user.model.PasswordResetRequest;
import cz.acrobits.csp.core.jpa.repository.JpaSoftDeleteGenericRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetRequestRepository extends JpaSoftDeleteGenericRepository<PasswordResetRequest, Long>,
        JpaSpecificationExecutor<PasswordResetRequest> {

    Optional<PasswordResetRequest> findOneByEmail(String email);

    Optional<PasswordResetRequest> findOneByToken(String token);

    List<PasswordResetRequest> findByCreatedAtBeforeAndDeletedAtIsNull(LocalDateTime time, Pageable pageable);

}
