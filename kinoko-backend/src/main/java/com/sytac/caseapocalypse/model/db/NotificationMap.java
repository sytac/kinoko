package com.sytac.caseapocalypse.model.db;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "notification_map")
public class NotificationMap implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne
    private Stage stage;

    @ManyToOne
    private Role role;

    @ManyToOne
    private Template template;

}
