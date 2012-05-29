package org.motechproject.whp.importer.csv.mapper;

import org.dozer.DozerBeanMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.DayOfWeek;
import org.motechproject.whp.importer.csv.builder.ImportPatientRequestBuilder;
import org.motechproject.whp.importer.csv.request.ImportPatientRequest;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.TreatmentCategory;
import org.motechproject.whp.patient.repository.AllTreatmentCategories;
import org.motechproject.whp.refdata.domain.SmearTestSampleInstance;
import org.motechproject.whp.refdata.domain.WHPConstants;
import org.motechproject.whp.refdata.domain.WeightInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationDataImporterContext.xml")
public class ImportPatientRequestMapperTest {
    @Autowired
    ImportPatientRequestMapper importPatientRequestMapper;

    @Autowired
    AllTreatmentCategories allTreatmentCategories;

    @Test
    public void shouldMapImportPatientRequestToPatientRequest() {
        ImportPatientRequest importPatientRequest = new ImportPatientRequestBuilder().withDefaults().build();
        PatientRequest patientRequest = importPatientRequestMapper.map(importPatientRequest);

        assertEquals(importPatientRequest.getCase_id(), patientRequest.getCase_id());
        assertEquals(importPatientRequest.getPatient_type(), patientRequest.getPatient_type().name());
        assertEquals(importPatientRequest.getAddress_block(), patientRequest.getAddress().getAddress_block());
        assertEquals(importPatientRequest.getAddress_district(), patientRequest.getAddress().getAddress_district());
        assertEquals(importPatientRequest.getAddress_landmark(), patientRequest.getAddress().getAddress_landmark());
        assertEquals(importPatientRequest.getAddress_location(), patientRequest.getAddress().getAddress_location());
        assertEquals(importPatientRequest.getAddress_state(), patientRequest.getAddress().getAddress_state());
        assertEquals(importPatientRequest.getAddress_village(), patientRequest.getAddress().getAddress_village());
        assertEquals(importPatientRequest.getAge(), patientRequest.getAge().toString());
        assertEquals(importPatientRequest.getDate_modified(), patientRequest.getDate_modified().toString(WHPConstants.DATE_TIME_FORMAT));
        assertEquals(importPatientRequest.getDisease_class(), patientRequest.getDisease_class().name());
        assertEquals(importPatientRequest.getFirst_name(), patientRequest.getFirst_name());
        assertEquals(importPatientRequest.getGender(), patientRequest.getGender().name());
        assertEquals(importPatientRequest.getLast_name(), patientRequest.getLast_name());
        assertEquals(importPatientRequest.getMobile_number(), patientRequest.getMobile_number());
        assertEquals(importPatientRequest.getPhi(), patientRequest.getPhi());
        assertEquals(importPatientRequest.getTb_id(), patientRequest.getTb_id());
        assertEquals(importPatientRequest.getTb_registration_number(), patientRequest.getTb_registration_number());
        assertEquals(importPatientRequest.getWeight(WeightInstance.PreTreatment), patientRequest.getWeightStatistics().get(0).getWeight().toString());
        assertEquals(importPatientRequest.getWeightDate(WeightInstance.PreTreatment), patientRequest.getWeightStatistics().get(0).getMeasuringDate().toString(WHPConstants.DATE_FORMAT));
        assertEquals(importPatientRequest.getTreatment_category(), patientRequest.getTreatment_category().getCode());
        assertEquals(importPatientRequest.getProvider_id(), patientRequest.getProvider_id());
        assertEquals(importPatientRequest.getTestDate1(SmearTestSampleInstance.PreTreatment), patientRequest.getSmearTestResults().get(0).getSmear_test_date_1().toString(WHPConstants.DATE_FORMAT));
        assertEquals(importPatientRequest.getTestDate2(SmearTestSampleInstance.PreTreatment), patientRequest.getSmearTestResults().get(0).getSmear_test_date_2().toString(WHPConstants.DATE_FORMAT));
        assertEquals(importPatientRequest.getTestResult1(SmearTestSampleInstance.PreTreatment), patientRequest.getSmearTestResults().get(0).getSmear_test_result_1().name());
        assertEquals(importPatientRequest.getTestResult2(SmearTestSampleInstance.PreTreatment), patientRequest.getSmearTestResults().get(0).getSmear_test_result_2().name());
    }

    @Before
    public void setup() {
        TreatmentCategory treatmentCategory = new TreatmentCategory("cat1", "01", 3, 12, 22, Arrays.asList(DayOfWeek.Monday));
        allTreatmentCategories.add(treatmentCategory);
    }

    @After
    public void tearDown() {
        allTreatmentCategories.removeAll();
    }
}
