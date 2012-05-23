package org.motechproject.whp.patient.service;

import org.joda.time.LocalDate;
import org.motechproject.whp.patient.command.AllCommands;
import org.motechproject.whp.patient.command.TreatmentUpdate;
import org.motechproject.whp.patient.command.TreatmentUpdateFactory;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.ProvidedTreatment;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.exception.WHPErrorCode;
import org.motechproject.whp.patient.exception.WHPRuntimeException;
import org.motechproject.whp.patient.mapper.TreatmentMapper;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.repository.AllTreatments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.motechproject.whp.patient.domain.criteria.UpdatePatientCriteria.canPerformSimpleUpdate;
import static org.motechproject.whp.patient.mapper.PatientMapper.*;

@Service
public class PatientService {

    private AllTreatments allTreatments;
    private AllPatients allPatients;
    private TreatmentUpdateFactory factory;

    @Autowired
    public PatientService(AllPatients allPatients, AllTreatments allTreatments, TreatmentUpdateFactory factory) {
        this.allPatients = allPatients;
        this.allTreatments = allTreatments;
        this.factory = factory;
    }

    public void createPatient(PatientRequest patientRequest) {
        Patient patient = mapBasicInfo(patientRequest);

        Treatment treatment = TreatmentMapper.map(patientRequest);
        allTreatments.add(treatment);

        ProvidedTreatment providedTreatment = mapProvidedTreatment(patientRequest, treatment);
        patient.addProvidedTreatment(providedTreatment, patientRequest.getDate_modified());
        allPatients.add(patient);
    }

    public void update(String updateCommand, PatientRequest patientRequest) {
        if (updateCommand.equals(AllCommands.simpleUpdate)) {
            simpleUpdate(patientRequest);
        } else {
            performTreatmentUpdate(patientRequest);
        }
    }

    void simpleUpdate(PatientRequest patientRequest) {
        Patient patient = allPatients.findByPatientId(patientRequest.getCase_id());
        ArrayList<WHPErrorCode> errorCodes = new ArrayList<WHPErrorCode>();
        if (canPerformSimpleUpdate(patient, patientRequest, errorCodes)) {
            Patient updatedPatient = mapUpdates(patientRequest, patient);
            allPatients.update(updatedPatient);
        } else {
            throw new WHPRuntimeException(errorCodes);
        }
    }

    public void performTreatmentUpdate(PatientRequest patientRequest) {
        TreatmentUpdate treatmentUpdate = factory.updateFor(patientRequest.getTreatment_update());
        treatmentUpdate.apply(patientRequest);
    }

    public void startTreatment(String patientId, LocalDate firstDoseTakenDate) {
        Patient patient = allPatients.findByPatientId(patientId);
        patient.getCurrentProvidedTreatment().getTreatment().setStartDate(firstDoseTakenDate);
        allPatients.update(patient);
    }
}
