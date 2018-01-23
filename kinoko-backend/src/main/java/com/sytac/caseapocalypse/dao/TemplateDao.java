package com.sytac.caseapocalypse.dao;

import com.sytac.caseapocalypse.model.db.Template;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface TemplateDao extends CrudRepository<Template, Long> {
    Template findByName(String name);
}
