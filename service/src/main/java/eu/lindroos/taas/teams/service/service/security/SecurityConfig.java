package eu.lindroos.taas.teams.service.service.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Created by Andreas on 13.4.2019
 * This is a security config for dev envs. Opens everything, but locks down known prod controllers.
 */
@EnableWebSecurity
@Configuration
@Log4j2
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenAuthFilter jwtTokenAuthFilter;

    public SecurityConfig(JwtTokenAuthFilter jwtTokenAuthFilter) {
        this.jwtTokenAuthFilter = jwtTokenAuthFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Loading security configs");
        http
                .addFilterBefore(jwtTokenAuthFilter, BasicAuthenticationFilter.class)
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .headers().frameOptions().sameOrigin() // This is for H2, consider disabling for prod        
        ;
    }
}
