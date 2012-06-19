package org.motechproject.whp.adherence.service;

import org.joda.time.LocalDate;
import org.motechproject.adherence.contract.AdherenceData;
import org.motechproject.adherence.contract.AdherenceRecords;
import org.motechproject.adherence.service.AdherenceService;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.adherence.audit.AdherenceAuditService;
import org.motechproject.whp.adherence.audit.AuditParams;
import org.motechproject.whp.adherence.domain.Adherence;
import org.motechproject.whp.adherence.domain.TreatmentWeek;
import org.motechproject.whp.adherence.domain.WeeklyAdherence;
import org.motechproject.whp.adherence.mapping.AdherenceDataMapper;
import org.motechproject.whp.adherence.mapping.AdherenceMapper;
import org.motechproject.whp.adherence.mapping.WeeklyAdherenceMapper;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.whp.adherence.domain.CurrentTreatmentWeek.currentWeekInstance;
import static org.motechproject.whp.patient.domain.TreatmentStartCriteria.shouldStartOrRestartTreatment;

@Service
public class WHPAdherenceService {

    AllPatients allPatients;
    AdherenceService adherenceService;
    PatientService patientService;
    AdherenceAuditService adherenceAuditService;

    @Autowired
    public WHPAdherenceService(AdherenceService adherenceService,
                               AllPatients allPatients,
                               PatientService patientService,
                               AdherenceAuditService adherenceAuditService
    ) {
        this.adherenceService = adherenceService;
        this.allPatients = allPatients;
        this.patientService = patientService;
        this.adherenceAuditService = adherenceAuditService;
    }

    public void recordAdherence(String patientId, WeeklyAdherence weeklyAdherence, AuditParams auditParams) {
        Patient patient = allPatients.findByPatientId(patientId);
        weeklyAdherence.setTbId(patient.tbId());
        weeklyAdherence.setProviderId(patient.providerId());

        List<AdherenceData> requests = requests(weeklyAdherence);
        adherenceService.saveOrUpdateAdherence(requests);
        if (shouldStartOrRestartTreatment(patient, weeklyAdherence)) {
            //implicitly sets startDate to null if no dose has been taken. this is intended.
            patientService.startTherapy(patientId, weeklyAdherence.firstDoseTakenOn());
        }
        adherenceAuditService.log(weeklyAdherence, auditParams);
    }

    public WeeklyAdherence currentWeekAdherence(Patient patient) {
        TreatmentWeek treatmentWeek = currentWeekInstance();
        AdherenceRecords adherenceRecords = adherenceService.adherenceRecords(patient.getPatientId(),
                patient.currentTreatmentId(),
                treatmentWeek.startDate(),
                treatmentWeek.endDate());

        if (adherenceRecords.size() > 0) {
            return new WeeklyAdherenceMapper(treatmentWeek, adherenceRecords).map();
        } else {
            return null;
        }
    }

    public List<Adherence> allAdherenceData(int pageNumber, int pageSize) {
        List<AdherenceData> adherenceData = adherenceService.adherenceLogs(DateUtil.today(), pageNumber, pageSize);
        return new AdherenceMapper().map(adherenceData);
    }

    public void addOrUpdateLogsByDoseDate(List<Adherence> adherences, String patientId) {
        List<AdherenceData> adherenceData = requests(adherences);
        adherenceService.addOrUpdateLogsByDoseDate(adherenceData, patientId);
    }

    public List<Adherence> findLogsInRange(String patientId, String treatmentId, LocalDate start, LocalDate end) {
        List<AdherenceData> adherenceData = adherenceService.findLogsInRange(patientId, treatmentId, start, end);
        return new AdherenceMapper().map(adherenceData);
    }

    private List<AdherenceData> requests(List<Adherence> adherences) {
        List<AdherenceData> adherenceData = new ArrayList<>();
        for (Adherence adherence : adherences)
            adherenceData.add(AdherenceDataMapper.request(adherence));
        return adherenceData;
    }

    private List<AdherenceData> requests(WeeklyAdherence weeklyAdherence) {
        List<AdherenceData> requests = new ArrayList<AdherenceData>();
        for (Adherence adherence : weeklyAdherence.getAdherenceLogs()) {
            requests.add(AdherenceDataMapper.request(adherence));
        }
        return requests;
    }
}
