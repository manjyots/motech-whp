package org.motechproject.whp.patient.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.*;
import static org.motechproject.util.DateUtil.today;

public class PhasesTest {

    private Phases phases;

    @Before
    public void setUp() {
        phases = new Phases(Arrays.asList(new Phase(PhaseName.IP), new Phase(PhaseName.CP), new Phase(PhaseName.EIP)));
    }

    @Test
    public void shouldReturnPhaseByName() {
        assertEquals(PhaseName.IP, phases.getByPhaseName(PhaseName.IP).getName());
    }

    @Test
    public void settingEIPStartDateShouldSetEndDateOnIP() {
        LocalDate today = today();
        phases.setEIPStartDate(today);

        assertEquals(today.minusDays(1), phases.getByPhaseName(PhaseName.IP).getEndDate());
    }

    @Test
    public void settingCPStartDateShouldSetEndDateOnEIPOnlyIfEIPWasStarted() {
        LocalDate today = today();
        phases.setEIPStartDate(today.minusDays(2));
        phases.setCPStartDate(today);

        assertEquals(today.minusDays(1), phases.getByPhaseName(PhaseName.EIP).getEndDate());
        assertNotSame(today.minusDays(1), phases.getByPhaseName(PhaseName.IP).getEndDate());
    }

    @Test
    public void settingCPStartDateShouldSetEndDateOnIPIfEIPWasNeverStarted() {
        LocalDate today = today();
        phases.setCPStartDate(today);

        assertEquals(today.minusDays(1), phases.getByPhaseName(PhaseName.IP).getEndDate());
    }

    @Test
    public void shouldSetPreviousPhaseEndDateToNullIfCurrentPhaseStartDateIsBeingSetToNull() {
        LocalDate today = today();
        phases.setEIPStartDate(today.minusDays(2));
        phases.setEIPEndDate(today.minusDays(1));
        phases.setCPStartDate(null);

        assertNull(phases.getByPhaseName(PhaseName.EIP).getEndDate());
    }

    @Test
    public void shouldGetCurrentPhase() {
        LocalDate today = today();
        phases.setIPStartDate(today);
        phases.setEIPStartDate(today.plusDays(10));

        assertEquals(phases.getByPhaseName(PhaseName.EIP), phases.getCurrentPhase());
    }

    @Test
    public void shouldGetLastCompletedPhase() {
        LocalDate today = today();
        phases.setIPStartDate(today);
        phases.setIPEndDate(today.plusDays(2));
        phases.setEIPStartDate(today.plusDays(3));
        phases.setEIPEndDate(today.plusDays(5));
        phases.setCPStartDate(today.plusDays(6));

        assertEquals(phases.getByPhaseName(PhaseName.EIP), phases.getLastCompletedPhase());
    }

    @Test
    public void shouldReturnNullIfNoPhaseHasStarted() {
        assertNull(phases.getCurrentPhase());
    }

}
