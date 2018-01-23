package com.sytac.caseapocalypse.model.http;

import com.sytac.caseapocalypse.model.db.GitHubMember;
import com.sytac.caseapocalypse.model.db.Stage;
import com.sytac.caseapocalypse.model.db.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateDevcaseRequest {
    private String type;
    private Date deadline;
    private User candidate;
    private User creator;
    private User reviewer;
    private String githubUrl;
    private Stage stage;
    private List<User> gitHubReviewers;
}
