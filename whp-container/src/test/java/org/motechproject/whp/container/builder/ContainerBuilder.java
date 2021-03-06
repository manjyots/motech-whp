package org.motechproject.whp.container.builder;

import org.joda.time.DateTime;
import org.motechproject.whp.container.domain.Container;
import org.motechproject.whp.container.domain.LabResults;
import org.motechproject.whp.refdata.domain.ContainerStatus;
import org.motechproject.whp.refdata.domain.SmearTestResult;
import org.motechproject.whp.refdata.domain.SputumTrackingInstance;

public class ContainerBuilder {

    private Container container = new Container();

    public static ContainerBuilder newContainer() {
        return new ContainerBuilder();
    }

    public ContainerBuilder withDefaults() {
        withProviderId("providerId").withContainerId("containerId");
        return this;
    }

    public ContainerBuilder withProviderId(String providerId) {
        container.setProviderId(providerId);
        return this;
    }

    public ContainerBuilder withContainerId(String containerId) {
        container.setContainerId(containerId);
        return this;
    }

    public ContainerBuilder withPatientId(String patientId) {
        container.setPatientId(patientId);
        return this;
    }

    public ContainerBuilder withInstance(SputumTrackingInstance instance) {
        container.setInstance(instance);
        return this;
    }

    public Container build() {
        return container;
    }

    public ContainerBuilder withSmearTestResult1(SmearTestResult smearTestResult) {
        if(container.getLabResults() == null){
            container.setLabResults(new LabResults());
        }
        container.getLabResults().setSmearTestResult1(smearTestResult);
        return this;
    }

    public ContainerBuilder withSmearTestResult2(SmearTestResult smearTestResult) {
        if(container.getLabResults() == null){
            container.setLabResults(new LabResults());
        }
        container.getLabResults().setSmearTestResult2(smearTestResult);
        return this;
    }

    public ContainerBuilder withStatus(ContainerStatus status) {
        container.setStatus(status);
        return this;
    }

    public ContainerBuilder withCreatedDateTime(DateTime dateTime) {
        container.setCreationTime(dateTime);
        return this;
    }
}
