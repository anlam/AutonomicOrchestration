package eu.arrowhead.autonomic.orchestrator.push;

import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;

public interface AutonomicOrchestrationPushService {

	public AdaptationPlan sendApdationPlan(AdaptationPlan plan); 
}
