package com.sytac.caseapocalypse.dao;

import com.sytac.caseapocalypse.model.db.Stage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface StageDao extends CrudRepository<Stage, Long> {
    Stage findByName(String name);
}