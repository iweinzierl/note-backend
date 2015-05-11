package de.inselhome.noteapp.configuration;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import de.inselhome.noteapp.security.JsonFileUserDetailsService;

/**
 * @author  iweinzierl
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JsonFileUserDetailsService userDetailsService;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        //J-
        http
            .userDetailsService(userDetailsService)
            .authorizeRequests()
                .antMatchers("/app/styles/**").permitAll()
                .antMatchers("/libs/bootstrap/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
            .csrf()
                .disable()
            .httpBasic();
        //J+
    }
}
