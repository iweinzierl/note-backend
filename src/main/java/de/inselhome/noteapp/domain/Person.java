package de.inselhome.noteapp.domain;

/**
 * @author  iweinzierl
 */
public class Person {

    private String name;

    public Person() { }

    public Person(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
