package eu.arrowhead.autonomic.orchestrator.manager.plan;

import java.util.List;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.SubstitutionAdaptation;

public class SubstitutionWorker implements Runnable {

	private Thread t;
	protected Plan plan;
	private String name = "SubstitutionWorker";
	private List<String> parameters;
	
	public SubstitutionWorker(Plan plan, List<String> pars ) 
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
		
		if(parameters.size() >= Constants.SubstitutionParameterSize)
		{
			String rulename = parameters.get(0);
			String consumerName = parameters.get(1);
			
			if(!rulename.startsWith(consumerName))
				return;
			
			String fromService = parameters.get(2);
			String fromProducer = parameters.get(3);
			String toService = parameters.get(4);
			String toProducer = parameters.get(5);
			
			SubstitutionAdaptation adaptation = new SubstitutionAdaptation();
			adaptation.setFromService(fromService);
			adaptation.setFromProducer(fromProducer);
			adaptation.setToService(toService);
			adaptation.setToProducer(toProducer);
			plan.UpdateAdaptationPlan(consumerName, adaptation);
			
		}
			
		
		//System.out.println("Thread " + name + " exiting.");
	}

}
