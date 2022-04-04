package cz.acrobits.csp.app_users_management.domain.mailing


import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class EmailTemplates {

    @Value('${csp.integration.linkupDomain}')
    private String linkupDomain

    private static recipientPlaceholder = "-x=recipient=x-"

    private static issueDescriptionPlaceholder = "-x=issue-description=x-"

    private static commentTextPlaceholder = "-x=comment-text=x-"

    private static String commentTextPlaceholder(Long id) {
        return "-x=comment-text-${id}=x-";
    }

    String passwordResetRequestEmailSubject(String appName) {
        """${appName} - Password reset request"""
    }

    String passwordResetRequestEmailText(String appName, String appSubdomain, String token) {
        """
Dear customer,
<br/>
based on the request to reset your password we are sending you this email. [Follow this link to set your new password](https://${appSubdomain}.${linkupDomain}/users/password-reset?token=${token})
<br/>
Best regards

Your ${appName} team
"""
    }
}


