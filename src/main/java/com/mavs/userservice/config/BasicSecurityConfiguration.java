package com.mavs.userservice.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

@Configuration
@EnableWebSecurity
public class BasicSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(getAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .httpBasic()
                .and()
                .sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy());
    }

    private SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        SessionFixationProtectionStrategy strategy = new SessionFixationProtectionStrategy();
        strategy.setMigrateSessionAttributes(Boolean.FALSE);

        RegisterSessionAuthenticationStrategy authenticationStrategy = new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());

        return new CompositeSessionAuthenticationStrategy(Lists.newArrayList(strategy, authenticationStrategy));
    }

    private AuthenticationProvider getAuthenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(getEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }

    private PasswordEncoder getEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
