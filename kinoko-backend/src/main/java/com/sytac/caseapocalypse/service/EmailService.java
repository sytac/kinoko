package com.sytac.caseapocalypse.service;

import com.sytac.caseapocalypse.dao.DevCaseEventDao;
import com.sytac.caseapocalypse.dao.NotificationMapDao;
import com.sytac.caseapocalypse.dao.TemplateDao;
import com.sytac.caseapocalypse.dao.UserDao;
import com.sytac.caseapocalypse.model.Roles;
import com.sytac.caseapocalypse.model.db.*;
import com.sytac.caseapocalypse.model.http.CreateDevcaseRequest;
import com.sytac.caseapocalypse.service.exception.DevCaseServiceException;
import com.sytac.caseapocalypse.service.exception.EmailException;
import com.sytac.caseapocalypse.utils.DevCaseEmail;
import com.sytac.caseapocalypse.utils.EmailUtil;
import com.sytac.caseapocalypse.utils.EmailUtilException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmailService {
    // The sender's username for sending the email
    @Value("${email.credentials.username}")
    private String EMAIL_SENDER_USERNAME;

    // The sender's password for sending the email
    @Value("${email.credentials.password}")
    private String EMAIL_SENDER_PASSWORD;

    // The url of the smtp service for sending the email
    @Value("${email.smtp.url}")
    private String EMAIL_SMTP_URL;

    // The name of the app that sends the email, useful for trusted email
    @Value("${email.smtp.app}")
    private String EMAIL_SMTP_APP;

    @Autowired
    private UserDao userDao;

    @Autowired
    private NotificationMapDao notificationMapDao;

    @Autowired
    UserService userService;

    @Autowired
    TemplateDao templateDao;

    @Autowired
    DevCaseEventDao devCaseEventDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);


    public String generateEmailSubject(DevCaseEmail devcaseTemplate) throws EmailException {
        return generateContent(devcaseTemplate,devcaseTemplate.getTemplate().getSubject());
    }

    public String generateEmailBody(DevCaseEmail devcaseTemplate) throws EmailException {
        return generateContent(devcaseTemplate,devcaseTemplate.getTemplate().getContent());
    }

    public String generateContent(DevCaseEmail devcaseTemplate, String variableText) throws EmailException {

        try {
            //Instantiate Configuration class
            Configuration cfg = new Configuration();
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            //Instantiate template
//            String templateStr="Hello ${user}";
            Template template = new Template("email", new StringReader(variableText), cfg);

            //Console output
            Writer outputStreamWriter = new StringWriter();
            template.process(devcaseTemplate.getContent(), outputStreamWriter);
            outputStreamWriter.flush();
            // get the String from the StringWriter
            String html = outputStreamWriter.toString();
            LOGGER.debug("Html generated with freemarker");
            return html;
        } catch (IOException | TemplateException e) {
            LOGGER.error("Error executing freemarker");
            throw new EmailException("Error executing freemarker" , e);
        }
    }

    public void sendEmailAboutDeadlineToCandidate(DevCase devCase) {
        Map<String, Object> content = new HashMap<>();
        content.put("candidate", devCase.getCandidate().getName());
        try {
            sendEmail(devCase.getCandidate(), "deadline_in_3_days_4candidate", content, devCase);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    public void sendEmailAboutDeadlineToCreator(DevCase devCase) {
        Map<String, Object> content = new HashMap<>();
        content.put("creator", devCase.getCreator().getName());
        content.put("candidate", devCase.getCandidate().getName());
        try {
            sendEmail(devCase.getCreator(), "deadline_now_4team", content, devCase);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    public void sendEmailsForStage(Long stageId, CreateDevcaseRequest createDevcaseRequest, String team, String repository) throws DevCaseServiceException {

        try {
            //Send email to Roles defined in the NotificationMap table
            Iterable<NotificationMap> notificationMapIterable = notificationMapDao.findByStage_Id(stageId);

            if (notificationMapIterable == null) {
                LOGGER.error("Info about notifications not present in the DB");
                throw new DevCaseServiceException("Info about notifications not present in the DB");
            }
            if (notificationMapIterable != null) {

                for (NotificationMap notificationMap : notificationMapIterable) {
                    if (notificationMap.getTemplate() == null || notificationMap.getTemplate().getName() == null) {
                        LOGGER.error("No Template assigned in the NotificationMap table");
                        throw new DevCaseServiceException("No Template assigned in the NotificationMap table");
                    }
                    if (notificationMap.getRole() == null || notificationMap.getRole().getName() == null) {
                        LOGGER.error("No Role assigned in the NotificationMap table");
                        throw new DevCaseServiceException("No Role assigned in the NotificationMap table");
                    }
                    com.sytac.caseapocalypse.model.db.Template template = notificationMap.getTemplate();
                    Role role = notificationMap.getRole();
                    List<User> users = new ArrayList<>();

                    if (Roles.CANDIDATE.get().equals(role.getName())) {
                        //send email just to the specific candidate of the project
                        if (createDevcaseRequest.getCandidate() != null) {
                            users.add(createDevcaseRequest.getCandidate());
                        }
                    } else if (Roles.CREATOR.get().equals(role.getName())) {
                        //send email just to the specific creator of the project
                        if (createDevcaseRequest.getCreator() != null) {
                            users.add(createDevcaseRequest.getCreator());
                        }
                    } else if (Roles.REVIEWER.get().equals(role.getName())) {
                        //send email just to the specific reviewer of the project
                        if (createDevcaseRequest.getReviewer() != null) {
                            users.add(createDevcaseRequest.getReviewer());
                        }
                    } else {
                        //send email to all the others
                        users = userService.getUsersByRoleAndReviewerTeam(role, team);
                    }
                    sendEmails(users, template, createDevcaseRequest, repository);
                }
            }
        } catch (Exception e) {
            throw new DevCaseServiceException("Impossible to send email " + e.getMessage(), e);
        }
    }

    private void sendEmail(User receiver, String templateName, Map<String, Object> content, DevCase devCase) throws EmailException {
        com.sytac.caseapocalypse.model.db.Template template = templateDao.findByName(templateName);
        if(template == null) {
            String message = String.format("Error sending email. Template %s is not found", templateName);
            LOGGER.error(message);
            throw new EmailException(message);
        }

        // Check if the email was already sent
        if(devCaseEventDao.findByNameAndDevCase(templateName, devCase) != null) {
            return;
        }

        DevCaseEmail devCaseEmail = new DevCaseEmail();
        devCaseEmail.setTemplate(template);
        devCaseEmail.setContent(content);

        try {
            sendEmail(devCase.getCreator().getEmail(), devCaseEmail);

            //Saving info that the email was sent to not send it again
            DevCaseEvent devCaseEvent = new DevCaseEvent();
            devCaseEvent.setDevCase(devCase);
            devCaseEvent.setName(templateName);
            devCaseEventDao.save(devCaseEvent);
        } catch (DevCaseServiceException e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String receiverEmail, DevCaseEmail devCaseEmail) throws DevCaseServiceException {
        try {
            String emailBody = generateEmailBody(devCaseEmail);
            String emailSubject = generateEmailSubject(devCaseEmail);
            if (emailBody == null) {
                LOGGER.error("Error generating email");
                throw new DevCaseServiceException("Error generating email");
            }
            EmailUtil.send(EMAIL_SENDER_USERNAME, EMAIL_SENDER_PASSWORD, receiverEmail, emailSubject, emailBody, EMAIL_SMTP_URL, EMAIL_SMTP_APP);
            System.out.println("email sent to " + receiverEmail);
        } catch (EmailUtilException e) {
            throw new DevCaseServiceException("Error sending the email", e);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    private void sendEmails(List<User> users, com.sytac.caseapocalypse.model.db.Template template, CreateDevcaseRequest createDevcaseRequest, String repository) throws DevCaseServiceException {
        try {
            if (users != null && users.size() > 0) {
                //Send an email (with a specific template) to every user whit that role
                for (User user : users) {
                    Map<String, Object> emailBodyContent = new HashMap<>();
                    emailBodyContent.put("project_type", createDevcaseRequest.getType());
                    emailBodyContent.put("candidate", createDevcaseRequest.getCandidate());
                    emailBodyContent.put("creator", createDevcaseRequest.getCreator());
                    emailBodyContent.put("repository_url", repository);
                    emailBodyContent.put("deadline", createDevcaseRequest.getDeadline().toString());
                    emailBodyContent.put("reviewer", createDevcaseRequest.getReviewer());
                    emailBodyContent.put("github_reviewers", createDevcaseRequest.getGitHubReviewers());
                    DevCaseEmail devCaseForUser = new DevCaseEmail();
                    devCaseForUser.setTemplate(template);
                    devCaseForUser.setContent(emailBodyContent);

                    if (user.getEmail() == null) {
                        User byName = userDao.findByName(user.getName());
                        if (byName == null) {
                            LOGGER.error("User not present in the DB");
                            throw new DevCaseServiceException("User not present in the DB");
                        }
                        sendEmail(byName.getEmail(), devCaseForUser);
                        LOGGER.debug("Email sent to " + byName.getEmail());
                    } else {
                        sendEmail(user.getEmail(), devCaseForUser);
                        LOGGER.debug("Email sent to " + user.getEmail());
                    }
                }
            }
        } catch (Exception e) {
            throw new DevCaseServiceException("Impossible to send email " + e.getMessage(), e);
        }
    }
}