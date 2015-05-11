package de.inselhome.noteapp.controller.interceptor;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

import de.inselhome.noteapp.domain.Note;

/**
 * @author  iweinzierl
 */
public class SaveInterceptor extends AbstractInterceptor {

    public void intercept(final Note note) {
        UserDetails userDetails = getUserDetails();
        note.setOwner(userDetails.getUsername());
        note.setCreation(new Date());
    }
}
