package com.reimbes.configuration;

import com.reimbes.implementation.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

import static com.reimbes.constant.SecurityConstants.*;

/**
 * This class will be used to update time expiration of token
 * whenever user do any request.
 */
@Component
public class LoggerInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);

    @Autowired
    private AuthServiceImpl authService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        log.info("[PRE HANDLE]");
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            // parse the token.
            HashMap<String, Object> userDetails = authService.getCurrentUserDetails(request);

            if (userDetails != null) {
                log.info("["+userDetails.get("username")+"]"+" Request: "+request.getRequestURI());
                authService.registerToken(token);
                return true;
            }
        }

        return false;
    }
}
