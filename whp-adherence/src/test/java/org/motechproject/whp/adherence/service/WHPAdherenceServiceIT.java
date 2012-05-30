package org.motechproject.whp.adherence.service;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.adherence.repository.AllAdherenceLogs;
import org.motechproject.model.DayOfWeek;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.adherence.audit.AllAuditLogs;
import org.motechproject.whp.adherence.audit.AuditParams;
import org.motechproject.whp.adherence.builder.WeeklyAdherenceBuilder;
import org.motechproject.whp.adherence.domain.AdherenceSource;
import org.motechproject.whp.adherence.domain.PillStatus;
import org.motechproject.whp.adherence.domain.WeeklyAdherence;
import org.motechproject.whp.patient.builder.PatientRequestBuilder;
import org.motechproject.whp.patient.command.UpdateCommandFactory;
import org.motechproject.whp.patient.command.UpdateScope;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.repository.AllTreatments;
import org.motechproject.whp.patient.service.PatientService;
import org.motechproject.whp.refdata.domain.PatientType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;
import static org.motechproject.whp.adherence.util.AssertAdherence.areSame;


@ContextConfiguration(locations = "classpath*:/applicationWHPAdherenceContext.xml")
public class WHPAdherenceServiceIT extends SpringIntegrationTest {

    public static final String PATIENT_ID = "patientId";
    public static final String NEW_PROVIDER_ID = "newProviderId";
    public static final String NEW_TB_ID = "newTbId";
    public static final String OLD_TB_ID = "elevenDigit";
    public static final String OLD_PROVIDER_ID = "123456";
    public static final String TB_ID = "elevenDigit";

    LocalDate today = DateUtil.newDate(2012, 5, 3);

    @Autowired
    @Qualifier(value = "whpDbConnector")
    CouchDbConnector couchDbConnector;

    @Autowired
    @Qualifier(value = "adherenceDbConnector")
    CouchDbConnector adherenceDbConnector;
    @Autowired
    private WHPAdherenceService adherenceService;

    @Autowired
    private PatientService patientService;
    @Autowired
    private AllAdherenceLogs allAdherenceLogs;
    @Autowired
    private AllPatients allPatients;
    @Autowired
    private AllTreatments allTreatments;
    @Autowired
    private AllAuditLogs allAuditLogs;
    @Autowired
    private UpdateCommandFactory factory;

    private final AuditParams auditParams = new AuditParams("user", AdherenceSource.WEB, "remarks");


    @Before
    public void setup() {
        mockCurrentDate(today);
    }

    @Test
    public void shouldRecordAdherenceForPatient() {
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withCaseId(PATIENT_ID)
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();
        patientService.createPatient(patientRequest);

        Patient patient = allPatients.findByPatientId(PATIENT_ID);
        WeeklyAdherence adherence = new WeeklyAdherenceBuilder().withDefaultLogs().forPatient(patient).build();

        adherenceService.recordAdherence(PATIENT_ID, adherence, auditParams);
        assertArrayEquals(
                adherence.getAdherenceLogs().toArray(),
                adherenceService.currentWeekAdherence(patient).getAdherenceLogs().toArray()
        );
    }

    @Test
    public void shouldSetLatestTbIdAndProviderIdOnAdherenceUpdate() {
        createPatientAndRecordAdherence();
        assertTbAndProviderId(OLD_TB_ID, OLD_PROVIDER_ID);

        PatientRequest patientRequest = createCloseTreatmentRequest();
        factory.updateFor(UpdateScope.closeTreatment).apply(patientRequest);

        patientRequest = createChangeProviderRequest(NEW_PROVIDER_ID, NEW_TB_ID);
        factory.updateFor(UpdateScope.transferIn).apply(patientRequest);

        WeeklyAdherence updatedAdherence = new WeeklyAdherenceBuilder().withLog(DayOfWeek.Monday, PillStatus.NotTaken).forPatient(allPatients.findByPatientId(PATIENT_ID)).build();

        adherenceService.recordAdherence(PATIENT_ID, updatedAdherence, auditParams);
        assertTbAndProviderId(NEW_TB_ID, NEW_PROVIDER_ID);
    }

    private void assertTbAndProviderId(String expectedTbId, String expectedProviderId) {
        assertEquals(expectedTbId, adherenceService.currentWeekAdherence(allPatients.findByPatientId(PATIENT_ID)).getAdherenceLogs().get(0).getTbId());
        assertEquals(expectedProviderId, adherenceService.currentWeekAdherence(allPatients.findByPatientId(PATIENT_ID)).getAdherenceLogs().get(0).getProviderId());
    }

    private void createPatientAndRecordAdherence() {
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withTbId(TB_ID)
                .withCaseId(PATIENT_ID)
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();
        patientService.createPatient(patientRequest);

        Patient patient = allPatients.findByPatientId(PATIENT_ID);
        WeeklyAdherence adherence = new WeeklyAdherenceBuilder().withLog(DayOfWeek.Monday, PillStatus.Taken).forPatient(patient).build();

        adherenceService.recordAdherence(PATIENT_ID, adherence, auditParams);
    }

    private PatientRequest createChangeProviderRequest(String newProviderId, String newTbId) {
        Patient patient = allPatients.findByPatientId(PATIENT_ID);
        Treatment latestTreatment = patient.latestTreatment();

        return new PatientRequestBuilder()
                .withMandatoryFieldsForTransferInTreatment()
                .withCaseId(PATIENT_ID)
                .withProviderId(newProviderId)
                .withDateModified(DateTime.now())
                .withDiseaseClass(latestTreatment.getDiseaseClass())
                .withTreatmentCategory(latestTreatment.getTreatmentCategory())
                .withTbId(newTbId)
                .build();
    }

    private PatientRequest createCloseTreatmentRequest() {
        return new PatientRequestBuilder()
                .withMandatoryFieldsForCloseTreatment()
                .withCaseId(PATIENT_ID)
                .withDateModified(DateTime.now())
                .withTbId(TB_ID)
                .build();
    }

    @Test
    public void shouldStartPatientOnTreatmentAfterRecordingAdherenceForTheFirstTime() {
        WeeklyAdherence adherence = adherenceIsRecordedForTheFirstTime();
        assertEquals(
                adherence.firstDoseTakenOn(),
                allPatients.findByPatientId(PATIENT_ID).getCurrentProvidedTreatment().getTreatment().getStartDate()
        );
    }

    @Test
    public void shouldNotStartPatientOnTreatmentWhenRecordingAdherenceForAnyWeekSubsequentToFirstEverAdherenceCapturedWeek() {
        WeeklyAdherence adherence = adherenceIsRecordedForTheFirstTime(); //capturing adherence for last week -> this is the first ever adherence captured week
        mockCurrentDate(today.plusDays(3)); //moving to the sunday -> capturing adherence for this week -> subsequent to first ever adherence captured week
        adherenceService.recordAdherence(PATIENT_ID, new WeeklyAdherence(), auditParams);
        assertEquals(
                adherence.firstDoseTakenOn(),
                allPatients.findByPatientId(PATIENT_ID).getCurrentProvidedTreatment().getTreatment().getStartDate()
        );
    }

    @Test
    public void shouldResetDoseStartDateWhenNoAdherenceIsPostedForSameWeek() {
        adherenceIsRecordedForTheFirstTime();
        mockCurrentDate(today.plusDays(1));
        adherenceService.recordAdherence(PATIENT_ID, new WeeklyAdherence(), auditParams);
        assertNull(allPatients.findByPatientId(PATIENT_ID).getCurrentProvidedTreatment().getTreatment().getStartDate());
    }

    @Test
    public void shouldReturnAdherenceWhenCurrentWeekAdherenceIsCaptured() {
        PatientRequest withDosesOnMonWedFri = new PatientRequestBuilder()
                .withDefaults()
                .withCaseId(PATIENT_ID)
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();
        patientService.createPatient(withDosesOnMonWedFri);

        Patient patient = allPatients.findByPatientId(PATIENT_ID);
        WeeklyAdherence expectedAdherence = new WeeklyAdherenceBuilder()
                .withDefaultLogs()
                .forPatient(patient)
                .build();
        adherenceService.recordAdherence(withDosesOnMonWedFri.getCase_id(), expectedAdherence, auditParams);

        WeeklyAdherence adherence = adherenceService.currentWeekAdherence(patient);
        areSame(expectedAdherence, adherence);
    }

    @Test
    public void shouldReturnNullWhenCurrentWeekAdherenceIsNotCaptured() {
        PatientRequest withDosesOnMonWedFri = new PatientRequestBuilder().withDefaults()
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();
        patientService.createPatient(withDosesOnMonWedFri);
        Patient patient = allPatients.findByPatientId(withDosesOnMonWedFri.getCase_id());

        assertNull(adherenceService.currentWeekAdherence(patient));
    }

    @Test
    public void shouldLogAuditAfterRecordingAdherence() {
        PatientRequest withDosesOnMonWedFri = new PatientRequestBuilder().withDefaults()
                .withCaseId(PATIENT_ID)
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .withProviderId("providerId")
                .withTbId("tbId")
                .build();

        patientService.createPatient(withDosesOnMonWedFri);
        Patient patient = allPatients.findByPatientId(withDosesOnMonWedFri.getCase_id());
        WeeklyAdherence adherence = new WeeklyAdherenceBuilder().withLog(DayOfWeek.Monday, PillStatus.Taken).forPatient(patient).build();

        adherenceService.recordAdherence(PATIENT_ID, adherence, auditParams);
        assertEquals(1, allAuditLogs.getAll().size());
    }

    @After
    public void tearDown() {
        super.tearDown();
        deleteAdherenceLogs();
        markForDeletion(allPatients.getAll().toArray());
        markForDeletion(allAuditLogs.getAll().toArray());
        markForDeletion(allTreatments.getAll().toArray());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }

    private WeeklyAdherence adherenceIsRecordedForTheFirstTime() {
        WeeklyAdherence adherence = new WeeklyAdherenceBuilder().withDefaultLogs().build();
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withCaseId(PATIENT_ID)
                .withPatientType(PatientType.New)
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();

        patientService.createPatient(patientRequest);
        adherenceService.recordAdherence(PATIENT_ID, adherence, auditParams);
        return adherence;
    }

    private void deleteAdherenceLogs() {
        for (Object log : allAdherenceLogs.getAll().toArray()) {
            adherenceDbConnector.delete(log);
        }
    }

}