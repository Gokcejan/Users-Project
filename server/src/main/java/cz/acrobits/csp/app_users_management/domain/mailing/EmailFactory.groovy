package cz.acrobits.csp.app_users_management.domain.mailing

import cz.acrobits.csp.app_users_management.domain.user.model.PasswordResetRequest
import cz.acrobits.csp.app_users_management.domain.user.model.User
import cz.acrobits.csp.app_users_management.provisioning.AppProvisioningDto
import cz.acrobits.csp.mailer.command.mail.dto.RecipientDto
import cz.acrobits.csp.mailer.command.mail.request.CreateUnprocessedEmailCommand
import cz.acrobits.csp.provisioningsdk.ProvisioningProvider
import cz.acrobits.csp.security.core.IUser
import cz.acrobits.csp.security.core.UserIdentityProvider
import cz.acrobits.csp.usermanagement.dto.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@Component
class EmailFactory {

    @Autowired
    private UserIdentityProvider userIdentityProvider
    @Autowired
    private EmailTemplates templates
    @Autowired
    private ProvisioningProvider<AppProvisioningDto> provisioningProvider;

    CreateUnprocessedEmailCommand createResetPasswordEmailCommand(PasswordResetRequest passwordResetRequest) {

        AppProvisioningDto provisioning = provisioningProvider.getProvisioning(passwordResetRequest.user.cloudId);

        CreateUnprocessedEmailCommand email = new CreateUnprocessedEmailCommand()
        String appName = provisioning.name
        String subdomain = provisioning.visibleUrlSubdomain
        email.subject = templates.passwordResetRequestEmailSubject(appName);
        email.contentMarkdown = templates
                .passwordResetRequestEmailText(appName, subdomain, passwordResetRequest.getToken())

        email.type = "app-user-password-reset"
        email.version = 1L
        email.recipients = mapRecipientsToDtos(passwordResetRequest.getUser().getEmail(), passwordResetRequest.getUser().getUsername())
        return email
    }


    private static List<RecipientDto> mapRecipientsToDtos(String email, String name) {
        return Arrays.asList(new RecipientDto(email, name, null));
    }


}
