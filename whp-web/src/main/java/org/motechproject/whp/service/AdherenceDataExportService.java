package org.motechproject.whp.service;

import org.motechproject.export.annotation.DataProvider;
import org.motechproject.export.annotation.ExcelDataSource;
import org.motechproject.whp.adherence.audit.domain.AdherenceAuditLog;
import org.motechproject.whp.adherence.audit.service.AdherenceAuditService;
import org.motechproject.whp.common.util.WHPDate;
import org.motechproject.whp.uimodel.AdherenceLogSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@ExcelDataSource(name = "adherence")
public class AdherenceDataExportService {

    private AdherenceAuditService adherenceAuditService;

    @Autowired
    public AdherenceDataExportService(AdherenceAuditService adherenceAuditService) {
        this.adherenceAuditService = adherenceAuditService;
    }

    @DataProvider
    public List<AdherenceLogSummary> adherenceAuditLogSummaries(int pageNo) {
        return map(adherenceAuditService.allAuditLogs(pageNo));
    }

    List<AdherenceLogSummary> map(List<AdherenceAuditLog> adherenceAuditLogs) {
        List<AdherenceLogSummary> adherenceLogSummaries = new ArrayList<>();
        for(AdherenceAuditLog adherenceAuditLog : adherenceAuditLogs) {
            AdherenceLogSummary adherenceLogSummary = new AdherenceLogSummary();
            adherenceLogSummary.setPatientId(adherenceAuditLog.getPatientId());
            adherenceLogSummary.setTbId(adherenceAuditLog.getTbId());
            adherenceLogSummary.setCreationTime(new WHPDate(adherenceAuditLog.getCreationTime().toLocalDate()).value());
            adherenceLogSummary.setDoseDate(new WHPDate(adherenceAuditLog.getDoseDate().toLocalDate()).value());
            adherenceLogSummary.setUserId(adherenceAuditLog.getUserId());
            adherenceLogSummary.setNumberOfDosesTaken(adherenceAuditLog.getNumberOfDosesTaken());
            adherenceLogSummary.setPillStatus(adherenceAuditLog.getPillStatus());
            adherenceLogSummary.setSourceOfChange(adherenceAuditLog.getSourceOfChange());
            adherenceLogSummaries.add(adherenceLogSummary);
        }
        return adherenceLogSummaries;
    }
}
