package eu.arrowhead.autonomic.orchestrator.manager.plan;

import java.util.List;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.SubstitutionAdaptation;

public class SubstitutionWorker implements Runnable {

    private Thread t;
    protected Plan plan;
    private String name = "SubstitutionWorker";
    private List<String> parameters;

    public SubstitutionWorker(Plan plan, List<String> pars) {
        this.plan = plan;
        this.parameters = pars;
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    @Override
    public void run() {

        // System.out.println("Thread " + name + " running.");

        if (parameters.size() >= Constants.SubstitutionParameterSize) {
            String consumerName = parameters.get(1);

            String fromService = parameters.get(2);
            String toService = parameters.get(3);

            SubstitutionAdaptation adaptation = new SubstitutionAdaptation();
            adaptation.setFromService(fromService);
            adaptation.setToService(toService);
            plan.UpdateAdaptationPlan(consumerName, adaptation);

        }

        // System.out.println("Thread " + name + " exiting.");
    }

}
