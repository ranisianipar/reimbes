package com.reimbes.implementation;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
* Author: Rani Lasma Uli
*
* This class filled with the static methods.
* Static methods need to be collected in one class for the sake of 'unit test' using Powermockito class.
*
* */

@Service
public class Utils {

    public String getUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
