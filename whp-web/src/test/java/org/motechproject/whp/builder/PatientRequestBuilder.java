package org.motechproject.whp.builder;

import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.domain.PatientType;
import org.motechproject.whp.patient.domain.WeightInstance;
import org.motechproject.whp.request.PatientRequest;

public class PatientRequestBuilder {

    private PatientRequest patientRequest = new PatientRequest();

    public PatientRequestBuilder withDefaults() {

        patientRequest = new PatientRequest()
                .setPatientInfo("1234567890", "Foo", "Bar", "M", PatientType.PHSTransfer.name(), "12345667890")
                .setPatientAddress("house number", "landmark", "block", "village", "district", "state")
                .setSmearTestResults("PreTreatment", "19/07/1888", "result1", "PreTreatment", "21/09/1985", "result2")
                .setWeightStatistics(WeightInstance.PreTreatment.name(), "99.7")
                .setTreatmentData("01", "providerId01seq1", "123456", "P", "200", "registrationNumber");
        patientRequest.setDate_modified(DateUtil.now().toString("dd/MM/YYYY HH:mm:ss"));

        return this;
    }

    public PatientRequest build() {
        return patientRequest;
    }

    public PatientRequestBuilder withCaseId(String caseId) {
        patientRequest.setCase_id(caseId);
        return this;
    }

    public PatientRequestBuilder withLastModifiedDate(String lastModifiedDate) {
        patientRequest.setDate_modified(lastModifiedDate);
        return this;
    }

    public PatientRequestBuilder withProviderId(String providerId) {
        patientRequest.setProvider_id(providerId);
        return this;
    }

    public PatientRequestBuilder withSmearTestDate1(String smearTestDate1) {
        patientRequest.setSmear_test_date_1(smearTestDate1);
        return this;
    }

    public PatientRequestBuilder withSmearTestDate2(String smearTestDate2) {
        patientRequest.setSmear_test_date_2(smearTestDate2);
        return this;
    }
}
