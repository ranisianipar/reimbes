package com.reimbes.configuration;

import com.reimbes.authentication.filter.JWTAuthenticationFilter;
import com.reimbes.authentication.filter.JWTAuthorizationFilter;
import com.reimbes.authentication.rest.RESTAuthenticationEntryPoint;
import com.reimbes.authentication.rest.RESTAuthenticationFailureHandler;
import com.reimbes.authentication.rest.RESTAuthenticationSuccessHandler;
import com.reimbes.constant.UrlConstants;
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
import static com.reimbes.constant.UrlConstants.CROSS_ORIGIN_URL;

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
                        UrlConstants.LOGIN_URL,
                        UrlConstants.LOGOUT_URL,
                        UrlConstants.ISLOGIN_URL,
                        "/v2/api-docs"
                ).permitAll()
                .antMatchers(
                        UrlConstants.ADMIN_PREFIX,
                        UrlConstants.ADMIN_PREFIX+"/**"
                    ).hasAuthority("ADMIN")
                .antMatchers(
                        UrlConstants.USER_PREFIX,
                        UrlConstants.USER_PREFIX+"/**",
                        UrlConstants.TRANSACTION_PREFIX,
                        UrlConstants.TRANSACTION_PREFIX+"/**"
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
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedHeader(HEADER_STRING);
        cors.addExposedHeader(HEADER_STRING);
        cors.addAllowedMethod(HttpMethod.POST);
        cors.addAllowedMethod(HttpMethod.GET);
        cors.addAllowedMethod(HttpMethod.PUT);
        cors.addAllowedMethod(HttpMethod.DELETE);
        cors.addAllowedOrigin(CROSS_ORIGIN_URL);
        source.registerCorsConfiguration("/**", cors.applyPermitDefaultValues());

        log.info("CORS: ");
        log.info(cors.checkOrigin(CROSS_ORIGIN_URL));
        log.info(cors.getAllowedMethods().toString());
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
        jwtAuthenticationFilter.setFilterProcessesUrl(UrlConstants.API_PREFIX+UrlConstants.LOGIN_URL);

        return jwtAuthenticationFilter;
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(authenticationManager);

        return jwtAuthorizationFilter;
    }

}
