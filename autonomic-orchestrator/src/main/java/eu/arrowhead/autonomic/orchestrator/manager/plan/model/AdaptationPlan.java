package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

import java.util.ArrayList;
import java.util.List;

public class AdaptationPlan {
	
	
	private String systemName;
	private PlanStatus status;
	private List<Adaptation> adaptations;
	
	public AdaptationPlan(String systemName)
	{
		this.systemName = systemName;
		status = PlanStatus.NEW;
		adaptations = new ArrayList<Adaptation>();
	}
	
	
	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public PlanStatus getStatus() {
		return status;
	}

	public void setStatus(PlanStatus status) {
		this.status = status;
	}

	public List<Adaptation> getAdaptations() {
		return adaptations;
	}

	public void setAdaptations(List<Adaptation> adaptations) {
		this.adaptations = adaptations;
	}


	
	
	
}
