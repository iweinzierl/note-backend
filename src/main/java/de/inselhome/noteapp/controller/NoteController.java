package de.inselhome.noteapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.inselhome.noteapp.controller.error.NoteAppRestError;
import de.inselhome.noteapp.controller.interceptor.ModifyInterceptor;
import de.inselhome.noteapp.controller.interceptor.SaveInterceptor;
import de.inselhome.noteapp.controller.interceptor.SolvedInterceptor;
import de.inselhome.noteapp.domain.Note;
import de.inselhome.noteapp.repository.NoteRepository;

/**
 * @author  iweinzierl
 */
@RestController
@RequestMapping("/note")
public class NoteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private NoteRepository noteRepository;

    private SaveInterceptor saveInterceptor = new SaveInterceptor();
    private ModifyInterceptor modifyInterceptor = new ModifyInterceptor();
    private SolvedInterceptor solvedInterceptor = new SolvedInterceptor();

    @Secured("hasRole(USER)")
    @RequestMapping(produces = "application/json")
    public List<Note> listOpen() {
        LOGGER.info("Inbound Http request: GET - /note");

        UserDetails userDetails = getUserDetails();

        List<Note> notes = noteRepository.findAllByOwnerAndSolvedAtIsNull(userDetails.getUsername());

        LOGGER.debug("found {} notes in database", notes.size());
        return notes;
    }

    @Secured("hasRole(USER)")
    @RequestMapping(value = "/solved", produces = "application/json")
    public List<Note> listSolved() {
        LOGGER.info("Inbound Http request: GET - /note/solved");

        UserDetails userDetails = getUserDetails();

        List<Note> notes = noteRepository.findByOwnerAndSolvedAtNotNull(userDetails.getUsername());

        LOGGER.debug("found {} solved notes in database", notes.size());
        return notes;
    }

    @Secured("hasRole(USER)")
    @RequestMapping(consumes = "application/json", produces = "application/json", method = RequestMethod.PUT)
    public Note save(@RequestBody final Note note) {
        LOGGER.info("Inbound Http request: PUT - /note");
        LOGGER.info("Payload: {}", note);

        saveInterceptor.intercept(note);

        Note savedNote = noteRepository.save(note);

        LOGGER.debug("saved note in database: {}", savedNote);
        return savedNote;
    }

    @Secured("hasRole(USER)")
    @RequestMapping(consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public Note update(@RequestBody final Note note) {
        LOGGER.info("Inbound Http request: POST - /note");
        LOGGER.info("Payload: {}", note);

        modifyInterceptor.intercept(note);

        Note savedNote = noteRepository.save(note);
        LOGGER.debug("updated note in database: {}", savedNote);
        return savedNote;

    }

    @Secured("hasRole(USER)")
    @RequestMapping(value = "/solve/{id}", produces = "application/json", method = RequestMethod.POST)
    public Note markSolved(@PathVariable final String id) {
        LOGGER.info("Inbound Http request: POST - /note/solve/{}", id);

        Note solvedNote = noteRepository.findOne(id);

        solvedInterceptor.intercept(solvedNote);

        noteRepository.save(solvedNote);
        LOGGER.debug("mark note as solved in database: {}", solvedNote);

        return solvedNote;
    }

    @Secured("hasRole(USER)")
    @RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.DELETE)
    public Note delete(@PathVariable final String id) {
        LOGGER.info("Inbound Http request: DELETE - /note/{}", id);

        Note toDelete = noteRepository.findOne(id);

        modifyInterceptor.intercept(toDelete);

        noteRepository.delete(id);
        LOGGER.debug("deleted note in database: {}", toDelete);

        return toDelete;
    }

    @ExceptionHandler(Exception.class)
    public NoteAppRestError handleError(final HttpServletRequest req, final Exception exception) {
        LOGGER.error("An internal error occurred", exception);
        return new NoteAppRestError(exception.getMessage(), exception.getLocalizedMessage());
    }

    private UserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (UserDetails) authentication.getPrincipal();
        }

        throw new BadCredentialsException("User not authenticated");
    }
}
