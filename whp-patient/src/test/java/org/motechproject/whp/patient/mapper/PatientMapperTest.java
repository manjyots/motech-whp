package org.motechproject.whp.patient.mapper;

import org.junit.Test;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.builder.PatientRequestBuilder;
import org.motechproject.whp.patient.builder.TreatmentUpdateRequestBuilder;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.contract.TreatmentUpdateRequest;
import org.motechproject.whp.patient.domain.*;

import static org.junit.Assert.*;
import static org.motechproject.whp.patient.mapper.PatientMapper.*;

public class PatientMapperTest {

    @Test
    public void shouldMapPatientRequestToPatientDomain() {
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();
        Patient patient = mapPatient(patientRequest);
        assertBasicPatientInfo(patient, patientRequest);
        assertProvidedTreatment(patient, patientRequest);
    }

    @Test
    public void shouldMapWeightStatisticsAsEmpty_WhenMissing() {
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .withWeightStatistics(null, null, DateUtil.today())
                .build();
        Patient patient = mapPatient(patientRequest);

        assertBasicPatientInfo(patient, patientRequest);

        ProvidedTreatment providedTreatment = patient.latestProvidedTreatment();
        assertEquals(0, providedTreatment.getTreatment().getWeightStatisticsList().size());
    }

    @Test
    public void mapProvidedTreatmentSetsStartDateOnProvidedTreatment() {
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withCaseId("caseId")
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();
        Treatment treatment = TreatmentMapper.map(patientRequest);
        ProvidedTreatment providedTreatment = mapProvidedTreatment(patientRequest, treatment);

        assertNotNull(providedTreatment.getStartDate());
    }


    @Test
    public void newProviderTreatmentForCategoryChange_RetainsOldProviderIdAndAddress_SetsNewTbId_SetsNewTreatment_SetsStartDate() {
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();
        Patient patient = mapPatient(patientRequest);

        ProvidedTreatment currentProvidedTreatment = patient.getCurrentProvidedTreatment();

        TreatmentUpdateRequest openNewTreatmentUpdateRequest = TreatmentUpdateRequestBuilder.startRecording()
                .withMandatoryFieldsForOpenNewTreatment()
                .withDateModified(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .withTbId("newTbId")
                .build();

        Treatment newTreatment = TreatmentMapper.createNewTreatment(patient, openNewTreatmentUpdateRequest);

        ProvidedTreatment newProvidedTreatment = createNewProvidedTreatmentForTreatmentCategoryChange(patient, openNewTreatmentUpdateRequest, newTreatment);

        assertNotSame(currentProvidedTreatment.getTbId(), newProvidedTreatment.getTbId());
        assertNotSame(currentProvidedTreatment.getTreatment(), newProvidedTreatment.getTreatment());
        assertEquals(openNewTreatmentUpdateRequest.getTb_id(), newProvidedTreatment.getTbId());
        assertEquals(currentProvidedTreatment.getPatientAddress(), newProvidedTreatment.getPatientAddress());
        assertEquals(openNewTreatmentUpdateRequest.getProvider_id(), newProvidedTreatment.getProviderId());
    }

    private Patient mapPatient(PatientRequest patientRequest) {
        Patient patient = mapBasicInfo(patientRequest);
        Treatment treatment = TreatmentMapper.map(patientRequest);
        ProvidedTreatment providedTreatment = mapProvidedTreatment(patientRequest, treatment);
        patient.addProvidedTreatment(providedTreatment, patientRequest.getDate_modified());
        return patient;
    }

    private void assertBasicPatientInfo(Patient patient, PatientRequest patientRequest) {
        assertEquals(patientRequest.getCase_id(), patient.getPatientId());
        assertEquals(patientRequest.getFirst_name(), patient.getFirstName());
        assertEquals(patientRequest.getLast_name(), patient.getLastName());
        assertEquals(patientRequest.getGender(), patient.getGender());
        assertEquals(patientRequest.getPatient_type(), patient.getPatientType());
        assertEquals(patientRequest.getMobile_number(), patient.getPhoneNumber());
        assertEquals(patientRequest.getPhi(), patient.getPhi());
    }

    private void assertProvidedTreatment(Patient patient, PatientRequest patientRequest) {
        ProvidedTreatment providedTreatment = patient.latestProvidedTreatment();
        assertEquals(patientRequest.getTb_id(), providedTreatment.getTbId());
        assertEquals(patientRequest.getProvider_id(), providedTreatment.getProviderId());

        assertEquals(patientRequest.getAddress(), providedTreatment.getPatientAddress());

        assertTreatment(patient, patientRequest);
    }

    private void assertTreatment(Patient patient, PatientRequest patientRequest) {
        Treatment treatment = patient.latestProvidedTreatment().getTreatment();
        assertEquals(patientRequest.getAge(), treatment.getPatientAge());
        assertEquals(patientRequest.getTreatment_category(), treatment.getTreatmentCategory());
        assertNull(treatment.getStartDate());

        assertEquals(patientRequest.getTb_registration_number(), treatment.getTbRegistrationNumber());
        assertEquals(patientRequest.getTreatmentStartDate(), treatment.getCreationDate());

        assertSmearTests(patientRequest, treatment);
        assertWeightStatistics(patientRequest, treatment);

    }

    private void assertSmearTests(PatientRequest patientRequest, Treatment treatment) {
        SmearTestResults smearTestResults = patientRequest.getSmearTestResults();
        assertEquals(smearTestResults.getSmear_sample_instance(), treatment.getSmearTestResults().get(0).getSmear_sample_instance());
        assertEquals(smearTestResults.getSmear_test_result_1(), treatment.getSmearTestResults().get(0).getSmear_test_result_1());
        assertEquals(smearTestResults.getSmear_test_date_1(), treatment.getSmearTestResults().get(0).getSmear_test_date_1());
        assertEquals(smearTestResults.getSmear_test_result_2(), treatment.getSmearTestResults().get(0).getSmear_test_result_2());
        assertEquals(smearTestResults.getSmear_test_date_2(), treatment.getSmearTestResults().get(0).getSmear_test_date_2());
    }

    private void assertWeightStatistics(PatientRequest patientRequest, Treatment treatment) {
        assertEquals(patientRequest.getWeightStatistics(), treatment.getWeightStatisticsList().get(0));
    }
}
