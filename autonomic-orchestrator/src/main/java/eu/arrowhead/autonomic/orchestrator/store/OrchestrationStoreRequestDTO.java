package eu.arrowhead.autonomic.orchestrator.store;

import java.io.Serializable;

import eu.arrowhead.common.dto.shared.CloudRequestDTO;
import eu.arrowhead.common.dto.shared.SystemResponseDTO;

public class OrchestrationStoreRequestDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2557895397416837464L;

    private String serviceDefinitionName;
    private int consumerSystemId;
    private AttributeDTO attribute;
    private SystemResponseDTO providerSystem;
    private CloudRequestDTO cloud;
    private String serviceInterfaceName;
    private String priority;

    public OrchestrationStoreRequestDTO() {
    }

    public String getServiceDefinitionName() {
        return serviceDefinitionName;
    }

    public void setServiceDefinitionName(String serviceDefinitionName) {
        this.serviceDefinitionName = serviceDefinitionName;
    }

    public int getConsumerSystemId() {
        return consumerSystemId;
    }

    public void setConsumerSystemId(int consumerSystemId) {
        this.consumerSystemId = consumerSystemId;
    }

    public AttributeDTO getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeDTO attribute) {
        this.attribute = attribute;
    }

    public SystemResponseDTO getProviderSystem() {
        return providerSystem;
    }

    public void setProviderSystem(SystemResponseDTO providerSystem) {
        this.providerSystem = providerSystem;
    }

    public CloudRequestDTO getCloud() {
        return cloud;
    }

    public void setCloud(CloudRequestDTO cloud) {
        this.cloud = cloud;
    }

    public String getServiceInterfaceName() {
        return serviceInterfaceName;
    }

    public void setServiceInterfaceName(String serviceInterfaceName) {
        this.serviceInterfaceName = serviceInterfaceName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

}
