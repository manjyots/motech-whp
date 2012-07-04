package org.motechproject.whp.applicationservice.orchestrator.phaseUpdateOrchestrator.part;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.adherence.contract.AdherenceRecord;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.refdata.domain.PhaseName;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SetNextPhaseTestPart extends PhaseUpdateOrchestratorTestPart {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void shouldNotEndCurrentPhaseWhenPatientHasNotTakenAllDosesForCurrentPhase() {
        phaseUpdateOrchestrator.setNextPhase(PATIENT_ID, PhaseName.EIP);

        verify(patientService, times(1)).setNextPhaseName(patient.getPatientId(), PhaseName.EIP);
        verifyNoMoreInteractions(patientService);
        verifyZeroInteractions(whpAdherenceService);
    }

    @Test
    public void shouldEndCurrentPhaseWhenPatientHasTakenAllDosesForCurrentPhase() {
        LocalDate therapyStartDate = new LocalDate(2012, 3, 1);
        LocalDate twentyFourthDoseTakenDate = new LocalDate(2012, 5, 11);
        patient.startTherapy(therapyStartDate);
        patient.setNumberOfDosesTaken(PhaseName.IP, 24);

        AdherenceRecord adherenceRecord = new AdherenceRecord(patient.getPatientId(), THERAPY_ID, twentyFourthDoseTakenDate);
        when(whpAdherenceService.nThTakenDose(patient.getPatientId(), THERAPY_ID, 24, therapyStartDate)).thenReturn(adherenceRecord);

        phaseUpdateOrchestrator.setNextPhase(patient.getPatientId(), PhaseName.EIP);

        verify(patientService, times(1)).setNextPhaseName(patient.getPatientId(), PhaseName.EIP);
        verify(patientService).autoCompleteCurrentPhase(patient, twentyFourthDoseTakenDate);
    }

    @Test
    public void shouldSetNextPhaseAndStartNextPhase_PatientIsTransitioning() {
        LocalDate therapyStartDate = new LocalDate(2012, 3, 1);
        LocalDate twentyFourthDoseTakenDate = new LocalDate(2012, 5, 11);

        patient.startTherapy(therapyStartDate);
        patient.setNumberOfDosesTaken(PhaseName.IP, 3);
        patient.nextPhaseName(PhaseName.EIP);
        patient.endCurrentPhase(twentyFourthDoseTakenDate);

        phaseUpdateOrchestrator.setNextPhase(patient.getPatientId(), PhaseName.EIP);

        verify(patientService, times(1)).setNextPhaseName(patient.getPatientId(), PhaseName.EIP);
        verify(whpAdherenceService, never()).nThTakenDose(anyString(), anyString(), anyInt(), any(LocalDate.class));
        verify(patientService, never()).autoCompleteCurrentPhase(any(Patient.class), any(LocalDate.class));
        verify(patientService).startNextPhase(patient);
    }
}