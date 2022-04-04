package cz.acrobits.csp.app_users_management.configuration.security;

import cz.acrobits.csp.security.core.sysuser.RestTemplateWithSysUser;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

@Configuration
public class SysuserConfiguration {

    @Bean
    @LoadBalanced
    @Primary
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context,
                                                 SpringClientFactory clientFactory) {
        RestTemplateWithSysUser restTemplateWithSysUser = new RestTemplateWithSysUser(resource, context, clientFactory);
        return restTemplateWithSysUser;
    }

}
