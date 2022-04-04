package cz.acrobits.csp.app_users_management.configuration;

import cz.acrobits.csp.app_users_management.provisioning.AppProvisioningDto;
import cz.acrobits.csp.app_users_management.provisioning.AppProvisioningMapper;
import cz.acrobits.csp.provisioningsdk.HttpProvisioningResolver;
import cz.acrobits.csp.provisioningsdk.ProvisioningProvider;
import cz.acrobits.csp.provisioningsdk.XmlProvisioningResolver;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProvisioningConfiguration {

    @Value("${csp.provisioning_url}")
    private String provisioningUrl;
    @Autowired
    private CloseableHttpClient httpClient;

    @Bean
    public XmlProvisioningResolver xmlProvisioningResolver() {
        return new HttpProvisioningResolver(provisioningUrl, httpClient);
    }

    @Bean
    public ProvisioningProvider<AppProvisioningDto> provisioningProvider(XmlProvisioningResolver xmlProvisioningResolver) {
        return new ProvisioningProvider<>(
                xmlProvisioningResolver,
                new AppProvisioningMapper()
        );
    }

}
