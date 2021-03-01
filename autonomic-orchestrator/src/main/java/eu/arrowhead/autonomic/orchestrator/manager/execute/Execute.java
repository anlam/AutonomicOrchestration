package eu.arrowhead.autonomic.orchestrator.manager.execute;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sys.JenaSystem;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;
import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.SubstitutionWorker;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.Adaptation;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.SubstitutionAdaptation;

public class Execute {

	private ExecuteWorker executeWorker;
	private TreeMap<String, String> consumerOrchestrationPushEndpointTreeMap;
	
	
	private Plan plan;
	
	
	private static final Logger log = LoggerFactory.getLogger(Execute.class );
	
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
			{
				
				if(adaptPlan.getStatus() == PlanStatus.NEW) //|| adaptPlan.getStatus() == PlanStatus.SENT)
				{
					log.debug("Execute found adapation plan for: " + consumerName);
					log.debug(adaptPlan.toString());
					
					System.out.println("Execute found adapation plan for: " + consumerName);
					System.out.println(adaptPlan);
					
					String orchPushEp = consumerOrchestrationPushEndpointTreeMap.get(consumerName);
					OrchestrationPushWorker orchestrationPushWorker = new OrchestrationPushWorker(this, plan, consumerName, adaptPlan, orchPushEp, 0, null);
					orchestrationPushWorker.start();
				}
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
				 "where { " +
				 // "?c rdf:type :Consumer . \n" +
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
	      
	      log.debug("Execute found orchestration push endpoint for " + consumerName + "at: " + addressString);
	     // System.out.println("Execute found orchestration push endpoint for " + consumerName + "at: " + addressString);
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

	public void ProcessExecutedAdaptationPlan(String consumerName, AdaptationPlan adaptationPlan) {
		
		
		List<Adaptation> executedAdapts = new ArrayList<Adaptation>();

		List<Adaptation> currentAdapts = plan.GetAdaptationPlan(consumerName).getAdaptations();
		for (Adaptation adapt : adaptationPlan.getAdaptations()) {
			if (adapt.getStatus() == PlanStatus.EXECUTED) {
				executedAdapts.add(adapt);
			}
			
			for(Adaptation cAdapt : currentAdapts)
				if(cAdapt.equals(adapt))
					cAdapt.setStatus(adapt.getStatus());
			
		}

		/*
		 * for (Adaptation adapt :
		 * plan.GetAdaptationPlan(consumerName).getAdaptations()) {
		 * adapt.setStatus(PlanStatus.SENT); if (executedAdapts.contains(adapt))
		 * adapt.setStatus(PlanStatus.EXECUTED); }
		 */

		if(!executedAdapts.isEmpty())
		{
			for (Adaptation adapt : executedAdapts) {
				if (adapt.getStatus() == PlanStatus.EXECUTED) {
					if (adapt instanceof SubstitutionAdaptation) {
						SubstitutionAdaptation substitutionAdaptation = (SubstitutionAdaptation) adapt;
						updateKnowledgeBaseWithExecutedSubstitution(consumerName, substitutionAdaptation);
						
					}
				}
				
				plan.RemoveAllAdaptations(consumerName);
				
			}
			

			//plan.RemoveAdaptationPlans(consumerName, executedAdapts);
			plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.EXECUTED);
			
		}
		
		log.debug("Execute: ProcessExecutedAdaptationPlan for " + consumerName);
		System.out.println("Execute: ProcessExecutedAdaptationPlan for " + consumerName);
		System.out.println(plan.GetAdaptationPlan(consumerName));


		

	}
	
	private void updateKnowledgeBaseWithExecutedSubstitution(String consumerName, SubstitutionAdaptation substitutionAdaptation)
	{
		String deleteString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
				 "prefix rdfs: <"+RDFS.getURI()+">\n" +
				 "prefix rdf: <"+RDF.getURI()+">\n" +
				 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
				 "delete data { :" + consumerName + " :consumesService :"  + substitutionAdaptation.getFromService() + "}";
	
		
		
		String insertString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
				 "prefix rdfs: <"+RDFS.getURI()+">\n" +
				 "prefix rdf: <"+RDF.getURI()+">\n" +
				 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
				 "insert data { :" + consumerName + " :consumesService :" + substitutionAdaptation.getToService() + "}";
		
		
		List<String> queries = new ArrayList<String>() ;
		queries.add(deleteString);
		queries.add(insertString);
		
		
		KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
	}
	

}
