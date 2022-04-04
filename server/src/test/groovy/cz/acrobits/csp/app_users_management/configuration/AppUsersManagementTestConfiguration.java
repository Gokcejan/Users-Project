package cz.acrobits.csp.app_users_management.configuration;

import cz.acrobits.csp.app_users_management.UserBuilder;
import cz.acrobits.csp.app_users_management.mock.PersistentCommandSenderMock;
import cz.acrobits.csp.app_users_management.mock.TestingDateTimeProvider;
import cz.acrobits.csp.core.Profiles;
import cz.acrobits.csp.core.common.datetime.CurrentDateTimeProvider;
import cz.acrobits.csp.core.jpa.dbqueue.PersistentCommandSender;
import cz.acrobits.csp.provisioningsdk.ClassPathProvisioningResolver;
import cz.acrobits.csp.provisioningsdk.XmlProvisioningResolver;
import cz.acrobits.csp.security.core.sysuser.RestTemplateWithSysUser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

import java.util.List;

@TestConfiguration
public class AppUsersManagementTestConfiguration {

    static final String AUTOMATED_TEST_APP = "MY_APP";

    @Bean
    @Primary
    public UserBuilder userIdentityProvider() {
        return new UserBuilder();
    }


    @Bean
    @Primary
    CurrentDateTimeProvider currentDateTimeProvider() {
        return new TestingDateTimeProvider();
    }

    @Bean
    @Primary
    PersistentCommandSender persistentCommandSender() {
        return new PersistentCommandSenderMock();
    }

    @Bean
    @Profile(Profiles.TEST)
    RestTemplateWithSysUser restTemplateWithSysUserMock(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context,
                                                        SpringClientFactory clientFactory) {
        return new RestTemplateWithSysUser(resource, context, clientFactory);
    }

    @Bean
    @Primary
    XmlProvisioningResolver xmlProvisioningResolver() {
        return new ClassPathProvisioningResolver(List.of(AUTOMATED_TEST_APP));
    }

}
