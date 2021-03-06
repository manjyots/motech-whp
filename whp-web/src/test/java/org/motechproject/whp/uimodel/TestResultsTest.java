package org.motechproject.whp.uimodel;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.whp.patient.domain.SmearTestRecord;
import org.motechproject.whp.patient.domain.SmearTestResults;
import org.motechproject.whp.patient.domain.WeightStatistics;
import org.motechproject.whp.patient.domain.WeightStatisticsRecord;
import org.motechproject.whp.refdata.domain.SampleInstance;
import org.motechproject.whp.refdata.domain.SmearTestResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.motechproject.whp.refdata.domain.SampleInstance.EndIP;
import static org.motechproject.whp.refdata.domain.SampleInstance.EndTreatment;
import static org.motechproject.whp.refdata.domain.SampleInstance.PreTreatment;

public class TestResultsTest {

    String weight;
    String result1;
    String result2;
    String date1;
    String date2;
    WeightStatistics weightStatistics;
    SampleInstance sampleInstance;
    SmearTestResults smearTestResults;
    String labName = "labName";
    String labNumber = "labNumber";

    @Before
    public void setup() {
        this.weight = "20.0";
        SmearTestResult result1 = SmearTestResult.Negative;
        SmearTestResult result2 = SmearTestResult.Positive;
        this.result1 = result1.value();
        this.result2 = result2.value();
        LocalDate date1 = new LocalDate(2012, 1, 2);
        LocalDate date2 = new LocalDate(2012, 1, 3);
        this.date1 = "02/01/2012";
        this.date2 = "03/01/2012";
        weightStatistics = new WeightStatistics();
        sampleInstance = PreTreatment;
        String labName = "labName";
        String labNumber = "labNumber";

        weightStatistics.add(new WeightStatisticsRecord(sampleInstance, new Double(weight), new LocalDate(2012, 1, 1)));
        smearTestResults = new SmearTestResults();
        smearTestResults.add(new SmearTestRecord(sampleInstance, date1, result1, date2, result2, labName, labNumber));
    }

    @Test
    public void shouldMapSampleInstanceTestResultsFromPatient() {

        TestResults testResults = new TestResults(smearTestResults, weightStatistics);
        assertThat(1, is(testResults.size()));

        TestResult testResult = testResults.get(0);
        assertTestResult(testResult, sampleInstance, date1, result1, date2, result2, labName, labNumber, String.valueOf(weight));
    }


    @Test
    public void shouldAddEmptyStringForWeightIfNotExists() {
        TestResults testResults = new TestResults(smearTestResults, new WeightStatistics());

        assertThat(1, is(testResults.size()));

        TestResult testResult = testResults.get(0);

        assertTestResult(testResult, sampleInstance, date1, result1, date2, result2, labName, labNumber, "");

    }

    @Test
    public void shouldAddEmptyStringForSmearTestResultsIfNotExists() {
        TestResults testResults = new TestResults(new SmearTestResults(), weightStatistics);

        assertThat(1, is(testResults.size()));

        TestResult testResult = testResults.get(0);

        assertTestResult(testResult, sampleInstance, "", "", "", "", "", "", weight);
    }

    @Test
    public void shouldNotAddTestResultForSampleInstanceIfSmearAndWeightTestDoesNotExist() {
        SmearTestResults smearTestResultsWithPreTreatmentResultOnly = smearTestResults;
        WeightStatistics weightStatisticsWithPreTreatmentResultOnly = weightStatistics;

        TestResults testResults = new TestResults(smearTestResultsWithPreTreatmentResultOnly, weightStatisticsWithPreTreatmentResultOnly);

        assertThat(testResults.size(), is(1));
        assertThat(testResults.get(0).getSampleInstance(), is(PreTreatment.value()));
    }

    @Test
    public void shouldReturnTestResultsInTheOrderOfSampleInstances() {
        SmearTestResults smearTestResults = new SmearTestResults();
        SmearTestRecord preTreatmentSmearTestRecord = new SmearTestRecord();
        preTreatmentSmearTestRecord.setSmear_sample_instance(PreTreatment);

        SmearTestRecord endIPSmearTestRecord = new SmearTestRecord();
        endIPSmearTestRecord.setSmear_sample_instance(EndIP);

        SmearTestRecord endTreatmentSmearTestRecord = new SmearTestRecord();
        endTreatmentSmearTestRecord.setSmear_sample_instance(EndTreatment);

        smearTestResults.add(endIPSmearTestRecord);
        smearTestResults.add(endTreatmentSmearTestRecord);
        smearTestResults.add(preTreatmentSmearTestRecord);

        TestResults testResults = new TestResults(smearTestResults, new WeightStatistics());

        assertThat(testResults.get(0).getSampleInstance(), is(PreTreatment.value()));
        assertThat(testResults.get(1).getSampleInstance(), is(EndIP.value()));
        assertThat(testResults.get(2).getSampleInstance(), is(EndTreatment.value()));
    }

    private void assertTestResult(TestResult testResult, SampleInstance expectedSampleInstance, String expectedDate1, String expectedResult1, String expectedDate2, String expectedResult2, String expectedLabName, String expectedLabNumber, String expectedWeight) {
        assertThat(testResult.getSampleInstance(), is(expectedSampleInstance.value()));
        assertThat(testResult.getSmearTestDate1(), is(expectedDate1));
        assertThat(testResult.getSmearTestResult1(), is(expectedResult1));
        assertThat(testResult.getSmearTestDate2(), is(expectedDate2));
        assertThat(testResult.getSmearTestResult2(), is(expectedResult2));
        assertThat(testResult.getWeight(), is(expectedWeight));
        assertThat(testResult.getLabName(), is(expectedLabName));
        assertThat(testResult.getLabNumber(), is(expectedLabNumber));
    }
}