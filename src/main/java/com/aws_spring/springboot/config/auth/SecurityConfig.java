package com.aws_spring.springboot.config.auth;

import com.aws_spring.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig  {

    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeHttpRequests( request -> request
                        .antMatchers("/", "/css/**", "/imges/**", "/js/**", "/h2-console/**", "profile").permitAll()
                        .antMatchers("/api/v1/**").hasAnyRole(Role.USER.name())
                        .anyRequest().authenticated())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(login -> login.userInfoEndpoint().userService(customOAuth2UserService));

        return http.build();
    }
}
