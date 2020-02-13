package com.reimbes.configuration;

import com.reimbes.authentication.filter.JWTAuthenticationFilter;
import com.reimbes.authentication.filter.JWTAuthorizationFilter;
import com.reimbes.authentication.rest.RESTAuthenticationEntryPoint;
import com.reimbes.authentication.rest.RESTAuthenticationFailureHandler;
import com.reimbes.authentication.rest.RESTAuthenticationSuccessHandler;
import com.reimbes.implementation.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.reimbes.constant.SecurityConstants.HEADER_STRING;
import static com.reimbes.constant.UrlConstants.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private static Logger log = LoggerFactory.getLogger(WebSecurity.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private RESTAuthenticationFailureHandler restAuthenticationFailureHandler;

    @Autowired
    private RESTAuthenticationSuccessHandler restAuthenticationSuccessHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .formLogin().successHandler(restAuthenticationSuccessHandler)
                .and()
                .formLogin().failureHandler(restAuthenticationFailureHandler)
                .and()
                .authorizeRequests()
                .antMatchers(
                        API_PREFIX + LOGIN_URL,
                        API_PREFIX + LOGOUT_URL,
                        API_PREFIX + ISLOGIN_URL,
                        // Swagger URL
                        "/v2/api-docs",
                        "/spring-security-rest/api**",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                ).permitAll()
                .antMatchers(
                        API_PREFIX + ADMIN_PREFIX,
                        API_PREFIX + ADMIN_PREFIX + "/**"
                ).hasAuthority("ADMIN")
                .antMatchers(
                        API_PREFIX + USER_PREFIX,
                        API_PREFIX + USER_PREFIX + "/**",
                        API_PREFIX + TRANSACTION_PREFIX,
                        API_PREFIX + TRANSACTION_PREFIX + "/**",
                        API_PREFIX + MEDICAL_PREFIX,
                        API_PREFIX + MEDICAL_PREFIX + "/**"
                ).hasAuthority("USER")
                .anyRequest().authenticated()
                .and()
                .addFilter(jwtAuthenticationFilter(authenticationManager()))
                .addFilter(jwtAuthorizationFilter(authenticationManager()))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }


    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        log.info("Set CORS configuration...");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration cors = new CorsConfiguration().applyPermitDefaultValues();
        cors.addAllowedMethod(HttpMethod.PUT);
        cors.addAllowedMethod(HttpMethod.DELETE);
        cors.addAllowedHeader(HEADER_STRING);
        cors.addExposedHeader(HEADER_STRING);
        source.registerCorsConfiguration("/**", cors);

        log.info("CORS origins, methods, and headers: ");
        log.info(cors.getAllowedOrigins().toString());
        log.info(cors.getAllowedMethods().toString());
        log.info(cors.getAllowedHeaders().toString());
        log.info(cors.getExposedHeaders().toString());
        return source;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl(API_PREFIX + LOGIN_URL);

        return jwtAuthenticationFilter;
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(authenticationManager);

        return jwtAuthorizationFilter;
    }

}
