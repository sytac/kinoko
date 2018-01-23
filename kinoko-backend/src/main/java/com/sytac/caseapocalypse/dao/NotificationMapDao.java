package com.sytac.caseapocalypse.dao;

import com.sytac.caseapocalypse.model.db.NotificationMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Component
public interface NotificationMapDao extends JpaRepository<NotificationMap, Long> {
    List<NotificationMap> findByStage_Name(String stageName);
    List<NotificationMap> findByStage_Id(Long stageId);
    @Query("delete from NotificationMap n where n.stage.id = ? and n.role.id = ? and n.template.id = ?")
    @Modifying
    void deleteByStageRoleTemplate(Long stageId, Long roleId, Long templateId);
}
