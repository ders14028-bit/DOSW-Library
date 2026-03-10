package edu.eci.dosw.tdd.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class User {
    private String name;
    private String id;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

}