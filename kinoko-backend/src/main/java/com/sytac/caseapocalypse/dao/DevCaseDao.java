package com.sytac.caseapocalypse.dao;

import com.sytac.caseapocalypse.model.db.DevCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface DevCaseDao extends JpaRepository<DevCase, Long> {
    DevCase findByCandidate_Name(String name);

    DevCase findByIdAndCandidate_Name(long id, String candidateName);
}
