package eu.arrowhead.autonomic.orchestrator.manager.monitor.model;

import java.io.Serializable;

public class BaseConsumer implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7908120278158043285L;
    private String serviceEndpoint;
    private String serviceName;
    private String systemName;
    public String getServiceEndpoint() {
        return serviceEndpoint;
    }
    public void setServiceEndpoint(String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getSystemName() {
        return systemName;
    }
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
