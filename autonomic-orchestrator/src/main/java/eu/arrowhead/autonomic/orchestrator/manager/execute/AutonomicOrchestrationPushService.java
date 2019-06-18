package eu.arrowhead.autonomic.orchestrator.manager.execute;

import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;

public interface AutonomicOrchestrationPushService {

	public AdaptationPlan sendApdationPlan(AdaptationPlan plan); 
}
