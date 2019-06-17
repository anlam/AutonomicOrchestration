package eu.arrowhead.autonomic.orchestrator.manager.execute;

import java.util.List;
import java.util.TreeMap;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;

public class Execute {

	private ExecuteWorker executeWorker;
	private TreeMap<String, String> orchestrationPushEndpoint;
	
	public Execute() {
		executeWorker = new ExecuteWorker(this, Constants.ExecuteWorkerInterval);
		orchestrationPushEndpoint = new TreeMap<String, String>();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Execute execute = new Execute();
		execute.start();

	}

	public void WorkerProcess() {
		
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
	      orchestrationPushEndpoint.put(consumerName, addressString);
	     
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
