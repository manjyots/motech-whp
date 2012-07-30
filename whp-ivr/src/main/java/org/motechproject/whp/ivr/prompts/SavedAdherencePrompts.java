package org.motechproject.whp.ivr.prompts;


import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.whp.ivr.WHPIVRMessage;
import org.motechproject.whp.ivr.builder.PromptBuilder;

import static org.motechproject.whp.ivr.IvrAudioFiles.*;

public class SavedAdherencePrompts {

    public static Prompt[] savedAdherencePrompts(WHPIVRMessage whpivrMessage, String patientId, int adherenceInput, int dosesPerWeek) {
        PromptBuilder promptBuilder = new PromptBuilder(whpivrMessage);
        promptBuilder.wav(CONFIRM_ADHERENCE)
                .id(patientId)
                .wav(HAS_TAKEN)
                .number(adherenceInput)
                .wav(OUT_OF)
                .number(dosesPerWeek)
                .wav(DOSES);
        return promptBuilder.build();
    }

}
