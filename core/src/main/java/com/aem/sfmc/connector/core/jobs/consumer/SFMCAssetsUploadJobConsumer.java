package com.aem.sfmc.connector.core.jobs.consumer;

import com.adobe.acs.commons.email.EmailService;

import com.aem.sfmc.connector.core.beans.SFMCAssetUploadReport;
import com.aem.sfmc.connector.core.configs.SFMCAssetsUploadJobConfig;
import com.aem.sfmc.connector.core.constants.JobTopicsConstants;
import com.aem.sfmc.connector.core.constants.SFMCConstants;
import com.aem.sfmc.connector.core.services.DamUtilityService;
import com.aem.sfmc.connector.core.services.GenericListService;
import com.aem.sfmc.connector.core.services.SFMCAssetUploadService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = JobConsumer.class, immediate = true, property = {
        JobConsumer.PROPERTY_TOPICS + SFMCConstants.EQUAL_TO + JobTopicsConstants.UPLOAD_JOB_TOPIC,
        Constants.SERVICE_DESCRIPTION + "=Job Consumer which adds job to Upload Assets"})
@Designate(ocd = SFMCAssetsUploadJobConfig.class)
@ServiceDescription("Assets upload Service")
public class SFMCAssetsUploadJobConsumer implements JobConsumer {

    JobResult jobResult = null;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Reference
    private SFMCAssetUploadService sfmcAssetUploadService;

    @Reference
    private DamUtilityService damUtilityService;

    @Reference
    private EmailService emailService;

    @Reference
    private GenericListService genericListService;

    private SFMCAssetsUploadJobConfig config;

    final Map<String, String> emailParams = new HashMap<>();
    final String templatePath = "/etc/notification/email/enterprisedam/sfmcEmailTemplate.txt";

    public void activate(final SFMCAssetsUploadJobConfig config) {
        this.config = config;
    }

    @Override
    public JobResult process(final Job job) {
        LOG.info("SFMCAssetsUploadJobConsumer -- START process");
        final SFMCAssetUploadReport uploadReport = sfmcAssetUploadService.uploadAssets();
        damUtilityService.updateAssetMetadata(uploadReport);
        if (config.emailServiceEnabled() && CollectionUtils.isNotEmpty(uploadReport.getUploadReport())) {
            emailParams.put("senderEmailAddress", "noreply@deloitte.com");
            emailParams.put("senderName", "Deloitte");
            emailParams.put("status", uploadReport.toString());
            List<String> recipients = genericListService.getTitlesAsList(config.genericListRoot(), "sfmcEmailRecipientsList");
            String[] recipientsArray = new String[recipients.size()];
            recipientsArray = recipients.toArray(recipientsArray);
            List<String> failureList = emailService.sendEmail(templatePath, emailParams, recipientsArray);
            LOG.info("failure receipients List {}", failureList);

        }
        LOG.info("SFMCAssetsUploadJobConsumer -- END process");
        return JobResult.OK;
    }

}