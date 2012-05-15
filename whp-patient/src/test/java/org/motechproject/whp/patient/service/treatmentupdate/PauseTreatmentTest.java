package org.motechproject.whp.patient.service.treatmentupdate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.matchers.Contains;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.builder.TreatmentUpdateRequestBuilder;
import org.motechproject.whp.patient.contract.TreatmentUpdateRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.exception.WHPDomainException;
import org.motechproject.whp.patient.repository.AllPatients;

import static org.mockito.Mockito.*;

public class PauseTreatmentTest {

    private AllPatients allPatients;
    private PauseTreatment pauseTreatment;
    private Patient patient;
    @Rule
    public ExpectedException exceptionThrown = ExpectedException.none();

    @Before
    public void setUp() {
        allPatients = mock(AllPatients.class);
        patient = new PatientBuilder().withDefaults().build();
        pauseTreatment = new PauseTreatment();
    }

    @Test
    public void shouldNotPauseCurrentTreatment_OnAnyErrors() {
        TreatmentUpdateRequest treatmentUpdateRequest = TreatmentUpdateRequestBuilder.startRecording().withDefaults().withTbId("wrongTbId").build();
        expectWHPDomainException("Cannot pause treatment for this case: [No such tb id for current treatment]");
        when(allPatients.findByPatientId(treatmentUpdateRequest.getCase_id())).thenReturn(patient);

        pauseTreatment.apply(allPatients, null, treatmentUpdateRequest);
        verify(allPatients, never()).update(patient);
    }

    @Test
    public void shouldPauseAndUpdatePatientCurrentTreatment_IfNoErrorsFound() {
        TreatmentUpdateRequest treatmentUpdateRequest = TreatmentUpdateRequestBuilder.startRecording().withDefaults().build();
        when(allPatients.findByPatientId(treatmentUpdateRequest.getCase_id())).thenReturn(patient);

        pauseTreatment.apply(allPatients, null, treatmentUpdateRequest);
        verify(allPatients).update(patient);
    }

    protected void expectWHPDomainException(String message) {
        exceptionThrown.expect(WHPDomainException.class);
        exceptionThrown.expectMessage(new Contains(message));
    }
}