package de.inselhome.noteapp.controller.interceptor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author  iweinzierl
 */
public abstract class AbstractInterceptor implements OperationInterceptor {

    protected UserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (UserDetails) authentication.getPrincipal();
        }

        throw new BadCredentialsException("User not authenticated");
    }
}
