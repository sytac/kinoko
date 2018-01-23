package com.sytac.caseapocalypse.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.sytac.caseapocalypse.dao.UserDao;
import com.sytac.caseapocalypse.model.Roles;
import com.sytac.caseapocalypse.model.db.GitHubMember;
import com.sytac.caseapocalypse.model.db.Role;
import com.sytac.caseapocalypse.model.db.User;
import com.sytac.caseapocalypse.model.http.UserLoginRequest;
import com.sytac.caseapocalypse.model.http.UserLoginResponse;
import com.sytac.caseapocalypse.service.UserService;
import com.sytac.caseapocalypse.service.exception.DevCaseServiceException;
import com.sytac.caseapocalypse.service.exception.GitHubServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserService {
    @Value("${github.company}")
    private String COMPANY;

    @Autowired
    UserDao userDao;

    @Autowired
    GithubService githubService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserLoginResponse login(UserLoginRequest userLoginRequest, ServletRequest servletRequest) throws AccessDeniedException {
        GoogleCredential credential = new GoogleCredential().setAccessToken(userLoginRequest.getToken());
        Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("Oauth2").build();

        Userinfoplus userinfoplus = null;

        try {
            userinfoplus = oauth2.userinfo().get().execute();
        } catch (IOException e) {
            throw new AccessDeniedException("Invalid access key.");
        }

        User user = userDao.findByEmail(userinfoplus.getEmail());

        if (user == null) {
            throw new AccessDeniedException("Invalid access key.");
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("user", user);

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setRole(user.getRole().getName());
        userLoginResponse.setEmail(user.getEmail());
        userLoginResponse.setName(user.getName());

        return userLoginResponse;
    }

    List<User> getUsersByRoleAndReviewerTeam(Role role, String team) throws DevCaseServiceException {

        if (role == null || role.getName() == null) {
            throw new DevCaseServiceException("The role name should be not empty");
        }
        if (Roles.REVIEWERS.get().equals(role.getName())) {
            List<User> reviewers = new ArrayList<>();
            //retrieve users from GitHub API
            try {
                List<GitHubMember> teamMembers = githubService.getTeamMembers(githubService.getTeamId(githubService.getTeams(COMPANY), team));
                if (teamMembers == null) {
                    LOGGER.error("Team members not found");
                    throw new DevCaseServiceException("Team members not found");
                }
                for (GitHubMember member : teamMembers) {
                    User reviewer = userDao.findByGithubUserName(member.getUserName());
                    if (reviewer == null) {
                        LOGGER.error("Team member not found");
                        throw new DevCaseServiceException("Team member not found");
                    }
                    if (reviewer != null) {
                        reviewers.add(reviewer);
                    }
                }
                return reviewers;
            } catch (GitHubServiceException e) {
                LOGGER.error("Impossible to retrieve the list of Reviewer from GitHub");
                throw new DevCaseServiceException("Impossible to retrieve the list of Reviewer from GitHub");
            }
        }
        if (Roles.CANDIDATE.get().equals(role.getName()) || Roles.REVIEWER.get().equals(role.getName())) {
            return new ArrayList<>();
        }
        if (Roles.ADMIN.get().equals(role.getName())
                || Roles.RECRUITER.get().equals(role.getName())) {
            //retrieve ADMIN users and RECRUITER users from the database
            return userDao.findByRole_Name(role.getName());
        }
        return null;
    }
}
