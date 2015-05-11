package de.inselhome.noteapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.inselhome.noteapp.domain.Note;

/**
 * @author  iweinzierl
 */
public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findAllByOwnerAndSolvedAtIsNull(String owner);

    List<Note> findByOwnerAndSolvedAtNotNull(String owner);
}
