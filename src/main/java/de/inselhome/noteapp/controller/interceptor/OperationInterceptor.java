package de.inselhome.noteapp.controller.interceptor;

import de.inselhome.noteapp.domain.Note;

/**
 * @author  iweinzierl
 */
public interface OperationInterceptor {

    void intercept(Note note);
}
