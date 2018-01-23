package com.sytac.caseapocalypse.model.db;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;
    private String email;
    private String githubUserName;

    @ManyToOne
    private Role role;

    private Date timestamp;
}
