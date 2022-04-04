package cz.acrobits.csp.app_users_management.permissions;

import cz.acrobits.csp.security.core.permissions.EntityPermissionEvaluator;
import org.springframework.stereotype.Component;

@Component
public class GenericPermissionEvaluator extends EntityPermissionEvaluator {

    public GenericPermissionEvaluator() {
        super(DummyClazz.class);
    }

    @Override
    protected void setup() {

    }


}
