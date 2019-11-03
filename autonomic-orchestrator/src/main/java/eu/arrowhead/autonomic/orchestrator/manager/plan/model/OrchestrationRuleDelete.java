package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

import java.util.List;

public class OrchestrationRuleDelete {

	private String systemName;
	private List<String> rules;
	
	public OrchestrationRuleDelete()
	{
		
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
}
