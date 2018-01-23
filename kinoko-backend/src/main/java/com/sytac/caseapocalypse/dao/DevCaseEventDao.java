package com.sytac.caseapocalypse.dao;

import com.sytac.caseapocalypse.model.db.DevCase;
import com.sytac.caseapocalypse.model.db.DevCaseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface DevCaseEventDao extends JpaRepository<DevCaseEvent, Long> {
    DevCaseEvent findByNameAndDevCase(String name, DevCase devCase);
}
