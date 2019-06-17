package eu.arrowhead.autonomic.orchestrator.manager.plan;

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
		System.out.println("Substitution");
		System.out.println("Rule: " + context.getRule().getName());
		for(Node n : args)
			System.out.println(n.toString());
		plan.ExecuteSustitionPlan();
	}

}
