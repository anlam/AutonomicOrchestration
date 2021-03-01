package eu.arrowhead.autonomic.orchestrator.manager.execute;

import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.Adaptation;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationType;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.SubstitutionAdaptation;
import eu.arrowhead.autonomic.orchestrator.push.AutonomicOrchestrationPushConsumerREST_WS;
import eu.arrowhead.autonomic.orchestrator.push.AutonomicOrchestrationPushService;

public class OrchestrationPushWorker implements Runnable {

	private Thread t;
	protected Execute execute;
	private String name = "OrchestrationPushWorker";
	private String consumerName;
	private String consumerURL;
	private int consumerPort;
	private String consumerPath;
	private AdaptationPlan adaptationPlan;
	private Plan plan;
	
	public OrchestrationPushWorker(Execute ex, Plan plan, String name, AdaptationPlan adaptPlan, String url, int port, String path) 
	{
		this.execute = ex;
		this.name = this.name + "_" + name;
		this.consumerName = name;
		this.consumerURL = url;
		this.consumerPort = port;
		this.consumerPath = path;
		this.adaptationPlan = adaptPlan;
		this.plan = plan;
	}
	
	public void start() {
		if (t == null) {
			t = new Thread(this, name);
			t.start();
		}
	}

	public void run() {

		System.out.println("OrchestrationPushWorker Sending orchestratin to: " + consumerName);
		AutonomicOrchestrationPushService pushService = new AutonomicOrchestrationPushConsumerREST_WS(consumerName,
				consumerURL, consumerPort, consumerPath);
		plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.SENDING);

		AdaptationPlan response = pushService.sendApdationPlan(adaptationPlan);
		if (response != null) 
		{
			plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.SENT);
			System.out.println("OrchestrationPushWorker get response: " + response);
			/*
			 * List<Adaptation> executedAdapts = new ArrayList<Adaptation>(); for
			 * (Adaptation adapt : response.getAdaptations()) {
			 * 
			 * if (adapt.getStatus() == PlanStatus.EXECUTED) {
			 * 
			 * executedAdapts.add(adapt); }
			 * 
			 * }
			 * 
			 * for (Adaptation adapt :
			 * plan.GetAdaptationPlan(consumerName).getAdaptations()) {
			 * adapt.setStatus(PlanStatus.SENT); if (executedAdapts.contains(adapt))
			 * adapt.setStatus(PlanStatus.EXECUTED); }
			 */

			execute.ProcessExecutedAdaptationPlan(consumerName, response);
		} else {
			plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.FAILED);
			System.err.println(String.format("Fail to send to %s, %s", consumerName,  adaptationPlan));
		}
	}
		
}
