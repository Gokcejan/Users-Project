package cz.acrobits.csp.app_users_management.users

import com.fasterxml.jackson.databind.ObjectMapper
import cz.acrobits.csp.app_users_management.Application
import cz.acrobits.csp.app_users_management.UserBuilder
import cz.acrobits.csp.app_users_management.configuration.AppUsersManagementTestConfiguration
import cz.acrobits.csp.app_users_management.core.CleanUpDb
import cz.acrobits.csp.app_users_management.domain.user.model.PasswordResetRequest
import cz.acrobits.csp.app_users_management.domain.user.model.User
import cz.acrobits.csp.app_users_management.domain.user.repository.PasswordResetRequestRepository
import cz.acrobits.csp.app_users_management.domain.user.repository.UserRepository
import cz.acrobits.csp.app_users_management.mock.TestingDateTimeProvider
import cz.acrobits.csp.core.Profiles
import cz.acrobits.csp.core.common.testing.DocumentedWebMockSpec
import cz.acrobits.csp.core.common.testing.WebMockSpec
import cz.acrobits.csp.core.jpa.dbqueue.PersistentCommandQueue
import cz.acrobits.csp.core.jpa.dbqueue.PersistentCommandRepository
import cz.acrobits.csp.security.core.IUser
import cz.acrobits.csp.security.core.SecurityContextSetter
import cz.acrobits.csp.usermanagement.security.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder

import static cz.acrobits.csp.core.common.testing.DtoAssertations.assertBasicDtoAttributes
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles(Profiles.TEST)
@DirtiesContext
@ContextConfiguration(classes = [Application, AppUsersManagementTestConfiguration], loader = SpringBootContextLoader)
class AppUsersManagementIntegrationSpec extends WebMockSpec implements DocumentedWebMockSpec, CleanUpDb {

    @Value('${spring.application.name}')
    private String springApplicationName

    @Autowired
    private UserBuilder userIdentityProvider
    @Autowired
    private PasswordResetRequestRepository passwordResetRequestRepository
    @Autowired
    private PersistentCommandRepository persistentCommandRepository
    @Autowired
    private SecurityContextSetter securityContextSetter
    @Autowired
    private UserRepository userRepository
    @Autowired
    private TestingDateTimeProvider dateTimeProvider
    @Autowired
    protected PersistentCommandQueue commandQueue
    @Autowired
    protected ObjectMapper objectMapper

    @Override
    AbstractMockMvcBuilder setupMockMvc(AbstractMockMvcBuilder builder) {
        applyDocumentedSetupWithStubs(builder, this.specificationContext, springApplicationName, "")
        return builder
    }

    def setup() {
        securityContextSetter.setupCurrentUser(new UserBuilder().build())
    }

    def cleanup() {
        cleanUpDb()
    }

    def "Password reset flow initiated by user"() {
        given:
        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)

        def userBody = defaultUserBody()

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userBody)
        )

        mockMvc.perform(post("/users/password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(defaultPasswordResetRequestBody())
        )

        PasswordResetRequest passwordResetRequest = passwordResetRequestRepository.findAll().first()

        def setPasswordBody = """
            {
                "password": "123456",
                "token": "${passwordResetRequest.getToken()}"
            }
        """

        when:
        ResultActions postPasswordSetResponse = mockMvc.perform(
                post("/users/password-set")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(setPasswordBody)
        )

        then:
        postPasswordSetResponse.andExpect(status().isOk())

        when:
        User userWithNewPassword = userRepository.findAll().first()

        then:
        userWithNewPassword.password == "123456"
        passwordResetRequestRepository.findAll().isEmpty()
    }

    def "Password reset flow initiated by admin"() {
        given:
        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)

        def userBody = defaultUserBody()

        ResultActions postUserResponse = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userBody)
        )

        def postUserResponseBody = extractBodyFromResponseAsMap(postUserResponse)

        mockMvc.perform(post("/users/${postUserResponseBody._identity.id}/password-reset"))

        PasswordResetRequest passwordResetRequest = passwordResetRequestRepository.findAll().first()

        def setPasswordBody = """
            {
                "password": "123456",
                "token": "${passwordResetRequest.getToken()}"
            }
        """

        when:
        ResultActions postPasswordSetResponse = mockMvc.perform(
                post("/users/password-set")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(setPasswordBody)
        )

        then:
        postPasswordSetResponse.andExpect(status().isOk())

        when:
        User userWithNewPassword = userRepository.findAll().first()

        then:
        userWithNewPassword.password == "123456"
        passwordResetRequestRepository.findAll().isEmpty()
    }

    String defaultUserBody() {
        """
            {
                "username": "Franta",
                "email": "pokus@pokus.cz",
                "cloudId": "MY_APP",
                "applicationId": 111,
                "organizationId": 222
                                 
            }
        """
    }

    String applicationUserBody() {
        """
            {
                "username": "Pepa",
                "email": "nepokus@nepokus.cz",
                "cloudId": "prima-123",
                "applicationId": 333,
                "organizationId": 222
                                 
            }
        """
    }

    String defaultPasswordResetRequestBody() {
        """
            {
                "email": "pokus@pokus.cz"
            }
        """
    }
}