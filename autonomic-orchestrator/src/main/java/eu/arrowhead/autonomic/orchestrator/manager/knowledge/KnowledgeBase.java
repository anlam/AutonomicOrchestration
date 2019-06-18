package eu.arrowhead.autonomic.orchestrator.manager.knowledge;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeBase {

	private static KnowledgeBase instance;
	
	private ReentrantLock lock;
	private static final Logger log = LoggerFactory.getLogger(KnowledgeBase.class );
	private KnowledgeBase()
	{
		lock =  new ReentrantLock();
	}
	
	public static void main(String[] args) {
		/*
		 * String updateCurentTimeQuery = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
		 * "prefix rdfs: <"+RDFS.getURI()+">\n" + "prefix rdf: <"+RDF.getURI()+">\n" +
		 * "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
		 * "prefix xsd: <"+XSD.getURI()+">\n" +
		 * "delete { :DateTimeNow :hasValue ?Value} \n" +
		 * "insert { :DateTimeNow :hasValue \"" + new Date().getTime() +
		 * "\"^^xsd:long } \n" + "where {  :DateTimeNow :hasValue ?Value . \n" + "}";
		 */
		
		String updateCurentTimeQuery = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
				 "prefix rdfs: <"+RDFS.getURI()+">\n" +
				 "prefix rdf: <"+RDF.getURI()+">\n" +
				 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
				 "prefix xsd: <"+XSD.getURI()+">\n" 
				
				 
				 + "insert data { "
				
				 + ":PrediktorAutoOrchPush :hasServiceDefinition \"AutonomicOrchestrationPush\" . \n"
				
				 + "}";
		
		System.out.println(updateCurentTimeQuery);
		
		List<String> queries = new ArrayList<String>();
		queries.add(updateCurentTimeQuery);
		//queries.add(offlineString);
		//queries.add(onlineString);
		KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
		KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");
		//KnowledgeBase.getInstance().ExecuteQuery(queries);

	}
	
	public static KnowledgeBase getInstance()
	{
		if(instance == null)
			instance = new KnowledgeBase();
		return instance;
	}
	
	public void AddStateMent(Statement s)
	{
		lock.lock();
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		
		try {
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			model.add(s);
			
			dataset.commit();
			
		}
		catch(Exception e)
		{
			log.debug("Fail to add statement: " + e.getMessage());
			System.out.println("Fail to add statement: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}
	}
	
	
	public void WriteModelToFile(String filename) {
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		try {

			Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

			model.setNsPrefix("sosa", OntologyNames.SOSA_URL);
			model.setNsPrefix(":", OntologyNames.BASE_URL);
			model.setNsPrefix("rdfs", RDFS.uri);
			model.setNsPrefix("xsd", XSD.NS);
			model.write(new FileOutputStream(new File(filename)), "TTL");

			dataset.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataset.end();

		}
	}
	
	public List<QuerySolution> ExecuteSelectQuery(String queries) {
		
		lock.lock();
		List<QuerySolution> ret = new ArrayList<QuerySolution>();
		log.debug("Knowledgebase Executing Select query");
		System.out.println("Knowledgebase Executing Select query");

		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.READ);

		try {
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

			Query query = QueryFactory.create(queries);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);

			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				ret.add(soln);

			}

		} catch (Exception e) {
			log.debug("Fail to execute query: " + e.getMessage());
			System.err.println("Fail to execute query: " + e.getMessage());
			// e.printStackTrace();
		} finally {
			dataset.end();
			lock.unlock();
		}

		return ret;
	}
	
	public void ExecuteUpdateQueries(List<String> queries)
	{
		lock.lock();
		//AddService(serviceName);
		
		log.debug("Knowledgebase Executing Update queries" );
		System.out.println("Knowledgebase Executing Update queries");
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		
		try {
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			for(String query : queries)
			{
				
				
				UpdateAction.parseExecute(query, model);
			}
			
			
			
			  model.setNsPrefix("sosa", OntologyNames.SOSA_URL); model.setNsPrefix(":",
			  OntologyNames.BASE_URL); model.setNsPrefix("rdfs", RDFS.uri);
			  model.setNsPrefix("xsd", XSD.NS); model.write(new FileOutputStream(new
			  File("./dataset.ttl")), "TTL" );
			 
			
			
			
			dataset.commit();
			
		}
		catch(Exception e)
		{
			log.debug("Fail to execute query: " + e.getMessage());
			System.err.println("Fail to execute query: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}
	}
	
	public void Reasoning(List<Rule> rules)
	{
		lock.lock();
		log.debug("Knowledgebase Reasoning Rules");
		System.out.println("Knowledgebase Reasoning Rules");
		
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.READ);
		
		try {
			
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			
			Reasoner reasoner = new GenericRuleReasoner( rules);
			
			InfModel infModel = ModelFactory.createInfModel( reasoner, model );

			
			//System.out.println("Knowledgebase Reasoning Rules prepare");
			
			
			infModel.prepare();
			
		}
		catch(Exception e)
		{
			log.debug("Fail to reason rules: " + e.getMessage());
			System.err.println("Fail to reason rules:  " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}
	}
	
	public void AddObservation(String observationId, String sensorId, long timestamp, String value) 
	{
		lock.lock();
		
		log.debug("Knowledgebase Adding observation" );
		System.out.println("Knowledgebase Adding observation");
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		boolean isObservationExisted = false;
		
		try {
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);
			
			String queryString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
								 "prefix rdfs: <"+RDFS.getURI()+">\n" +
								 "prefix rdf: <"+RDF.getURI()+">\n" +
								 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
								 "select ?obs \n" +
								 "where { ?obs rdf:type sosa:Observation . \n" +
								 "?obs sosa:madeBySensor :"  + sensorId + " . \n" +
								 "}";
			
			String updateString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
								 "prefix rdfs: <"+RDFS.getURI()+">\n" +
								 "prefix rdf: <"+RDF.getURI()+">\n" +
								 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
								 "prefix xsd: <"+XSD.getURI()+">\n" +
								 "delete { :" + observationId + " sosa:hasSimpleResult ?value. \n" + 
								 ":" + observationId + " sosa:resultTime ?time. \n" + 
								 "} \n" +
								 "insert { :" + observationId + " sosa:hasSimpleResult \"" + value + "\"^^xsd:double . \n" + 
								 ":" + observationId + " sosa:resultTime  \"" + timestamp + "\"^^xsd:long . \n" + 
								 "} \n" +
								 "where { :" + observationId + " sosa:hasSimpleResult ?value. \n" + 
								 ":" + observationId + " sosa:resultTime ?time. \n" + 
								 "}";
			
			String addString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
					 "prefix rdfs: <"+RDFS.getURI()+">\n" +
					 "prefix rdf: <"+RDF.getURI()+">\n" +
					 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
					 "prefix xsd: <"+XSD.getURI()+">\n" +
					 "insert data { "   +
					 ":" + observationId + " rdf:type sosa:Observation . \n" + 
					 ":" + observationId + " sosa:madeBySensor :" + sensorId + " . \n" + 
					 ":" + observationId + " sosa:hasSimpleResult \"" + value + "\"^^xsd:double . \n" + 
					 ":" + observationId + " sosa:resultTime  \"" + timestamp + "\"^^xsd:long . \n" + 
					 "}";
			
			//System.out.println(addString);
			
			Query query = QueryFactory.create(queryString);
		  try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;
			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;
			      
			     // System.out.println(soln);
			      if(soln.toString().contains(observationId))
			      {
			    	  isObservationExisted = true;
			    	  break;
			      }
			    }
			  }
		  
		  if(isObservationExisted)
			  UpdateAction.parseExecute(updateString, model);
		  else
		  {
			  UpdateAction.parseExecute(addString, model);
		  }
		  
			 
		  
		  dataset.commit();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}

	}
	
	public void AddSensor(String sensorName, String serviceName)
	{
		lock.lock();
		AddService(serviceName);
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		
		try {
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			String addString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
					 "prefix rdfs: <"+RDFS.getURI()+">\n" +
					 "prefix rdf: <"+RDF.getURI()+">\n" +
					 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
					 "prefix xsd: <"+XSD.getURI()+">\n" +
					 "insert data{ "   +
					 ":" + sensorName + " rdf:type :SensorUnit . \n" + 
					 //":" + sensorName + " :hasID :" + sensorId + " . \n" + 
					 ":" + sensorName + " :hasService :" + serviceName + " . \n" + 
					 "}";
			
			UpdateAction.parseExecute(addString, model);
			
			dataset.commit();
			
		} 
		catch(Exception e)
		{
			log.debug("Fail to add sensor: " + e.getMessage());
			System.err.println("Fail to add sensor: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}

	}
	
	
	public void AddService(String serviceName)
	{
		lock.lock();
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		
		try {
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			String addString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
					 "prefix rdfs: <"+RDFS.getURI()+">\n" +
					 "prefix rdf: <"+RDF.getURI()+">\n" +
					 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
					 "prefix xsd: <"+XSD.getURI()+">\n" +
					 "insert data{ "   +
					 ":" + serviceName + " rdf:type :Service . \n" + 
					 //":" + serviceName + " :hasID :" + serviceId + " . \n" + 
					 "}";
			
			UpdateAction.parseExecute(addString, model);
			
			dataset.commit();
			
		}
		catch(Exception e)
		{
			log.debug("Fail to add service: " + e.getMessage());
			System.err.println("Fail to add service: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}

	}

}
