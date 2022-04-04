package cz.acrobits.csp.app_users_management.domain.user.job;

import cz.acrobits.csp.app_users_management.domain.user.model.PasswordResetRequest;
import cz.acrobits.csp.app_users_management.domain.user.repository.PasswordResetRequestRepository;
import cz.acrobits.csp.core.common.datetime.CurrentDateTimeProvider;
import cz.acrobits.csp.logging.sentry.SetupSentryContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

@Component
@Log4j2
@ConditionalOnProperty(
        value = "user.resetPasswordExpirationJob.enabled",
        havingValue = "true",
        matchIfMissing = true)
@ManagedResource(objectName = "acrobits.jmx:name=ResetPasswordExpirationJob")
public class ResetPasswordExpirationJob {

    @Autowired
    private CurrentDateTimeProvider currentDateTimeProvider;
    @Autowired
    private PasswordResetRequestRepository passwordResetRequestRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @ManagedOperation(description = "Manually run job")
    @Scheduled(cron = "${user.resetPasswordExpirationJob.cronExpr}")
    @SetupSentryContext
    public void processExpiredRooms() {
        log.info("Starting ResetPasswordExpirationJob");

        boolean shouldRun = true;
        while (shouldRun) {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            shouldRun = ofNullable(transactionTemplate.execute(x -> executeOneRound())).orElse(false);
        }

        log.info("ResetPasswordExpirationJob ended");
    }


    private boolean executeOneRound() {
        LocalDateTime createdWeekAgo = currentDateTimeProvider.getCurrentDateTime().minusDays(7);
        List<PasswordResetRequest> requests = passwordResetRequestRepository.findByCreatedAtBeforeAndDeletedAtIsNull(
                createdWeekAgo,
                PageRequest.of(0, 100)
        );

        if (requests.size() == 0) {
            return false;
        }

        requests.forEach(passwordResetRequestRepository::delete);
        return true;
    }

}
