package cz.acrobits.csp.app_users_management;

import cz.acrobits.csp.core.Profiles;
import cz.acrobits.csp.core.common.CspCoreCommonPackageDefiningType;
import cz.acrobits.csp.core.jpa.CspCoreJpaPackageDefiningType;
import cz.acrobits.csp.core.queue.CspCoreQueuePackageDefiningType;
import cz.acrobits.csp.mailer.sdk.CspMailerSdkPackageDefiningType;
import cz.acrobits.csp.security.core.SecurityCorePackageDefiningType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackageClasses = {Application.class,
        CspCoreCommonPackageDefiningType.class,
        CspCoreJpaPackageDefiningType.class,
        SecurityCorePackageDefiningType.class,
        CspCoreQueuePackageDefiningType.class,
        CspMailerSdkPackageDefiningType.class})
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class Application {
    public static void main(String[] args) {
        String property = System.getProperties().getProperty("spring.profiles.active");
        if (property == null) {
            System.setProperty("spring.profiles.active", Profiles.PROD);
        }
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }
}