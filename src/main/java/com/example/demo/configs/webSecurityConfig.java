package com.example.demo.configs;

import com.example.demo.filters.JwtAuthfilter;
import com.example.demo.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class webSecurityConfig {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtAuthfilter jwtAuthfilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws  Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws  Exception{
        httpSecurity
            .authorizeHttpRequests(
               auth ->auth
                   .requestMatchers(
                           HttpMethod.GET,
                           "/", "/blogPost/allblogs", "/blogPost/{id:[0-9]+}",
                           "/blogPost/", "/auth/**"
                   ).permitAll()
                   .requestMatchers(HttpMethod.POST,
                           "/comments/addComment/**"
                   ).permitAll()
                   .requestMatchers("/auth/**").permitAll()
                   .requestMatchers(
                           "/blogPost/blogCreationForm",//user and admin can update , create, delte post
                           "/blogPost/newPost",
                           "/blogPost/updatePostForm/**",
                           "/blogPost/update",
                           "/blogPost/deletePost/**"
                   ).hasAnyRole("USER", "ADMIN")
                   .requestMatchers("/comments/updateComment",
                           "/comments/deleteComment"
                   ).authenticated()//needed acc to delete , update comment
                   .requestMatchers("/error").permitAll()



                    //rest routes
                   .requestMatchers(HttpMethod.POST,
                           "/api/auth/login",
                           "/api/auth/signup"
                    ).permitAll()
                   .requestMatchers(HttpMethod.GET,
                           "/api/blogPost/allblogs",
                           "/api/blogPost/{id:[0-9]+}"
                   ).permitAll()
                   .requestMatchers(HttpMethod.POST,
                           "/api/comments/addComment/**"
                   ).permitAll()


                   .requestMatchers(HttpMethod.POST,
                           "/api/blogPost/newPost"
                   ).hasAnyRole("USER", "ADMIN")
                   .requestMatchers(HttpMethod.PUT,
                           "/api/blogPost/update",
                           "/api/comments/updateComment"
                   ).hasAnyRole("USER", "ADMIN")
                   .requestMatchers(HttpMethod.DELETE,
                           "/api/blogPost/deletePost/**",
                           "/api/comments/deleteComment"
                   ).hasAnyRole("USER", "ADMIN")
                   .requestMatchers("/api/auth/me").authenticated()

                   .anyRequest().authenticated()
            )
            .formLogin(
                form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")

                .defaultSuccessUrl("/blogPost/allblogs", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(
                logout->logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .sessionManagement(
                session ->session
                .maximumSessions(1)
            )
            //rest
            .addFilterBefore(jwtAuthfilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(
                 csrf->csrf
                         .ignoringRequestMatchers("/api/**")
            );

        return httpSecurity.build();
    }

    //we can give authenticationProvider; like daoauthentiction provider and customise it
}
//csrf, auth