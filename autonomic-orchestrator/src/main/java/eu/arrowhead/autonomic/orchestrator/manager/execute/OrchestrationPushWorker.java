package eu.arrowhead.autonomic.orchestrator.manager.execute;

import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.Adaptation;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;

public class OrchestrationPushWorker implements Runnable {

	private Thread t;
	protected Execute execute;
	private String name = "OrchestrationPushWorker";
	private String consumerName;
	private String consumerURL;
	private AdaptationPlan adaptationPlan;
	private Plan plan;
	
	public OrchestrationPushWorker(Execute ex, Plan plan, String name, AdaptationPlan adaptPlan, String url ) 
	{
		this.execute = ex;
		this.name = this.name + "_" + name;
		this.consumerName = name;
		this.consumerURL = url;
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
		AutonomicOrchestrationPushService pushService = new AutonomicOrchestrationPushConsumerREST_WS(consumerName, consumerURL);
		AdaptationPlan response = pushService.sendApdationPlan(adaptationPlan);
		if(response != null)
		{
			List<Adaptation> executedAdapts = new ArrayList<Adaptation>();
			for(Adaptation adapt : response.getAdaptations())
			{
				if(adapt.getStatus() == PlanStatus.EXECUTED)
					executedAdapts.add(adapt);
			}
			
			for(Adaptation adapt : plan.GetAdaptationPlan(consumerName).getAdaptations())
			{
				adapt.setStatus(PlanStatus.SENT);
				if(executedAdapts.contains(adapt))
					adapt.setStatus(PlanStatus.EXECUTED);			
			}
			
			plan.ProcessExecutedAdaptationPlan(consumerName, executedAdapts);
			
		}
	}
		
}
