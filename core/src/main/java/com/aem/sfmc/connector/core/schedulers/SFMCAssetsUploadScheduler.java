package com.aem.sfmc.connector.core.schedulers;


import com.aem.sfmc.connector.core.configs.SFMCAssetsUploadSchedulerConfig;
import com.aem.sfmc.connector.core.constants.JobTopicsConstants;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@Designate(ocd = SFMCAssetsUploadSchedulerConfig.class)
@Component(immediate = true, service = SFMCAssetsUploadScheduler.class)
public class SFMCAssetsUploadScheduler {

    @Reference
    private Scheduler scheduler;

    @Reference
    private JobManager sfmcUploadJob;

    private int schedulerID;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    public void activate(SFMCAssetsUploadSchedulerConfig config) {
        schedulerID = config.schedulerName().hashCode();
        addScheduler(config);
    }

    @Modified
    protected void modified(SFMCAssetsUploadSchedulerConfig config) {
        removeScheduler();
        schedulerID = config.schedulerName().hashCode(); // update schedulerID
        addScheduler(config);
    }

    @Deactivate
    protected void deactivate(SFMCAssetsUploadSchedulerConfig config) {
        removeScheduler();
    }


    private void removeScheduler() {
        log.debug("Removing Scheduler Job '{}'", schedulerID);
        Collection<ScheduledJobInfo> scheduledJobInfos = sfmcUploadJob.getScheduledJobs(JobTopicsConstants.UPLOAD_JOB_TOPIC, 0, null);
        for (ScheduledJobInfo scheduledJobInfo : scheduledJobInfos) {
            scheduledJobInfo.unschedule();
        }
    }

    private void addScheduler(SFMCAssetsUploadSchedulerConfig config) {
        if (config.serviceEnabled()) {
            JobBuilder.ScheduleBuilder scheduleBuilder = sfmcUploadJob.createJob(JobTopicsConstants.UPLOAD_JOB_TOPIC).schedule();
            String cron = PropertiesUtil.toString(config.schedulerExpression(), config.schedulerExpression());
            scheduleBuilder.cron(cron);
            ScheduledJobInfo scheduledJobInfo = scheduleBuilder.add();
            if (scheduledJobInfo == null) {
                log.info("scheduledJobInfo is null");
            } else {
                log.info("scheduledJobInfo {}", scheduledJobInfo.getJobTopic());
            }
        }

    }

}