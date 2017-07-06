package com.example.urlshortener.main;

import com.example.urlshortener.dao.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class UrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApplication.class, args);
    }
}

@EnableWebSecurity
@AllArgsConstructor
@ComponentScan(basePackages = "com.example.urlshortener")
@Slf4j
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private AccountRepository accountRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return accountId -> {
            log.info("user with account id: {}, trying to access", accountId);
            val optionalAccount = accountRepository.findById(accountId);
            return optionalAccount
                    .map(account -> {
                        log.info("account id: {} was found, user successfully authenticated", account.getId());
                        return new User(
                                account.getId(),
                                account.getPassword(),
                                true,
                                true,
                                true,
                                true,
                                AuthorityUtils.createAuthorityList("USER"));
                    }).orElseThrow(() -> {
                        log.info("account id: {} was not found, user authentication failed", accountId);
                        return new UsernameNotFoundException(accountId + " not found");
                    });
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/register")
                .authenticated()
                .antMatchers("/statistics")
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable();
    }
}