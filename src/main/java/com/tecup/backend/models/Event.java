package com.tecup.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String place;
    private Date fecha_inicio;
    private Date fecha_fin;


    @ManyToOne
    @JoinColumn(name = "organizador_id", nullable = false)
    private User user;

    public Event() {}

    public Event(Long id, String name, String description, String place, Date fecha_inicio, Date fecha_fin, User user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.place = place;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.user = user;
    }
}
