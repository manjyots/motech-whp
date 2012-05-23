package org.motechproject.whp.integration.validation.provider;

import org.junit.Test;
import org.motechproject.whp.builder.ProviderRequestBuilder;
import org.motechproject.whp.patient.command.AllCommands;
import org.motechproject.whp.patient.exception.WHPRuntimeException;
import org.motechproject.whp.request.ProviderWebRequest;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class DateTest extends BaseProviderTest {

    @Test
    public void shouldThrowAnExceptionIfDateIsNull() {
        expectFieldValidationRuntimeException("field:date:may not be null");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("P00001").withDate(null).withDistrict("Chambal").withPrimaryMobile("9880000000").build();
        validator.validate(providerWebRequest, AllCommands.create); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldThrowSingleExceptionIfDateIsEmpty() {
        String errorMessage = "";
        try {
            ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("P00001").withDate("").withDistrict("Chambal").withPrimaryMobile("9880000000").build();
            validator.validate(providerWebRequest, AllCommands.create);
        } catch (WHPRuntimeException e) {
            if (e.getMessage().contains("field:date: may not be empty")) {
                fail("Use @NotNull instead of @NotEmpty to validate null condition. @DateTimeFormat already validates empty date field.");
            }
            errorMessage = e.getMessage();
        }
        assertTrue(errorMessage.contains("field:date:Invalid format: \"\""));
    }

    @Test
    public void shouldThrowExceptionWhenDateFormatIsNotTheCorrectDateFormat() {
        expectFieldValidationRuntimeException("field:date:Invalid format: \"17-03-1990 17:03:56\" is malformed at \"-03-1990 17:03:56\"");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("P00001").withDate("17-03-1990 17:03:56").withDistrict("Chambal").withPrimaryMobile("9880000000").build();
        validator.validate(providerWebRequest, AllCommands.create); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldThrowExceptionWhenDateFormatDoesNotHaveTimeComponent() {
        expectFieldValidationRuntimeException("field:date:Invalid format: \"17/03/1990\" is too short");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("P00001").withDate("17/03/1990").withDistrict("Chambal").withPrimaryMobile("9880000000").build();
        validator.validate(providerWebRequest, AllCommands.create); //Can be any scope. None of the validation is scope dependent.
    }

}
