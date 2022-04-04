package cz.acrobits.csp.app_users_management.domain.mailing;

import cz.acrobits.csp.core.jpa.dbqueue.PersistentCommandQueue;
import cz.acrobits.csp.core.pub.MicroserviceAliases;
import cz.acrobits.csp.mailer.command.mail.request.CreateEmailCommand;
import cz.acrobits.csp.mailer.command.mail.request.CreateUnprocessedEmailCommand;
import cz.acrobits.csp.mailer.sdk.service.MailerSdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailerService {

    @Autowired
    private PersistentCommandQueue persistentCommandQueue;

    @Autowired
    private MailerSdk mailerSdk;

    public void sendEmail(CreateUnprocessedEmailCommand unprocessedEmailCommand) {
        CreateEmailCommand emailCommand = mailerSdk.processEmail(unprocessedEmailCommand);
        persistentCommandQueue.sendCommand(emailCommand, MicroserviceAliases.MAILER);
    }

}
