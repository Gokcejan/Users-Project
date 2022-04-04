package cz.acrobits.csp.app_users_management.domain.user.factory

import cz.acrobits.csp.app_users_management.domain.user.model.PasswordResetRequest
import cz.acrobits.csp.app_users_management.domain.user.model.User
import cz.acrobits.csp.core.common.utils.UniqueIdGenerator
import org.springframework.stereotype.Component

@Component
class PasswordResetRequestMapper {


    PasswordResetRequest toEntity(User user) {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest()

        passwordResetRequest.user = user
        passwordResetRequest.email = user.email
        passwordResetRequest.token = UniqueIdGenerator.uuid()

        passwordResetRequest
    }
}
