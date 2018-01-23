package com.sytac.caseapocalypse.dao;


import com.sytac.caseapocalypse.model.db.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserDao extends CrudRepository<User, Long> {
    User findByName(String name);
    List<User> findByRole_Name(String name);
    User findByEmail(String email);
    User findByGithubUserName(String githubUsername);
}
