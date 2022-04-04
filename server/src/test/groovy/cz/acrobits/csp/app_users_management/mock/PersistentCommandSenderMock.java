package cz.acrobits.csp.app_users_management.mock;

import cz.acrobits.csp.core.jpa.dbqueue.PersistentCommandDto;
import cz.acrobits.csp.core.jpa.dbqueue.PersistentCommandSender;

public class PersistentCommandSenderMock implements PersistentCommandSender {


    public void send(PersistentCommandDto persistentCommand, String destinationService) {

    }
}
