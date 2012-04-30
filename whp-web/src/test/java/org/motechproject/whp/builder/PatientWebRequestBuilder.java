package org.motechproject.whp.builder;

import org.motechproject.whp.patient.domain.*;
import org.motechproject.whp.request.PatientWebRequest;

public class PatientWebRequestBuilder {

    private PatientWebRequest patientWebRequest = new PatientWebRequest();

    public PatientWebRequestBuilder withDefaults() {

        patientWebRequest = new PatientWebRequest()
                .setPatientInfo("1234567890", "Foo", "Bar", Gender.M.getValue(), PatientType.PHSTransfer.name(), "1234567890", "phi")
                .setPatientAddress("house number", "landmark", "block", "village", "district", "state")
                .setSmearTestResults("PreTreatment", "19/07/2000", SmearTestResult.Positive.name(), "21/09/2000", SmearTestResult.Positive.name())
                .setWeightStatistics(WeightInstance.PreTreatment.name(), "99.7")
                .setTreatmentData("01", "12345678901", "123456", "P", "40", "registrationNumber");
        patientWebRequest.setDate_modified("10/10/2010 10:10:10");
        return this;
    }

    public PatientWebRequestBuilder withSimpleUpdateFields() {
        patientWebRequest = new PatientWebRequest()
                .setPatientInfo("1234567890", null, null, null, null, "9087654321", null)
                .setPatientAddress("new_house number", "new_landmark", "new_block", "new_village", "new_district", "new_state")
                .setSmearTestResults(SmearTestSampleInstance.EndTreatment.name(), "19/07/2010", SmearTestResult.Negative.name(), "21/09/2010", SmearTestResult.Negative.name())
                .setWeightStatistics(WeightInstance.EndTreatment.name(), "99.7")
                .setTreatmentData(null, null, null, null, "50", null);
        patientWebRequest.setDate_modified("15/10/2010 10:10:10");
        return this;
    }

    public PatientWebRequest build() {
        return patientWebRequest;
    }

    public PatientWebRequestBuilder withCaseId(String caseId) {
        patientWebRequest.setCase_id(caseId);
        return this;
    }

    public PatientWebRequestBuilder withLastModifiedDate(String lastModifiedDate) {
        patientWebRequest.setDate_modified(lastModifiedDate);
        return this;
    }

    public PatientWebRequestBuilder withProviderId(String providerId) {
        patientWebRequest.setProvider_id(providerId);
        return this;
    }

    public PatientWebRequestBuilder withTreatmentCategory(String category) {
        patientWebRequest.setTreatment_category(category);
        return this;
    }

    public PatientWebRequestBuilder withSmearTestDate1(String smearTestDate1) {
        patientWebRequest.setSmear_test_date_1(smearTestDate1);
        return this;
    }

    public PatientWebRequestBuilder withSmearTestDate2(String smearTestDate2) {
        patientWebRequest.setSmear_test_date_2(smearTestDate2);
        return this;
    }

    public PatientWebRequestBuilder withMobileNumber(String mobileNumber) {
        patientWebRequest.setMobile_number(mobileNumber);
        return this;
    }

    public PatientWebRequestBuilder withSmearTestResult1(String smearTestResult) {
        patientWebRequest.setSmear_test_result_1(smearTestResult);
        return this;
    }

    public PatientWebRequestBuilder withTBId(String tbId) {
        patientWebRequest.setTb_id(tbId);
        return this;
    }

    public PatientWebRequestBuilder withGender(String gender) {
        patientWebRequest.setGender(gender);
        return this;
    }

    public PatientWebRequestBuilder withAge(String age) {
        patientWebRequest.setAge(age);
        return this;
    }

    public PatientWebRequestBuilder withWeight(String weight) {
        patientWebRequest.setWeight(weight);
        return this;
    }
}