package org.motechproject.whp.registration.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.security.exceptions.WebSecurityException;
import org.motechproject.security.service.MotechAuthenticationService;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.contract.ProviderRequest;
import org.motechproject.whp.patient.exception.WHPRuntimeException;
import org.motechproject.whp.patient.service.PatientService;
import org.motechproject.whp.patient.service.ProviderService;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationServiceTest {

    @Mock
    private ProviderService providerService;
    @Mock
    private PatientService patientService;
    @Mock
    private MotechAuthenticationService motechAuthenticationService;

    RegistrationService registrationService;

    @Before
    public void setUp() {
        initMocks(this);
        registrationService = new RegistrationService(providerService, patientService, motechAuthenticationService);
    }

    @Test
    public void shouldRegisterUserAsInactiveByDefault() throws WebSecurityException {
        ProviderRequest providerRequest = new ProviderRequest("providerId", "district", "1111111111", DateUtil.now());
        String externalId = "externalId";
        when(providerService.createProvider(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<DateTime>any())).thenReturn(externalId);
        registrationService.registerProvider(providerRequest);

        verify(motechAuthenticationService).register(providerRequest.getProviderId(), "password", externalId, Arrays.asList("PROVIDER"), false);
    }

    @Test(expected = WHPRuntimeException.class)
    public void shouldThrowWhpRunTimeExceptionIfRegisterThrowsException() throws WebSecurityException {
        ProviderRequest providerRequest = new ProviderRequest("providerId", "district", "1111111111", DateUtil.now());
        String externalId = "externalId";
        when(providerService.createProvider(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<DateTime>any())).thenReturn(externalId);
        doThrow(new RuntimeException("Exception to be thrown for test")).when(motechAuthenticationService).register(providerRequest.getProviderId(),"password",externalId,Arrays.asList("PROVIDER"), false);
        registrationService.registerProvider(providerRequest);
    }
}
