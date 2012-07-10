package org.motechproject.whp.patient.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.builder.TreatmentBuilder;
import org.motechproject.whp.refdata.domain.Gender;
import org.motechproject.whp.refdata.domain.Phase;
import org.motechproject.whp.refdata.domain.TreatmentOutcome;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.motechproject.util.DateUtil.now;
import static org.motechproject.util.DateUtil.today;
import static org.motechproject.whp.common.TreatmentWeekInstance.currentWeekInstance;
import static org.motechproject.whp.patient.builder.PatientBuilder.patient;
import static org.motechproject.whp.patient.builder.TreatmentBuilder.treatment;
import static org.motechproject.whp.refdata.domain.Phase.EIP;
import static org.motechproject.whp.refdata.domain.Phase.IP;

public class PatientTest {

    @Test
    public void shouldSetTreatmentAddedAsCurrentTreatment() {
        Patient patient = patient();
        Treatment treatment = treatment();
        patient.addTreatment(treatment, now());
        assertEquals(treatment, patient.getCurrentTreatment());
    }

    @Test
    public void shouldUpdateTreatmentHistoryWhenNewTreatmentIdAdded() {
        Patient patient = patient();
        Treatment firstTreatment = patient.getCurrentTreatment();
        patient.addTreatment(treatment(), now());

        assertArrayEquals(new Object[]{firstTreatment}, patient.getTreatments().toArray());
    }

    @Test
    public void shouldNotHaveAnyHistoryWhenTreatmentHasNeverBeenUpdated() {
        Patient patientWithOneTreatment = patient();
        assertTrue(patientWithOneTreatment.getTreatments().isEmpty());
    }

    @Test
    public void shouldAddCurrentTreatmentToHistoryWhenNewTreatmentIsAdded() {
        Patient patient = patient();
        Treatment treatment = patient.getCurrentTreatment();
        patient.addTreatment(treatment(), now());

        assertArrayEquals(new Object[]{treatment}, patient.getTreatments().toArray());
    }

    @Test
    public void shouldUpdateLastModifiedDateWhenTreatmentIsClosed() {
        DateTime now = now();
        Patient patient = patient();
        patient.closeCurrentTreatment(TreatmentOutcome.Cured, now);

        assertEquals(now, patient.getLastModifiedDate());
    }

    @Test
    public void shouldUpdateLastModifiedDateWhenTreatmentIsPaused() {
        DateTime now = now();
        Patient patient = patient();
        patient.pauseCurrentTreatment("paws", now);

        assertEquals(now, patient.getLastModifiedDate());
    }

    @Test
    public void shouldUpdateLastModifiedDateWhenTreatmentIsRestarted() {
        DateTime now = now();
        Patient patient = patient();
        patient.pauseCurrentTreatment("paws", now.minusHours(2));

        patient.restartCurrentTreatment("swap", now);

        assertEquals(now, patient.getLastModifiedDate());
    }

    @Test
    public void shouldStoreIdsInLowerCase() {
        Patient patient = new Patient();
        patient.setPatientId("QWER");
        assertEquals("qwer", patient.getPatientId());

        patient = new Patient("QWER", "", "", Gender.M, "");
        assertEquals("qwer", patient.getPatientId());

        patient.setPatientId(null);
        assertEquals(null, patient.getPatientId());
    }

    @Test
    public void shouldGetCurrentTreatmentIfGivenDateIsInCurrentTreatmentPeriod() {
        Patient patient = new PatientBuilder().withDefaults().build();
        LocalDate startDate = new LocalDate(2012, 1, 2);
        LocalDate endDate = new LocalDate(2012, 3, 15);
        patient.getCurrentTreatment().setStartDate(startDate);
        patient.getCurrentTreatment().setEndDate(endDate);
        assertEquals(patient.getCurrentTreatment(), patient.getTreatment(startDate));
        assertEquals(patient.getCurrentTreatment(), patient.getTreatment(endDate));
        assertEquals(patient.getCurrentTreatment(), patient.getTreatment(startDate.plusDays(15)));
    }

    @Test
    public void settingIdsShouldHandleNullValues() {
        Patient patient = new Patient("", "", "", Gender.F, "");
        patient.setPatientId(null);
        assertEquals(null, patient.getPatientId());

        patient = new Patient(null, "", "", Gender.F, "");
        assertEquals(null, patient.getPatientId());

    }

    @Test
    public void shouldSetNextPhaseOnCurrentTherapy() {
        Patient patient = new PatientBuilder().withDefaults().build();

        patient.nextPhaseName(Phase.EIP);

        assertEquals(patient.getCurrentTherapy().getPhases().getNextPhase(), Phase.EIP);
    }

    @Test
    public void shouldEndCurrentPhaseIfNotNull() {
        Patient patient = new PatientBuilder().withDefaults().build();
        LocalDate phaseEndDate = new LocalDate(2012, 4, 1);
        patient.startTherapy(new LocalDate(2012, 3, 1));

        patient.endCurrentPhase(phaseEndDate);

        assertEquals(phaseEndDate, patient.getCurrentTherapy().getPhaseEndDate(IP));
    }

    @Test
    public void startNextPhaseShouldSetStartDateOnNextPhaseAsNextCalendarDateOfEndDateOfLastCompletedPhase() {
        Patient patient = new PatientBuilder().withDefaults().build();
        LocalDate phaseEndDate = new LocalDate(2012, 4, 1);
        patient.startTherapy(new LocalDate(2012, 3, 1));
        patient.nextPhaseName(EIP);

        patient.endCurrentPhase(phaseEndDate);
        patient.startNextPhase();

        assertEquals(phaseEndDate.plusDays(1), patient.getCurrentTherapy().getPhaseStartDate(EIP));
        assertNull(patient.getCurrentTherapy().getPhases().getNextPhase());
    }

    @Test
    public void isTransitioningShouldReturnTrueIfCurrentPhaseIsNull() {
        Patient patient = new PatientBuilder().withDefaults().build();
        patient.startTherapy(new LocalDate(2012, 3, 1));
        patient.endCurrentPhase(new LocalDate(2012, 4, 1));

        assertTrue(patient.isTransitioning());
    }

    @Test
    public void hasPhaseToTransitionToShouldReturnTrueIfNextPhaseNameIsNotNull() {
        Patient patient = new PatientBuilder().withDefaults().build();
        patient.nextPhaseName(Phase.EIP);

        assertTrue(patient.hasPhaseToTransitionTo());
    }

    @Test
    public void shouldGetCurrentTreatment_WhenDateIsTheStartDayOfCurrentTreatment() {
        Patient patient = new PatientBuilder().withDefaults().withCurrentTreatmentStartDate(date(2011, 10, 1)).build();
        patient.closeCurrentTreatment(TreatmentOutcome.Defaulted, dateTime(2011, 12, 1));

        patient.addTreatment(new TreatmentBuilder().withDefaults().build(), dateTime(2012, 1, 1));
        patient.closeCurrentTreatment(TreatmentOutcome.Defaulted, dateTime(2012, 3, 15));

        Treatment currentTreatment = new TreatmentBuilder().withDefaults().withTbId("current").build();
        patient.addTreatment(currentTreatment, dateTime(2012, 4, 1));

        assertEquals(currentTreatment, patient.getTreatment(date(2012, 4, 1)));
    }

    @Test
    public void shouldGetCurrentTreatment_WhenDateIsAfterStartDayOfCurrentTreatment() {
        Patient patient = new PatientBuilder().withDefaults().withCurrentTreatmentStartDate(date(2011, 10, 1)).build();
        patient.closeCurrentTreatment(TreatmentOutcome.Defaulted, dateTime(2011, 12, 1));

        patient.addTreatment(new TreatmentBuilder().withDefaults().build(), dateTime(2012, 1, 1));
        patient.closeCurrentTreatment(TreatmentOutcome.Defaulted, dateTime(2012, 3, 15));

        Treatment currentTreatment = new TreatmentBuilder().withDefaults().withTbId("current").build();
        patient.addTreatment(currentTreatment, dateTime(2012, 4, 1));

        assertEquals(currentTreatment, patient.getTreatment(date(2012, 4, 2)));
    }

    @Test
    public void shouldGetFirstTreatment_WhenDateIsSameAsEndDate() {
        Patient patient = new PatientBuilder().withDefaults().withCurrentTreatmentStartDate(date(2011, 10, 1)).build();
        Treatment firstTreatment = patient.getCurrentTreatment();
        patient.closeCurrentTreatment(TreatmentOutcome.Defaulted, dateTime(2011, 12, 1));

        patient.addTreatment(new TreatmentBuilder().withDefaults().build(), dateTime(2012, 1, 1));
        patient.closeCurrentTreatment(TreatmentOutcome.Defaulted, dateTime(2012, 3, 15));

        patient.addTreatment(new TreatmentBuilder().withDefaults().withTbId("current").build(), dateTime(2012, 4, 1));

        assertEquals(firstTreatment, patient.getTreatment(date(2011, 12, 1)));
    }

    @Test
    public void shouldGetLatestTreatment_WhenTreatmentIsCloseAndOpenedOnSameDay() {
        Patient patient = new PatientBuilder().withDefaults().withCurrentTreatmentStartDate(date(2011, 10, 1)).build();
        patient.closeCurrentTreatment(TreatmentOutcome.Defaulted, dateTime(2011, 12, 1));

        Treatment secondTreatment = new TreatmentBuilder().withDefaults().build();
        patient.addTreatment(secondTreatment, dateTime(2011, 12, 1));
        patient.closeCurrentTreatment(TreatmentOutcome.Defaulted, dateTime(2012, 3, 15));

        patient.addTreatment(new TreatmentBuilder().withDefaults().withTbId("current").build(), dateTime(2012, 4, 1));

        assertEquals(secondTreatment, patient.getTreatment(date(2011, 12, 1)));
    }

    @Test
    public void shouldSetOnActiveTreatmentWhenFirstTreatmentIsAdded() {
        Patient patient = patient();
        assertTrue(patient.isOnActiveTreatment());
    }

    @Test
    public void shouldSetOnActiveTreatmentWhenSubsequentTreatmentIsAdded() {
        Patient patient = patient();
        patient.setOnActiveTreatment(false);

        Treatment treatment = treatment();
        patient.addTreatment(treatment, now());

        assertTrue(patient.isOnActiveTreatment());
    }

    @Test
    public void shouldUnSetOnActiveTreatmentWhenTreatmentIsClosed() {
        Patient patient = patient();
        patient.closeCurrentTreatment(TreatmentOutcome.TreatmentCompleted, now());

        assertFalse(patient.isOnActiveTreatment());
    }

    @Test
    public void shouldReturnNumberOfRemainingDosesInLastCompletedPhase() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(today().minusMonths(5));

        patient.setNumberOfDosesTaken(Phase.IP, 22);
        patient.setNumberOfDosesTakenAsOfLastSunday(Phase.IP, 22);

        patient.endCurrentPhase(today().minusMonths(4));

        patient.nextPhaseName(Phase.EIP);
        patient.startNextPhase();

        assertEquals(2, patient.getRemainingDosesInLastCompletedPhase());
    }

    @Test
    public void shouldReturnNumberOfDosesForGivenPhase() {
        assertEquals(Integer.valueOf(24), patient().numberOfDosesForPhase(Phase.IP));
        assertEquals(Integer.valueOf(12), patient().numberOfDosesForPhase(Phase.EIP));
        assertEquals(Integer.valueOf(54), patient().numberOfDosesForPhase(Phase.CP));
    }

    @Test
    public void shouldReturnTrueIfCurrentPhaseIsDoseComplete() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(today().minusMonths(5));

        // x >= 24.
        patient.setNumberOfDosesTaken(Phase.IP, 25);

        assertTrue(patient.currentPhaseDoseComplete());
    }

    @Test
    public void shouldReturnFalseIfCurrentPhaseIsNotDoseComplete() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(today().minusMonths(5));

        patient.setNumberOfDosesTaken(Phase.IP, 22);

        assertFalse(patient.currentPhaseDoseComplete());
    }

    @Test
    public void shouldReturnNumberOfWeeksElapsedSinceStartOfTherapy() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(today().minusWeeks(5));

        assertEquals(Integer.valueOf(5), patient.getWeeksElapsed());
    }

    @Test
    public void shouldReturnTotalDosesToHaveBeenTakenTillLastSunday() {
        //patient on 3 pills a day category
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(currentWeekInstance().startDate());

        assertEquals(3, patient.getTotalDosesToHaveBeenTakenTillLastSunday());
    }

    @Test
    public void shouldReturnNumberOfDosesTakenTillLastSunday() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(today().minusWeeks(4));

        patient.setNumberOfDosesTaken(Phase.IP, 12);
        patient.setNumberOfDosesTakenAsOfLastSunday(Phase.IP, 9);

        assertEquals(9, patient.getTotalNumberOfDosesTakenTillLastSunday());
    }

    @Test
    public void shouldReturnTheCumulativeNumberOfDosesNotTakenTillLastSunday() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(currentWeekInstance().startDate().minusWeeks(20));

        patient.setNumberOfDosesTaken(Phase.IP, 24);
        patient.setNumberOfDosesTakenAsOfLastSunday(Phase.IP, 22);

        patient.endCurrentPhase(today().minusMonths(4));

        patient.nextPhaseName(Phase.EIP);
        patient.startNextPhase();

        patient.setNumberOfDosesTaken(Phase.EIP, 9);
        patient.setNumberOfDosesTakenAsOfLastSunday(Phase.EIP, 8);

        //20 weeks back + the current week (till "last" Sunday) * number of doses per week - (total number of taken doses till last sunday)
        assertEquals(21*3 - 24 - 8, patient.getCumulativeDosesNotTaken());

    }

    @Test
    public void shouldReturnZeroMissingDosesIfAdherenceHasBeenRecordedBeforeEndOfWeek_ByAdmin() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(currentWeekInstance().dateOf(DayOfWeek.Sunday));

        patient.setNumberOfDosesTaken(Phase.IP, 3);
        patient.setNumberOfDosesTakenAsOfLastSunday(Phase.IP, 3);

        //0 - 3 = 0 (in the NamNam field)
        assertEquals(0, patient.getCumulativeDosesNotTaken());
    }

    @Test
    public void shouldReturnFormattedIPProgress() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(currentWeekInstance().startDate().minusWeeks(20));

        patient.setNumberOfDosesTaken(Phase.IP, 24);

        patient.endCurrentPhase(today().minusMonths(4));

        patient.nextPhaseName(Phase.EIP);
        patient.startNextPhase();

        patient.setNumberOfDosesTaken(Phase.EIP, 9);

        //(24:IP + 9:EIP) / (24:IP + 12:EIP)
        assertEquals("33/36 (91.67%)", patient.getIPProgress());
    }

    @Test
    public void shouldReturnFormattedCPProgress() {
        Patient patient = PatientBuilder.patient();
        patient.startTherapy(currentWeekInstance().startDate().minusWeeks(20));
        patient.setNumberOfDosesTaken(Phase.IP, 24);

        patient.endCurrentPhase(today().minusMonths(4));

        patient.nextPhaseName(Phase.EIP);
        patient.startNextPhase();
        patient.setNumberOfDosesTaken(Phase.EIP, 9);

        patient.endCurrentPhase(today().minusMonths(3));

        patient.nextPhaseName(Phase.CP);
        patient.startNextPhase();
        patient.setNumberOfDosesTaken(Phase.CP, 35);

        //(24:IP + 9:EIP) / (24:IP + 12:EIP)
        assertEquals("35/54 (64.81%)", patient.getCPProgress());
    }

    private LocalDate date(int year, int monthOfYear, int dayOfMonth) {
        return new LocalDate(year, monthOfYear, dayOfMonth);
    }

    private DateTime dateTime(int year, int monthOfYear, int dayOfMonth) {
        return new LocalDate(year, monthOfYear, dayOfMonth).toDateTimeAtCurrentTime();
    }

}
