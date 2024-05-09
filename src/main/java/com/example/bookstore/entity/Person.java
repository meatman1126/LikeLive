package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;
}
