package de.inselhome.noteapp.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.springframework.data.annotation.Id;

/**
 * @author  iweinzierl
 */
public class Note {

    public static class Builder {

        private Note note;

        public Builder() {
            this.note = new Note();
        }

        public Builder withCreation(final Date creation) {
            note.setCreation(creation);
            return this;
        }

        public Builder withDescription(final String description) {
            note.setDescription(description);
            return this;
        }

        public Builder withPerson(final Person person) {
            note.addPerson(person);
            return this;
        }

        public Note build() {
            return note;
        }
    }

    @Id
    private String id;

    private String owner;

    private Date creation;
    private Date solvedAt;

    private String description;

    private Set<Person> people;
    private Set<Tag> tags;

    public Note() {
        people = new HashSet<>();
        tags = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(final Date creation) {
        this.creation = creation;
    }

    public Date getSolvedAt() {
        return solvedAt;
    }

    public void setSolvedAt(final Date solvedAt) {
        this.solvedAt = solvedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Set<Person> getPeople() {
        return people;
    }

    public void setPeople(final Set<Person> people) {
        this.people = people;
    }

    public void addPerson(final Person person) {
        this.people.add(person);
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(final Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(final Tag tag) {
        this.tags.add(tag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("creation", creation).append("solvedAt", solvedAt)
                                        .append("description", description).append("people", people)
                                        .append("tags", tags).toString();
    }
}
