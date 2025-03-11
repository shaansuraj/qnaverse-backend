// package com.qnaverse.QnAverse.config;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import com.qnaverse.QnAverse.repositories.UserRepository;
// import com.qnaverse.QnAverse.services.CustomUserDetailsService;

// @Configuration
// public class SecurityConfig {

//     @Autowired
//     private JwtAuthFilter jwtAuthFilter; // <--- We autowire our new filter

//     /**
//      * Provide a UserDetailsService bean that needs a UserRepository.
//      */
//     @Bean
//     public UserDetailsService userDetailsService(UserRepository userRepository) {
//         return new CustomUserDetailsService(userRepository);
//     }

//     /**
//      * Provide AuthenticationProvider, using the custom UserDetailsService.
//      */
//     @Bean
//     public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
//         DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//         provider.setUserDetailsService(userDetailsService);
//         provider.setPasswordEncoder(passwordEncoder());
//         return provider;
//     }

//     /**
//      * Build the AuthenticationManager from config
//      */
//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//         return config.getAuthenticationManager();
//     }

//     /**
//      * Define the Security filter chain.
//      */
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider provider) throws Exception {

//         http
//         .csrf(csrf -> csrf
//         .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) 
//          )
//             .cors(Customizer.withDefaults())
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authenticationProvider(provider)
//             .authorizeHttpRequests(auth -> auth
//                 // Public endpoints
//                 .requestMatchers("/api/auth/register").permitAll()
//                 .requestMatchers("/api/auth/login").permitAll()

           

//                 // Admin endpoints
//                 .requestMatchers("/api/admin/**").hasAuthority("admin")
//                 .requestMatchers("/api/question/approve/**").hasAuthority("admin")

//                 // Authenticated endpoints
//                 .requestMatchers("/api/block/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/search/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/question/trending/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/follow/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/notifications/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/user/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/like/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/saved/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/question/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/answer/**").hasAnyAuthority("user","admin")
//                 .requestMatchers("/api/report/**").hasAnyAuthority("user","admin")

//                 // Fallback
//                 .anyRequest().permitAll()
//             );

//         // <-- Add the JwtAuthFilter in the chain BEFORE UsernamePasswordAuthenticationFilter
//         http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }

//     /**
//      * Provide a BCrypt password encoder bean.
//      */
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     /**
//      * Basic CORS configuration, for Postman and front-end calls
//      */
//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         // configuration.setAllowedOrigins(List.of("*"));
//         configuration.setAllowedOrigins(List.of("https://qnaverse.netlify.app"));
//         // configuration.setAllowedOrigins(List.of("http://localhost:5173"));
    

//         // configuration.setAllowedOrigins(List.of("*"));
//         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         configuration.setAllowedHeaders(List.of("*"));
//         configuration.setAllowCredentials(true);

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);
//         return source;
//     }
// }

package com.qnaverse.QnAverse.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.qnaverse.QnAverse.repositories.UserRepository;
import com.qnaverse.QnAverse.services.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // <--- Autowire JWT Authentication Filter

    /**
     * Provide a UserDetailsService bean that connects to the UserRepository.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    /**
     * Provide AuthenticationProvider, using the custom UserDetailsService.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Build the AuthenticationManager from config.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define the Security filter chain with CSRF, CORS, and JWT handling.
     */
public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider provider) throws Exception {

        http
            .requiresChannel(channel -> channel.anyRequest().requiresSecure())
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(provider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("admin")
                .requestMatchers("/api/question/approve/**").hasAuthority("admin")
                .requestMatchers("/api/block/**", "/api/search/**", "/api/question/trending/**",
                                 "/api/follow/**", "/api/notifications/**", "/api/user/**",
                                 "/api/like/**", "/api/saved/**", "/api/question/**",
                                 "/api/answer/**", "/api/report/**")
                .hasAnyAuthority("user", "admin")
                .anyRequest().permitAll()
            )
            // ✅ Manually set SameSite=None in response headers
            .headers(headers -> headers
                .addHeaderWriter(new StaticHeadersWriter("Set-Cookie", "XSRF-TOKEN=; Path=/; Secure; HttpOnly; SameSite=None"))
            );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provide a BCrypt password encoder bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure CORS settings to allow front-end communication.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins (Update these with your frontend URLs)
        configuration.setAllowedOrigins(List.of("https://qnaverse.netlify.app"));
        
        // Allowed request methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (required for cookies like CSRF token)
        configuration.setAllowCredentials(true);

        // Register CORS configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

