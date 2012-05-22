package org.motechproject.whp.importer.csv;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.builder.PatientWebRequestBuilder;
import org.motechproject.whp.patient.domain.Provider;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.repository.AllProviders;
import org.motechproject.whp.patient.repository.AllTreatments;
import org.motechproject.whp.request.PatientWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext.xml")
public class PatientRecordImporterTest {

    @Autowired
    PatientRecordImporter patientRecordImporter;

    @Autowired
    AllPatients allPatients;

    @Autowired
    AllProviders allProviders;

    @Autowired
    private AllTreatments allTreatments;

    private final String PROVIDER_ID = "providerId";
    private Provider defaultProvider;

    @Before
    public void setUpDefaultProvider() {
        defaultProvider = new Provider(PROVIDER_ID, "1234567890", "chambal", DateUtil.now());
        allProviders.add(defaultProvider);
    }

    @After
    public void tearDown() {
        allPatients.removeAll();
        allProviders.remove(defaultProvider);
        allTreatments.removeAll();
    }


    @Test
    public void shouldStoreOnlyValidPatientData() {
        assertNull(allPatients.findByPatientId("12345"));

        PatientWebRequest patientWebRequest1 = new PatientWebRequestBuilder().withDefaults().withCaseId("12344").build();
        PatientWebRequest patientWebRequest2 = new PatientWebRequestBuilder().withDefaults().withProviderId(PROVIDER_ID).withCaseId("12345").build();

        patientRecordImporter.post(Arrays.asList((Object) patientWebRequest1, patientWebRequest2));

        assertNotNull(allPatients.findByPatientId("12345"));
        assertEquals(1,allPatients.getAll().size());
    }
}
