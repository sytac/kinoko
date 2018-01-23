package com.sytac.caseapocalypse.model.db;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "templates")
public class Template implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String subject;
}
