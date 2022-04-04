package cz.acrobits.csp.app_users_management.users

import com.fasterxml.jackson.databind.ObjectMapper
import cz.acrobits.csp.app_users_management.Application
import cz.acrobits.csp.app_users_management.UserBuilder
import cz.acrobits.csp.app_users_management.configuration.AppUsersManagementTestConfiguration
import cz.acrobits.csp.app_users_management.core.CleanUpDb
import cz.acrobits.csp.app_users_management.domain.user.model.PasswordResetRequest
import cz.acrobits.csp.app_users_management.domain.user.repository.PasswordResetRequestRepository
import cz.acrobits.csp.app_users_management.domain.user.repository.UserRepository
import cz.acrobits.csp.app_users_management.mock.TestingDateTimeProvider
import cz.acrobits.csp.app_users_management.rest.dto.UserCreateDto
import cz.acrobits.csp.core.Profiles
import cz.acrobits.csp.core.common.testing.ConstrainedFields
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
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.hypermedia.LinksSnippet
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder

import static cz.acrobits.csp.core.common.testing.DocumentationUtils.*
import static cz.acrobits.csp.core.common.testing.DtoAssertations.assertBasicDtoAttributes
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles(Profiles.TEST)
@DirtiesContext
@ContextConfiguration(classes = [Application, AppUsersManagementTestConfiguration], loader = SpringBootContextLoader)
class AppUsersManagementAPISpec extends WebMockSpec implements DocumentedWebMockSpec, CleanUpDb {

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

    def "Create user"() {
        given:
        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)

        def userBody = defaultUserBody()
        def userBodyMap = toMap(userBody)

        when:


        ResultActions postUserResponse = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody)

        )

        String postUserLocation = extractLocationFromHeader(postUserResponse)
        def postUserResponseBody = extractBodyFromResponseAsMap(postUserResponse)

        then:
        postUserResponse.andExpect(status().isCreated())
        postUserLocation == "http://app-users-management/users/1"

        assertBasicDtoAttributes(postUserResponseBody, "app-users-management-user")
        postUserResponseBody.username == userBodyMap.username
        postUserResponseBody.email == userBodyMap.email
        postUserResponseBody.cloudId == userBodyMap.cloudId
        postUserResponseBody.applicationId == userBodyMap.applicationId
        postUserResponseBody.organizationId == userBodyMap.organizationId

        and:
        ConstrainedFields fields = new ConstrainedFields(UserCreateDto)
        postUserResponse.andDo(document("create-app-user",
                requestFields(
                        fields.withPath("username").description("Username"),
                        fields.withPath("email").description("Email"),
                        fields.withPath("cloudId").description("CloudID in which the user will be created"),
                        fields.withPath("applicationId").description("ID of the Application in which the user will be created"),
                        fields.withPath("organizationId").description("ID of the Organization in which the user will be created"),
                ),
                responseHeaders(headerWithName("Location").description("Created resource url")),
                documentation_userFields(),
                documentation_userLinks()
        ))
    }

    def "Create PasswordResetRequest"() {

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

        def passwordResetRequestBody = defaultPasswordResetRequestBody()

        when:

        ResultActions postPasswordResetRequestResponse = mockMvc.perform(
                post("/users/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordResetRequestBody)

        )
        then:
        postPasswordResetRequestResponse.andExpect(status().isOk())
    }

    def "Password set "() {
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
    }

    def "Get user"() {
        given:

        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)

        def userBody = defaultUserBody()
        def userBodyMap = toMap(userBody)


        ResultActions postUserResponse = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody)
        )

        String postUserLocation = extractLocationFromHeader(postUserResponse)
        def postUserResponseBody = extractBodyFromResponseAsMap(postUserResponse)

        when:

        def getUserResponse = mockMvc.perform(get(postUserLocation))
        def getUserResponseBody = extractBodyFromResponseAsMap(getUserResponse)

        then:

        getUserResponse.andExpect(status().isOk())
        assertBasicDtoAttributes(postUserResponseBody, "app-users-management-user")
        getUserResponseBody.username == userBodyMap.username
        postUserResponseBody.email == userBodyMap.email
        getUserResponseBody.cloudId == userBodyMap.cloudId
        getUserResponseBody.applicationId == userBodyMap.applicationId
        getUserResponseBody.organizationId == userBodyMap.organizationId

    }

    def "Get users collection"() {
        given:

        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)


        for (int i = 1; i < 11; i++) {
            def userBody = defaultUserBody()
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userBody)
            )
        }

        when:
        def getUserResponse = mockMvc.perform(get("/users?size=7&page=1&applicationId=111"))
        def getUserResponseMap = extractBodyFromResponseAsMap(getUserResponse)

        then:
        getUserResponse.andExpect(status().isOk())
        getUserResponseMap._page.size == 7
        getUserResponseMap._page.totalElements == 10
        getUserResponseMap._page.totalPages == 2
        getUserResponseMap._page.number == 1
        getUserResponseMap._embedded.users.size == 3


    }

    def "Get users collection different applicationId"() {
        given:

        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)


        for (int i = 1; i < 7; i++) {
            def userBody = defaultUserBody()
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userBody)
            )
        }
        for (int i = 1; i < 7; i++) {
            def userBody = applicationUserBody()
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userBody)
            )
        }

        when:
        def getUserResponse = mockMvc.perform(get("/users?size=3&page=0&applicationId=333"))
        def getUserResponseMap = extractBodyFromResponseAsMap(getUserResponse)

        then:
        getUserResponse.andExpect(status().isOk())
        getUserResponseMap._page.size == 3
        getUserResponseMap._page.totalElements == 6
        getUserResponseMap._page.totalPages == 2
        getUserResponseMap._page.number == 0
        getUserResponseMap._embedded.users.size == 3


    }

    def "Delete user"() {

        given:
        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)

        def userBody = defaultUserBody()

        ResultActions postUserResponse = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody)
        )
        String postUserLocation = extractLocationFromHeader(postUserResponse)

        when:
        ResultActions deleteUserResponse = mockMvc.perform(
                delete(postUserLocation)
        )
        then:
        deleteUserResponse.andExpect(status().isNoContent())


    }

    def "Patch user"() {

        given:

        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)

        def userBody = defaultUserBody()
        def patchUserBody = """
            {
                "username": "Jan"
            }
        """

        ResultActions postUserResponse = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody)
        )
        String postUserLocation = extractLocationFromHeader(postUserResponse)
        def postUserResponseBody = extractBodyFromResponseAsMap(postUserResponse)

        when:
        def patchUserResponse = mockMvc.perform(
                patch(postUserLocation)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchUserBody)
        )

        def patchUserResponseBody = extractBodyFromResponseAsMap(patchUserResponse)

        then:
        patchUserResponse.andExpect(status().isOk())
        postUserResponseBody.username != patchUserResponseBody.username

    }

    def "Authenticate user"() {
        given:

        IUser user = new UserBuilder()
                .with(Role.CLIENT_PROVIDER, 222)
                .build()

        securityContextSetter.setupCurrentUser(user)

        def userBody = defaultUserBody()
        def authenticateUserBody = """
            {
                "username": "Franta",
                "email": "pokus@pokus.cz",
                "cloudId": "MY_APP",
                "applicationId": 111,
                "organizationId": 222
            }
        """
        def authenticateUserBodyWrongUsername = """
            {
                "username": "Jan",
                "email": "pokus@pokus.cz",
                "cloudId": "MY_APP"
            }
        """
        def authenticateUserBodyWrongEmail = """
            {
                "username": "Franta",
                "email": "jan@jan.cz",
                "cloudId": "MY_APP"
            }
        """

        def authenticateUserBodyDifferentCloudId = """
            {
                "username": "Franta",
                "email": "pokus@pokus.cz",
                "cloudId": "prima-123"
            }
        """

        def authenticateUserBodyAsteriskCloudId = """
            {
        "username": "Franta",
        "email": "pokus@pokus.cz",
        "cloudId": "MY_APP*"
            }
        """


        ResultActions postUserResponse = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody)


        )


        when:

        ResultActions authenticateUserResponse = mockMvc.perform(
                post("/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticateUserBody)


        )


        then:
        authenticateUserResponse.andExpect(status().isOk())

        when:

        ResultActions authenticateUserResponseWrongUsername = mockMvc.perform(
                post("/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticateUserBodyWrongUsername)


        )


        then:
        authenticateUserResponseWrongUsername.andExpect(status().isUnauthorized())

        when:

        ResultActions authenticateUserResponseWrongPassword = mockMvc.perform(
                post("/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticateUserBodyWrongEmail)


        )


        then:
        authenticateUserResponseWrongPassword.andExpect(status().isUnauthorized())

        when:
        ResultActions authenticateUserResponseDifferentCloudId = mockMvc.perform(
                post("/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticateUserBodyDifferentCloudId)
        )

        then:
        authenticateUserResponseDifferentCloudId.andExpect(status().isUnauthorized())

        when:

        ResultActions authenticateUserResponseWithAsterisk = mockMvc.perform(
                post("/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticateUserBodyAsteriskCloudId)


        )

        then:
        authenticateUserResponseWithAsterisk.andExpect(status().isOk())


    }

    def "Import users"() {
        given: "Prepare data"
        String johnUsername = "john.doe@example.com"
        String johnEmail = "john.doe@example.com"
        String georgeUsername = "george"
        String georgeEmail = "george.smith@example.com"

        and: "Prepare request"
        String cloudId = "MYCLOUDID"
        Long applicationId = 2L
        Long organizationId = 3L
        String content =
                "username,email\n" +
                        "${johnUsername},${johnEmail}\n" +
                        "${georgeUsername},${georgeEmail}"
        MockMultipartFile file = new MockMultipartFile("file", "users.csv", "application/csv", content.getBytes())


        when:
        ResultActions postFileResponse = mockMvc.perform(
                fileUpload("/users/import")
                        .file(file)
                        .param("cloudId", cloudId)
                        .param("applicationId", applicationId.toString())
                        .param("organizationId", organizationId.toString())
        )

        then:
        postFileResponse.andExpect(status().isNoContent())
        def john = userRepository.findOneByEmail(johnEmail).get()
        john.username == johnUsername
        john.email == johnEmail
        john.cloudId == cloudId
        john.applicationId == applicationId
        john.organizationId == organizationId
        def george = userRepository.findOneByEmail(georgeEmail).get()
        george.username == georgeUsername
        george.email == georgeEmail
        george.cloudId == cloudId
        george.applicationId == applicationId
        george.organizationId == organizationId

        and:
        postFileResponse.andDo(document("import-app-users"))
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

    private ResponseFieldsSnippet documentation_userFields() {
        return withFields(
                commonDtoFields(),
                fieldWithPath("username").description("Username"),
                fieldWithPath("email").description("Email"),
                fieldWithPath("cloudId").description("CloudID in which the user will be created"),
                fieldWithPath("applicationId").description("ID of the Application in which the user will be created"),
                fieldWithPath("organizationId").description("ID of the Organization in which the user will be created"),
                fieldWithPath('_relations.createdBy.id').description("User who created the resource."),
                fieldWithPath('_relations.createdBy.cls').description(RELATION_CLS_DESCRIPTION),
                fieldWithPath('_relations.createdBy.type').description(RELATION_TYPE_DESCRIPTION),
        )
    }

    private LinksSnippet documentation_userLinks() {
        return withLinks(
                linkWithRel("createdBy").optional().description("Link to the user who is crated the app user"),
        )
    }

}