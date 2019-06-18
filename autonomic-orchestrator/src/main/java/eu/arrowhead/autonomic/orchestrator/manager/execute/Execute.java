package eu.arrowhead.autonomic.orchestrator.manager.execute;

import java.util.List;
import java.util.TreeMap;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sys.JenaSystem;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;
import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.SubstitutionWorker;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;

public class Execute {

	private ExecuteWorker executeWorker;
	private TreeMap<String, String> consumerOrchestrationPushEndpointTreeMap;
	
	private Plan plan;
	
	public Execute(Plan plan) {
		executeWorker = new ExecuteWorker(this, Constants.ExecuteWorkerInterval);
		consumerOrchestrationPushEndpointTreeMap = new TreeMap<String, String>();
		this.plan = plan;
	}
	
	public static void main(String[] args) {
		
		JenaSystem.init();
		// TODO Auto-generated method stub
		Plan plan = new Plan();
		Execute execute = new Execute(plan);
		
		
		plan.start();
		execute.start();

	}

	public void WorkerProcess() {
		
		getOrchestrationPushEndpoints();
		checkAndSendAdaptation();
		
	}
	
	private void checkAndSendAdaptation()
	{
		for(String consumerName : consumerOrchestrationPushEndpointTreeMap.keySet())
		{
			AdaptationPlan adaptPlan = plan.GetAdaptationPlan(consumerName);
			if(adaptPlan != null)
				if(adaptPlan.getStatus() == PlanStatus.NEW)
				{
					String orchPushEp = consumerOrchestrationPushEndpointTreeMap.get(consumerName);
					OrchestrationPushWorker orchestrationPushWorker = new OrchestrationPushWorker(this, plan, consumerName, adaptPlan, orchPushEp);
					orchestrationPushWorker.start();
				}
		}
	}
	
	private void getOrchestrationPushEndpoints()
	{
		String queryString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
				 "prefix rdfs: <"+RDFS.getURI()+">\n" +
				 "prefix rdf: <"+RDF.getURI()+">\n" +
				 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
				 "select ?c ?s ?a \n" +
				 "where { ?c rdf:type :Consumer . \n" +
				 "?c :producesService ?s . \n" +
				 "?s :hasServiceDefinition \"" +  Constants.OrchestrationPushServiceDefinition + "\" . \n" +
				 "?s :hasOperation ?o . \n" +
				 "?o :hasAddress ?a . \n" +
				 "}";
		
		List<QuerySolution> results = KnowledgeBase.getInstance().ExecuteSelectQuery(queryString);
	    for (QuerySolution soln : results )
	    {
	      Resource consumer =  soln.getResource("c");
	      String consumerName = consumer.getLocalName();
	      
	      Literal address =  soln.getLiteral("a");
	      String addressString = address.getString();
	      
	      System.out.println("Execute found orchestration push endpoint for " + consumerName);
	      //System.out.println(addressString);
	      consumerOrchestrationPushEndpointTreeMap.put(consumerName, addressString);
	     
	    }
	}
	
	public void start()
	{
		executeWorker.start();
	}
	
	public void stop()
	{
		executeWorker.stop();
	}

}
