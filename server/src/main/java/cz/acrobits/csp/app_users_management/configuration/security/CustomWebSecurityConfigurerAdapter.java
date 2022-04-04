package cz.acrobits.csp.app_users_management.configuration.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(1)
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/actuator/**")
                .httpBasic().and()
                .authorizeRequests().antMatchers("/actuator/**").authenticated().and()
                .csrf().disable()
                .headers().disable();
    }
}
