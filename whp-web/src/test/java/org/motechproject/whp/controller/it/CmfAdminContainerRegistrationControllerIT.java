package org.motechproject.whp.controller.it;

import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.security.authentication.LoginSuccessHandler;
import org.motechproject.security.domain.MotechWebUser;
import org.motechproject.security.exceptions.WebSecurityException;
import org.motechproject.security.service.MotechUser;
import org.motechproject.whp.common.service.RemediProperties;
import org.motechproject.whp.common.util.SpringIntegrationTest;
import org.motechproject.whp.container.contract.ContainerRegistrationMode;
import org.motechproject.whp.container.domain.Container;
import org.motechproject.whp.container.mapping.domain.AdminContainerMapping;
import org.motechproject.whp.container.mapping.domain.ContainerRange;
import org.motechproject.whp.container.mapping.domain.ProviderContainerMapping;
import org.motechproject.whp.container.mapping.repository.AllAdminContainerMappings;
import org.motechproject.whp.container.mapping.repository.AllProviderContainerMappings;
import org.motechproject.whp.container.service.ContainerService;
import org.motechproject.whp.controller.CmfAdminContainerRegistrationController;
import org.motechproject.whp.refdata.domain.SputumTrackingInstance;
import org.motechproject.whp.user.domain.CmfAdmin;
import org.motechproject.whp.user.domain.WHPRole;
import org.motechproject.whp.user.service.CmfAdminService;
import org.motechproject.whp.user.service.ProviderService;
import org.motechproject.whp.webservice.builder.ProviderRequestBuilder;
import org.motechproject.whp.webservice.request.ProviderWebRequest;
import org.motechproject.whp.webservice.service.ProviderWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.motechproject.whp.common.util.WHPDate.DATE_TIME_FORMAT;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath*:META-INF/spring/applicationContext.xml")
public class CmfAdminContainerRegistrationControllerIT  extends SpringIntegrationTest {

    @Autowired
    private CmfAdminContainerRegistrationController containerRegistrationController;
    @Autowired
    AllProviderContainerMappings allProviderContainerMappings;
    @Autowired
    AllAdminContainerMappings allAdminContainerMappings;
    @Autowired
    private ProviderWebService providerWebService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private CmfAdminService cmfAdminService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private RemediProperties remediProperties;

    @ReplaceWithMock
    @Autowired
    private HttpClientService httpClientService;
    private final String providerId = "provider";
    private String remediUrl;
    private String apiKey;

    @Before
    public void setUp() throws WebSecurityException {
        remediUrl = remediProperties.getUrl();
        apiKey = remediProperties.getApiKey();
        ProviderContainerMapping providerContainerMapping = new ProviderContainerMapping();
        providerContainerMapping.add(new ContainerRange(10000000000L, 20000000000L));
        providerContainerMapping.setProviderId(providerId);
        allProviderContainerMappings.add(providerContainerMapping);

        AdminContainerMapping adminContainerMapping = new AdminContainerMapping();
        adminContainerMapping.add(new ContainerRange(30000000000L, 40000000000L));
        allAdminContainerMappings.removeAll();
        allAdminContainerMappings.add(adminContainerMapping);

        ProviderWebRequest whpProviderWeb = new ProviderRequestBuilder().withDefaults().withProviderId(providerId).build();
        providerWebService.createOrUpdate(whpProviderWeb);

        CmfAdmin admin = new CmfAdmin("admin", "password", "test", "Delhi", "Cmf Admin1");
        cmfAdminService.add(admin, "password");

        markForDeletion(providerContainerMapping);
        markForDeletion(adminContainerMapping);
        markForDeletion(providerService.findByProviderId(providerId));
        markForDeletion(admin);
    }

    @Test
    public void shouldRegisterTheContainerAndInvokeRemediWithAppropriateXml_OnBehalfOfProvider() throws Exception {
        String containerId = "10000000000";
        String instance = SputumTrackingInstance.InTreatment.getDisplayText();

        ArrayList<String> roles = new ArrayList<>();
        roles.add(WHPRole.CMF_ADMIN.name());

        standaloneSetup(containerRegistrationController).build()
                .perform(post("/containerRegistration/by_cmfAdmin/register")
                        .param("containerId", containerId)
                        .param("instance", instance).param("providerId", providerId)
                        .param("containerRegistrationMode", ContainerRegistrationMode.ON_BEHALF_OF_PROVIDER.name())
                        .sessionAttr(LoginSuccessHandler.LOGGED_IN_USER, new MotechUser(new MotechWebUser(null, null, null, roles))))
                .andExpect(status().isOk());

        Container container = containerService.getContainer(containerId);

        assertNotNull(container);
        assertThat(container.getProviderId(), is(providerId));
        assertThat(container.getContainerId(), is(containerId));

        String expectedContainerRegistrationXML = String.format("<?xml version=\"1.0\"?>\n" +
                "<case xmlns=\"http://openrosa.org/javarosa\" case_id=\"%s\" date_modified=\"%s\" user_id=\"system\"\n" +
                "      api_key=\"%s\">\n" +
                "    <create>\n" +
                "        <case_type>%s</case_type>\n" +
                "    </create>\n" +
                "    <update>\n" +
                "        <provider_id>%s</provider_id>\n" +
                "    </update>\n" +
                "</case>\n", containerId, container.getCreationTime().toString(DATE_TIME_FORMAT), apiKey, instance, providerId);

        verify(httpClientService).post(remediUrl, expectedContainerRegistrationXML);
        markForDeletion(container);
    }

    @Test
    public void shouldRegisterTheContainerAndInvokeRemediWithAppropriateXml_ForNewContainer() throws Exception {
        String containerId = "30000000000";
        String instance = SputumTrackingInstance.InTreatment.getDisplayText();

        ArrayList<String> roles = new ArrayList<>();
        roles.add(WHPRole.CMF_ADMIN.name());

        standaloneSetup(containerRegistrationController).build()
                .perform(post("/containerRegistration/by_cmfAdmin/register")
                        .param("containerId", containerId)
                        .param("instance", instance).param("providerId", providerId)
                        .param("containerRegistrationMode", ContainerRegistrationMode.NEW_CONTAINER.name())
                        .sessionAttr(LoginSuccessHandler.LOGGED_IN_USER, new MotechUser(new MotechWebUser(null, null, null, roles))))
                .andExpect(status().isOk());

        Container container = containerService.getContainer(containerId);

        assertNotNull(container);
        assertThat(container.getProviderId(), is(providerId));
        assertThat(container.getContainerId(), is(containerId));

        String expectedContainerRegistrationXML = String.format("<?xml version=\"1.0\"?>\n" +
                "<case xmlns=\"http://openrosa.org/javarosa\" case_id=\"%s\" date_modified=\"%s\" user_id=\"system\"\n" +
                "      api_key=\"%s\">\n" +
                "    <create>\n" +
                "        <case_type>%s</case_type>\n" +
                "    </create>\n" +
                "    <update>\n" +
                "        <provider_id>%s</provider_id>\n" +
                "    </update>\n" +
                "</case>\n", containerId, container.getCreationTime().toString(DATE_TIME_FORMAT), apiKey, instance, providerId);

        verify(httpClientService).post(remediUrl, expectedContainerRegistrationXML);
        markForDeletion(container);
    }
}