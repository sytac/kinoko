package com.sytac.caseapocalypse.background;

import com.sytac.caseapocalypse.model.Stages;
import com.sytac.caseapocalypse.service.DevCaseService;
import com.sytac.caseapocalypse.service.EmailService;
import com.sytac.caseapocalypse.service.exception.DevCaseServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DeadlineChecker {
    @Autowired
    DevCaseService devCaseService;

    @Autowired
    EmailService emailService;

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void run() {
        executorService.scheduleAtFixedRate(() -> {
            try {
                devCaseService.getAllDevcases().stream().forEach(devCase -> {
                    if(!Stages.INIT.get().equals(devCase.getStage().getName())) {
                        return;
                    }

                    Date now = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(now);
                    cal.add(Calendar.DATE, -3);
                    Date threeDaysBeforeNow = cal.getTime();

                    if(devCase.getDeadline().getTime() > now.getTime()) {
                        emailService.sendEmailAboutDeadlineToCreator(devCase);
                    }
                    else if(devCase.getDeadline().getTime() > threeDaysBeforeNow.getTime()) {
                        emailService.sendEmailAboutDeadlineToCandidate(devCase);
                    }
                });
            } catch (DevCaseServiceException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stop() {
        executorService.shutdownNow();
    }
}
