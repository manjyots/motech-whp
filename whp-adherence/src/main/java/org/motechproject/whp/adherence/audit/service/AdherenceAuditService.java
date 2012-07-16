package org.motechproject.whp.adherence.audit.service;

import org.motechproject.whp.adherence.audit.repository.AllAuditLogs;
import org.motechproject.whp.adherence.audit.domain.AuditLog;
import org.motechproject.whp.adherence.audit.contract.AuditParams;
import org.motechproject.whp.adherence.domain.WeeklyAdherenceSummary;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.Treatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdherenceAuditService {

    private AllAuditLogs allAuditLogs;

    @Autowired
    public AdherenceAuditService(AllAuditLogs allAuditLogs) {
        this.allAuditLogs = allAuditLogs;
    }

    public void log(Patient patient, WeeklyAdherenceSummary weeklyAdherenceSummary, AuditParams auditParams) {
        Treatment currentTreatment = patient.getCurrentTherapy().getCurrentTreatment();
        AuditLog auditLog = new AuditLog()
                .numberOfDosesTaken(weeklyAdherenceSummary.getDosesTaken())
                .providerId(currentTreatment.getProviderId())
                .remark(auditParams.getRemarks())
                .user(auditParams.getUser())
                .sourceOfChange(auditParams.getSourceOfChange().name())
                .patientId(weeklyAdherenceSummary.getPatientId())
                .tbId(currentTreatment.getTbId());
        allAuditLogs.add(auditLog);
    }

    public List<AuditLog> fetchAuditLogs() {
        return allAuditLogs.getAll();
    }
}