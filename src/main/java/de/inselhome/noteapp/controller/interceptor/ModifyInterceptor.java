package de.inselhome.noteapp.controller.interceptor;

import org.apache.commons.lang3.StringUtils;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

import de.inselhome.noteapp.domain.Note;

/**
 * @author  iweinzierl
 */
public class ModifyInterceptor extends AbstractInterceptor {

    public void intercept(final Note note) {
        UserDetails userDetails = getUserDetails();
        if (!StringUtils.equals(note.getOwner(), userDetails.getUsername())) {
            throw new BadCredentialsException("User is not allowed to update other users note");
        }
    }
}
