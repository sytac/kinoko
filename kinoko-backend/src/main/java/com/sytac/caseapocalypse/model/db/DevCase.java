package com.sytac.caseapocalypse.model.db;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "dev_cases")
public class DevCase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String type;

    private Date deadline;

    private Date creation;

    private Date modified;

    @ManyToOne
    private User candidate;

    @ManyToOne
    private User creator;

    /**
     * the guy who did the review
     */
    @ManyToOne
    private User reviewer;

    private String githubUrl;

    @ManyToOne
    private Stage stage;
}
