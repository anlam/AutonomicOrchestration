package eu.arrowhead.autonomic.orchestrator.manager.plan;

import java.util.List;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.ConfigureAdaptation;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.SubstitutionAdaptation;

public class ConfigureWorker implements Runnable {

	private Thread t;
	protected Plan plan;
	private String name = "ConfigureWorker";
	private List<String> parameters;
	
	public ConfigureWorker(Plan plan, List<String> pars ) 
	{
		this.plan = plan;
		this.parameters = pars;
	}
	
	public void start() {
		if (t == null) {
			t = new Thread(this, name);
			t.start();
		}
	}

	public void run() {

		//System.out.println("Thread " + name + " running.");
		
		if(parameters.size() >= Constants.ConfigureParameterSize)
		{
			String rulename = parameters.get(0);
			String consumerName = parameters.get(1);
			
			if(!rulename.startsWith(consumerName))
				return;
			
			String attString = parameters.get(2);
			String valString = parameters.get(3);
			
			
			ConfigureAdaptation adaptation = new ConfigureAdaptation();
			adaptation.setAttribute(attString);
			adaptation.setValue(valString);
			plan.UpdateAdaptationPlan(consumerName, adaptation);
			
		}
			
		
		//System.out.println("Thread " + name + " exiting.");
	}

}
