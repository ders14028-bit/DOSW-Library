package edu.eci.dosw.tdd.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class Book {
    private String title;
    private String author;
    private String id;

    public Book(String id, String author, String title) {
        this.id = id;
        this.author = author;
        this.title = title;
    }

}

