package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

import java.util.List;

public class OrchestrationServiceRegister {

    private String systemName;
    private String endPoint;
    private List<String> rules;

    public OrchestrationServiceRegister() {

    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
}
