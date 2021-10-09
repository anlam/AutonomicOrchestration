package eu.arrowhead.autonomic.orchestrator.store;

import java.io.Serializable;

import eu.arrowhead.common.dto.shared.ServiceDefinitionResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.SystemResponseDTO;

public class OrchestrationStoreEntryDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -429399345151206733L;

    private int id;
    private ServiceDefinitionResponseDTO serviceDefinition;
    private SystemResponseDTO consumerSystem;
    private Boolean foreign;
    private CloudResponseDTO providerCloud;
    private SystemResponseDTO providerSystem;
    private ServiceInterfaceResponseDTO serviceInterface;
    private int priority;
    private String attribute;
    private String createdAt;
    private String updatedAt;

    public OrchestrationStoreEntryDTO() {
    }

    public ServiceDefinitionResponseDTO getServiceDefinition() {
        return serviceDefinition;
    }

    public void setServiceDefinition(ServiceDefinitionResponseDTO serviceDefinition) {
        this.serviceDefinition = serviceDefinition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SystemResponseDTO getConsumerSystem() {
        return consumerSystem;
    }

    public void setConsumerSystem(SystemResponseDTO consumerSystem) {
        this.consumerSystem = consumerSystem;
    }

    public Boolean getForeign() {
        return foreign;
    }

    public void setForeign(Boolean foreign) {
        this.foreign = foreign;
    }

    public CloudResponseDTO getProviderCloud() {
        return providerCloud;
    }

    public void setProviderCloud(CloudResponseDTO providerCloud) {
        this.providerCloud = providerCloud;
    }

    public SystemResponseDTO getProviderSystem() {
        return providerSystem;
    }

    public void setProviderSystem(SystemResponseDTO providerSystem) {
        this.providerSystem = providerSystem;
    }

    public ServiceInterfaceResponseDTO getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(ServiceInterfaceResponseDTO serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
