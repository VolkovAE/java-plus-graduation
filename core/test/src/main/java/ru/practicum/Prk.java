package ru.practicum;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Prk {
    @Id
    long id;

    long h;

    long l;
}
