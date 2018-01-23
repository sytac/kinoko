package com.sytac.caseapocalypse.model.db;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "event_log")
public class DevCaseEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne
    private DevCase devCase;

    private String name;
}
