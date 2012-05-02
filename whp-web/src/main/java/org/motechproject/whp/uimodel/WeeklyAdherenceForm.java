package org.motechproject.whp.uimodel;

import lombok.Data;
import org.motechproject.model.DayOfWeek;

import java.util.ArrayList;
import java.util.List;

@Data
public class WeeklyAdherenceForm {

    private List<DailyAdherenceForm> allDailyAdherenceForms;

    public WeeklyAdherenceForm() {
        allDailyAdherenceForms = new ArrayList<DailyAdherenceForm>();
    }

    public WeeklyAdherenceForm(List<DayOfWeek> pillDays) {
        this();
        for (DayOfWeek pillDay : pillDays) {
            allDailyAdherenceForms.add(new DailyAdherenceForm(pillDay));
        }
    }

}