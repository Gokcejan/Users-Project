package cz.acrobits.csp.app_users_management.configuration;

import cz.acrobits.csp.core.common.request_scope.JsonRequestBodyProvider;
import cz.acrobits.csp.core.common.validations.ValidationProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationsConfiguration {

    @Autowired
    JsonRequestBodyProvider jsonRequestBodyProvider;
    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public ValidationProcessor createValidationProcessor() {
        return new ValidationProcessor(applicationContext,jsonRequestBodyProvider, "cz.acrobits.csp.app_users_management");
    }
}
