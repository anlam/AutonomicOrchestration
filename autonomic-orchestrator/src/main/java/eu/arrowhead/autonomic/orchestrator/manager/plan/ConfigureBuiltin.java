package eu.arrowhead.autonomic.orchestrator.manager.plan;

import java.util.ArrayList;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;

public class ConfigureBuiltin extends BaseBuiltin {

    private Plan plan;

    public ConfigureBuiltin(Plan plan) {
        this.plan = plan;
    }

    @Override
    public String getName() {
        return "configure";
    }

    @Override
    public String getURI() {
        return OntologyNames.BASE_URL + getName();
    }

    @Override
    public void headAction(Node[] args, int length, RuleContext context) {

        // System.out.println("ConfigureBuiltin Rule: " + context.getRule().getName());
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(context.getRule().getName());
        for (Node n : args) {
            if (n.isLiteral()) {
                parameters.add(n.getLiteral().getValue().toString());
            } else {
                parameters.add(n.getLocalName());
            }
        }

        // System.out.println(n.toString());
        ConfigureWorker worker = new ConfigureWorker(plan, parameters);
        worker.start();

    }

}
