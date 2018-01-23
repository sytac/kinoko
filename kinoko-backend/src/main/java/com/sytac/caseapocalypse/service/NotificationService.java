package com.sytac.caseapocalypse.service;

import com.sytac.caseapocalypse.dao.NotificationMapDao;
import com.sytac.caseapocalypse.dao.RoleDao;
import com.sytac.caseapocalypse.dao.StageDao;
import com.sytac.caseapocalypse.dao.TemplateDao;
import com.sytac.caseapocalypse.model.db.NotificationMap;
import com.sytac.caseapocalypse.model.db.Role;
import com.sytac.caseapocalypse.model.db.Stage;
import com.sytac.caseapocalypse.model.db.Template;
import com.sytac.caseapocalypse.model.http.NotificationMapRequest;
import com.sytac.caseapocalypse.service.NotificationService;
import com.sytac.caseapocalypse.service.exception.DevCaseServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    RoleDao roleDao;

    @Autowired
    NotificationMapDao notificationMapDao;

    @Autowired
    StageDao stageDao;

    @Autowired
    TemplateDao templateDao;


    public List<Role> getAllRoles() throws DevCaseServiceException {
        try {
            return roleDao.findAll();
        } catch (Exception e) {
            throw new DevCaseServiceException("Impossible to load all the roles", e);
        }
    }

    public List<Role> getRolesForStage(String stage) throws DevCaseServiceException {
        try {
            return notificationMapDao.findByStage_Name(stage).stream()
                    .map(notificationMap -> notificationMap.getRole()).collect(Collectors.toList());
        } catch (Exception e) {
            throw new DevCaseServiceException("Impossible to retrieve the roles for the stage " + stage, e);
        }
    }

    private NotificationMap makeNotificationMap(NotificationMapRequest notificationMapRequest) {
        NotificationMap notificationMap = new NotificationMap();

        Role role = roleDao.findOne(notificationMapRequest.getRole());
        Stage stage = stageDao.findOne(notificationMapRequest.getStage());
        Template template = templateDao.findOne(notificationMapRequest.getTemplate());
        notificationMap.setRole(role);
        notificationMap.setStage(stage);
        notificationMap.setTemplate(template);

        return notificationMap;
    }

    public void addRoleToStage(NotificationMapRequest notificationMapRequest) throws DevCaseServiceException {
        try {
            NotificationMap notificationMap = makeNotificationMap(notificationMapRequest);
            notificationMapDao.save(notificationMap);
        } catch (Exception e) {
            throw new DevCaseServiceException("Impossible to add the role for the stage " + notificationMapRequest.getStage().toString(), e);
        }
    }

    public void deleteRoleFromStage(NotificationMapRequest notificationMapRequest) throws DevCaseServiceException {
        try {
            NotificationMap notificationMap = makeNotificationMap(notificationMapRequest);
            notificationMapDao.delete(notificationMap);
        } catch (Exception e) {
            throw new DevCaseServiceException("Impossible to remove the role for the stage " + notificationMapRequest.getStage().toString(), e);
        }
    }

    public List<NotificationMap> findAll() {
        return notificationMapDao.findAll();
    }

    public void deleteByStageRoleTemplate(Long stageId, Long roleId, Long templateId) throws DevCaseServiceException {
        try {
            notificationMapDao.deleteByStageRoleTemplate(stageId, roleId, templateId);
        } catch (Exception e) {
            throw new DevCaseServiceException("Impossible to remove the notification", e);
        }
    }

    public List<NotificationMap> getRolesTemplatesByStage(Long stageId) {
        return notificationMapDao.findByStage_Id(stageId);
    }
}
