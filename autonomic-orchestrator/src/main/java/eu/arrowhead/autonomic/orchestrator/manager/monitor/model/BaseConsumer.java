package eu.arrowhead.autonomic.orchestrator.manager.monitor.model;

import java.io.Serializable;
import java.util.List;

public class BaseConsumer implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7908120278158043285L;
    private List<String> services;
    private String systemName;

    public BaseConsumer() {
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public void addToServices(String service) {
        this.services.add(service);
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
