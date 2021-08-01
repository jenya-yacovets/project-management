package com.yacovets.projectmanagement.configuration;

import com.yacovets.projectmanagement.service.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final SecurityService securityService;

    public SecurityConfiguration(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().ignoringAntMatchers("/db/**")
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/db/**",
                        "/assets/**",
                        "/security/registration",
                        "/security/password-recovery",
                        "/security/password-recovery/*",
                        "/security/email-verification/*"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/security/authentication")
                .usernameParameter("emailOrUsername")
                .defaultSuccessUrl("/home", true)
                .permitAll()
                .and().headers().frameOptions().sameOrigin()
                .and()
                .logout()
                .logoutUrl("/security/logout")
                .logoutSuccessUrl("/security/authentication")
                .permitAll();
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(securityService).passwordEncoder(encoder());
    }
}
