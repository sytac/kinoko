package com.sytac.caseapocalypse.controllers;

import com.sytac.caseapocalypse.model.JSONresponse;
import com.sytac.caseapocalypse.model.db.DevCase;
import com.sytac.caseapocalypse.model.db.NotificationMap;
import com.sytac.caseapocalypse.model.db.Role;
import com.sytac.caseapocalypse.model.db.User;
import com.sytac.caseapocalypse.model.http.*;
import com.sytac.caseapocalypse.service.DevCaseService;
import com.sytac.caseapocalypse.service.NotificationService;
import com.sytac.caseapocalypse.service.UserService;
import com.sytac.caseapocalypse.service.exception.DevCaseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    public static final String SESSION_OBJECT = "user";

    @Autowired
    UserService userService;

    @Autowired
    DevCaseService devCaseService;

    @Autowired
    NotificationService notificationService;

    @RequestMapping(method = RequestMethod.GET, value = "/connection/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity testConnection() {
        return new ResponseEntity<JSONresponse>(new JSONresponse("Server available."), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.OPTIONS, value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loginOptions(@RequestBody UserLoginRequest userLoginRequest, ServletRequest servletRequest) throws AccessDeniedException {
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody UserLoginRequest userLoginRequest, ServletRequest servletRequest) {
        try {
            UserLoginResponse userLoginResponse = userService.login(userLoginRequest, servletRequest);
            LOGGER.info("Login user: ", userLoginRequest);
            return new ResponseEntity<UserLoginResponse>(userLoginResponse, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/devcase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllDevcases() {
        try {
            List<DevCase> allDevcases = devCaseService.getAllDevcases();
            return new ResponseEntity<List<DevCase>>(allDevcases, HttpStatus.OK);
        } catch (DevCaseServiceException e) {
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/devcase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createDevcase(@RequestBody CreateDevCaseRequestJson createDevCaseRequestJson, HttpSession httpSession) {
        try {
            User user = (User) httpSession.getAttribute(SESSION_OBJECT);
            CreateDevcaseRequest createDevcaseRequest = new CreateDevcaseRequest();
            createDevcaseRequest.setCreator(user);
            createDevcaseRequest.setDeadline(createDevCaseRequestJson.getDeadline());
            createDevcaseRequest.setType(createDevCaseRequestJson.getType());
            User candidate = new User();
            candidate.setEmail(createDevCaseRequestJson.getCandidate_email());
            candidate.setName(createDevCaseRequestJson.getCandidate_name());
            candidate.setGithubUserName(createDevCaseRequestJson.getCandidate_account());
            candidate.setTimestamp(new Date());
            createDevcaseRequest.setCandidate(candidate);
            devCaseService.setupDevCase(createDevcaseRequest);
        } catch (DevCaseServiceException e) {
            //return HTTP 500 Server Error
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //return HTTP 201 Created
        return new ResponseEntity<JSONresponse>(new JSONresponse("Repo created"),HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/devcase/{caseId}/{stage}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDevcase(@PathVariable Long caseId, @PathVariable Long stage, HttpSession httpSession) {
        try {
            User user = (User) httpSession.getAttribute(SESSION_OBJECT);
            devCaseService.updateDevcase(caseId, stage, user);
        } catch (DevCaseServiceException e) {
            //return HTTP 500 Server Error
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //return HTTP 201 Created
        return new ResponseEntity<JSONresponse>(new JSONresponse("Repo updated"),HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/role", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllRoles() {
        try {
            List<Role> allRoles = notificationService.getAllRoles();
            return new ResponseEntity<List<Role>>(allRoles, HttpStatus.OK);
        } catch (DevCaseServiceException e) {
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/role/{stage}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRolesForStage(@PathVariable Long stage) {
        List<NotificationMap> rolesTemplatesByStage = null;
        try {
            rolesTemplatesByStage = notificationService.getRolesTemplatesByStage(stage);
            return new ResponseEntity<List<NotificationMap>>(rolesTemplatesByStage, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/notifications/role")
    public ResponseEntity addRoleToStage(@RequestBody NotificationMapRequest notificationMapRequest) {
        try {
            notificationService.addRoleToStage(notificationMapRequest);
            return new ResponseEntity<JSONresponse>(new JSONresponse("Role added"),HttpStatus.CREATED);
        } catch (DevCaseServiceException e) {
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/admin/notifications/role")
    public ResponseEntity deleteRoleFromStage(@RequestBody NotificationMapRequest notificationMapRequest) {
        try {
            notificationService.deleteRoleFromStage(notificationMapRequest);
            return new ResponseEntity<JSONresponse>(new JSONresponse("Role deleted"),HttpStatus.OK);
        } catch (DevCaseServiceException e) {
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/admin/notifications/role/{role}/{stage}/{template}")
    public ResponseEntity deleteNotificationMapEntry(@PathVariable Long role, @PathVariable Long stage, @PathVariable Long template) {
        try {
            notificationService.deleteByStageRoleTemplate(stage, role, template);
            return new ResponseEntity<JSONresponse>(new JSONresponse("NotificationMap entry deleted"),HttpStatus.OK);
        } catch (DevCaseServiceException e) {
            return new ResponseEntity<JSONresponse>(new JSONresponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
