package com.sytac.caseapocalypse.dao;

import com.sytac.caseapocalypse.model.db.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RoleDao extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
