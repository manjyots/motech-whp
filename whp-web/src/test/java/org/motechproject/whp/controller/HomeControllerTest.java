package org.motechproject.whp.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.security.LoginSuccessHandler;
import org.motechproject.security.domain.AuthenticatedUser;
import org.motechproject.whp.patient.builder.ProviderBuilder;
import org.motechproject.whp.patient.domain.Provider;
import org.motechproject.whp.patient.repository.AllProviders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HomeControllerTest {

    HomeController homeController;

    @Mock
    AllProviders allProviders;
    @Mock
    HttpServletRequest request;

    @Before
    public void setup() {
        initMocks(this);
        homeController = new HomeController(allProviders);
    }

    @Test
    public void shouldRedirectToTheListPageForProviderUponLogin() {
        Provider provider = ProviderBuilder.startRecording().withDefaults().withId(UUID.randomUUID().toString()).build();
        login(provider);
        setupProvider(provider);
        assertEquals("redirect:/providers/" + provider.getProviderId(), homeController.homePage(request));
    }

    private void login(Provider provider) {
        HttpSession session = mock(HttpSession.class);
        AuthenticatedUser user = authenticatedUserFor(provider);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    private AuthenticatedUser authenticatedUserFor(Provider provider) {
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        when(authenticatedUser.getExternalId()).thenReturn(provider.getId());
        return authenticatedUser;
    }

    private void setupProvider(Provider provider) {
        when(allProviders.get(provider.getId())).thenReturn(provider);
    }
}
