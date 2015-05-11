package de.inselhome.noteapp.controller.error;

/**
 * @author  iweinzierl
 */
public class NoteAppRestError {

    private String errorMessage;
    private String localizedErrorMessage;

    public NoteAppRestError() { }

    public NoteAppRestError(final String errorMessage, final String localizedErrorMessage) {
        this.errorMessage = errorMessage;
        this.localizedErrorMessage = localizedErrorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getLocalizedErrorMessage() {
        return localizedErrorMessage;
    }
}
