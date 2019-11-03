package eu.arrowhead.autonomic.orchestrator.manager.plan;

import java.util.ArrayList;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;

public class SubstitutionServiceBuiltin extends BaseBuiltin {

	private Plan plan;
	
    public SubstitutionServiceBuiltin(Plan plan) {
		this.plan = plan;
	}
	
	@Override
	public String getName() {
		return "substituteService";
	}

	@Override
	public String getURI() {
		return OntologyNames.BASE_URL + getName();
	}

	@Override
	public void headAction(Node[] args, int length, RuleContext context) {
		
		//System.out.println("SubstitutionServiceBuiltin Rule: " + context.getRule().getName());
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(context.getRule().getName());
		for(Node n : args)
			parameters.add(n.getLocalName());
			//System.out.println(n.toString());
		SubstitutionWorker worker = new SubstitutionWorker(plan, parameters);
		worker.start();
		
	}

}
