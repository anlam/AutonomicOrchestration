package eu.arrowhead.autonomic.orchestrator.manager.knowledge;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.jena.JenaRuntime;
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
import org.apache.jena.sys.JenaSystem;
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
	
	public static void main2(String[] args) {
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
				
				 
				 + "delete data { "
				
				 + ":PrediktorApisServer :consumesService :Service_9575530. \n"
				
				 + "}";
		
		
		String updateCurentTimeQuery2 = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
				 "prefix rdfs: <"+RDFS.getURI()+">\n" +
				 "prefix rdf: <"+RDF.getURI()+">\n" +
				 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
				 "prefix xsd: <"+XSD.getURI()+">\n" 
				
				 
				 + "insert data { "
				
 				+ ":PrediktorApisServer :consumesService :Service_2999285. \n"
				
				 + "}";
		
		System.out.println(updateCurentTimeQuery);
		
		List<String> queries = new ArrayList<String>();
		queries.add(updateCurentTimeQuery);
		queries.add(updateCurentTimeQuery2);
		//queries.add(offlineString);
		//queries.add(onlineString);
		KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
		KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");
		//KnowledgeBase.getInstance().ExecuteQuery(queries);

	}
	
	public static void main1(String[] args) {
		/*
		 * String updateCurentTimeQuery = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
		 * "prefix rdfs: <"+RDFS.getURI()+">\n" + "prefix rdf: <"+RDF.getURI()+">\n" +
		 * "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
		 * "prefix xsd: <"+XSD.getURI()+">\n" +
		 * "delete { :DateTimeNow :hasValue ?Value} \n" +
		 * "insert { :DateTimeNow :hasValue \"" + new Date().getTime() +
		 * "\"^^xsd:long } \n" + "where {  :DateTimeNow :hasValue ?Value . \n" + "}";
		 */
		
		//KnowledgeBase.getInstance().AddSensor("Device_1199791", "Service_1199791", "TopMiddle", "TellUConnector");
		KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");
		//KnowledgeBase.getInstance().ExecuteQuery(queries);

	}
	
	public static void main(String[] args) {
		
		String updateCurentTimeQuery = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
				 "prefix rdfs: <"+RDFS.getURI()+">\n" +
				 "prefix rdf: <"+RDF.getURI()+">\n" +
				 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
				 "prefix xsd: <"+XSD.getURI()+">\n" 
				
				 
				 + "delete data { "
				
				+ 
				":ANewConsumer  :hasJenaRule  :ANewConsumer_rule1 .\r\n" + 
				"\r\n" + 
				":ANewConsumer_rule1  :hasBody  \"[ ANewConsumer_rule1: (?c rdf:type auto:Consumer) (?c auto:consumesService ?s1) (?s1 auto:hasState auto:OfflineState) (?o1 sosa:madeBySensor ?d1) (?o1 auto:hasUnit ?u1) (?p1 auto:producesService ?s1) (?d1 auto:hasService ?s1) (?d1 sosa:hasLocation ?l) (?d2 auto:hasService ?s2) (?o2 sosa:madeBySensor ?d2) (?o2 auto:hasUnit ?u2) notEqual(?u1 ?u2) (?d2 sosa:hasLocation ?l) (?s2 auto:hasState auto:OnlineState) (?p2 auto:producesService ?s2) -> substituteService(?c ?s1 ?p1 ?s2 ?p2) configure(?c 'unit' ?u2) ]\" ." + "}";
		
		System.out.println(updateCurentTimeQuery);
		
		List<String> queries = new ArrayList<String>();
		queries.add(updateCurentTimeQuery);
		//queries.add(updateCurentTimeQuery2);
		 
		
		//KnowledgeBase.getInstance().AddSensor("Device_9575530", "Service_9575530", "TopMiddle");
		
		KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
		KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");

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
			log.error("Fail to add statement: " + e.getMessage());
			System.err.println("Fail to add statement: " + e.getMessage());
			//e.printStackTrace();
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
			log.error("Fail to WriteModelToFile: " + e.getMessage());
			System.err.println("Fail to WriteModelToFile: " + e.getMessage());
			//e.printStackTrace();
		} finally {
			dataset.end();

		}
	}
	
	public List<QuerySolution> ExecuteSelectQuery(String queries) {
		
		lock.lock();
		List<QuerySolution> ret = new ArrayList<QuerySolution>();
		log.debug("Knowledgebase Executing Select query");
		//System.out.println("Knowledgebase Executing Select query");

		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.READ);

		try {
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

			Query query = QueryFactory.create(queries);
			//System.out.println(query);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);

			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				ret.add(soln);

			}

		} catch (Exception e) {
			log.error("Fail to execute query: " + e.getMessage());
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
		//System.out.println("Knowledgebase Executing Update queries");
		
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
			log.error("Fail to execute query: " + e.getMessage());
			System.err.println("Fail to execute query: " + e.getMessage());
			//e.printStackTrace();
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
		//System.out.println("Knowledgebase Reasoning Rules");
		
		
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
			log.error("Fail to reason rules: " + e.getMessage());
			System.err.println("Fail to reason rules:  " + e.getMessage());
			//e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}
	}
	
	public void AddObservation(String observationId, String sensorId, long timestamp, String value, String featureOfInterest, String unit) 
	{
		lock.lock();
		
		log.debug(String.format("Knowledgebase Adding observation %s, %s, %d, %s", observationId, sensorId, timestamp, value));
		//System.out.println(String.format("Knowledgebase Adding observation %s, %s, %d, %s", observationId, sensorId, timestamp, value));
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		boolean isObservationExisted = false;
		
		try {
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);
			
			String queryString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
								 "prefix rdfs: <"+RDFS.getURI()+">\n" +
								 "prefix rdf: <"+RDF.getURI()+">\n" +
								 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
								 //"select ?obs \n" +
								 "ask { ?obs rdf:type sosa:Observation . \n" +
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
					 ":" + observationId + " sosa:hasFeatureOfInterest :" + featureOfInterest + " . \n" + 
					 ":" + observationId + " sosa:hasSimpleResult \"" + value + "\"^^xsd:double . \n" + 
					 ":" + observationId + " sosa:resultTime  \"" + timestamp + "\"^^xsd:long . \n" + 
					 ":" + observationId + " :hasUnit  \"" + unit + "\" . \n" + 
					 "}";
			
			//System.out.println(addString);
			
			Query query = QueryFactory.create(queryString);
		    QueryExecution qexec = QueryExecutionFactory.create(query, model);
			isObservationExisted = qexec.execAsk();
			  
		  
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
			log.error("Fail to AddObservation: " + e.getMessage());
			System.err.println("Fail to AddObservation:  " + e.getMessage());
			//e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}

	}
	
	public void AddSensor(String sensorName, String serviceName, String location, String producer, String serviceDefinition)
	{
		lock.lock();
		AddService(serviceName, producer, serviceDefinition);
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		
		try {
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			String deleteString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
					 "prefix rdfs: <"+RDFS.getURI()+">\n" +
					 "prefix rdf: <"+RDF.getURI()+">\n" +
					 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
					 "prefix xsd: <"+XSD.getURI()+">\n" +
					 "delete data{ "   +
					 ":" + sensorName + " rdf:type :SensorUnit . \n" + 
					 //":" + sensorName + " :hasID \"" + sensorName + "\" . \n" + 
					 ":" + sensorName + " :hasService :" + serviceName + " . \n" + 
					 ":" + sensorName + " sosa:hasLocation :" + location + " . \n" + 
					 "}";
			UpdateAction.parseExecute(deleteString, model);
			
			String addString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
					 "prefix rdfs: <"+RDFS.getURI()+">\n" +
					 "prefix rdf: <"+RDF.getURI()+">\n" +
					 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
					 "prefix xsd: <"+XSD.getURI()+">\n" +
					 "insert data{ "   +
					 ":" + sensorName + " rdf:type :SensorUnit . \n" + 
					 //":" + sensorName + " :hasID \"" + sensorName + "\" . \n" + 
					 ":" + sensorName + " :hasService :" + serviceName + " . \n" + 
					 ":" + sensorName + " sosa:hasLocation :" + location + " . \n" + 
					 "}";
			
			UpdateAction.parseExecute(addString, model);
			
			dataset.commit();
			
		} 
		catch(Exception e)
		{
			log.error("Fail to add sensor: " + e.getMessage());
			System.err.println("Fail to add sensor: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}

	}
	
	
	public void AddService(String serviceName, String producer, String serviceDefinition)
	{
		lock.lock();
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.WRITE);
		
		try {
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			String deleteString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
					 "prefix rdfs: <"+RDFS.getURI()+">\n" +
					 "prefix rdf: <"+RDF.getURI()+">\n" +
					 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
					 "prefix xsd: <"+XSD.getURI()+">\n" +
					 "delete data{ "   +
					 ":" + serviceName + " rdf:type :Service . \n" + 
					 ":" + serviceName + " :hasServiceDefinition " + "\"" + serviceDefinition +  "\"" + " . \n" + 
					 ":" + producer + " rdf:type :Producer . \n" + 
					 ":" + producer + " :producesService :" + serviceName + " . \n" + 
					 //":" + serviceName + " :hasID \"" + serviceName + "\" . \n" + 
					 "}";
			
			UpdateAction.parseExecute(deleteString, model);
			
			String addString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
					 "prefix rdfs: <"+RDFS.getURI()+">\n" +
					 "prefix rdf: <"+RDF.getURI()+">\n" +
					 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
					 "prefix xsd: <"+XSD.getURI()+">\n" +
					 "insert data{ "   +
					 ":" + serviceName + " rdf:type :Service . \n" + 
					 ":" + serviceName + " :hasServiceDefinition " + "\"" + serviceDefinition +  "\"" + " . \n" + 
					 ":" + producer + " rdf:type :Producer . \n" + 
					 ":" + producer + " :producesService :" + serviceName + " . \n" + 
					 //":" + serviceName + " :hasID \"" + serviceName + "\" . \n" + 
					 "}";
			
			UpdateAction.parseExecute(addString, model);
			
			dataset.commit();
			
		}
		catch(Exception e)
		{
			log.error("Fail to add service: " + e.getMessage());
			System.err.println("Fail to add service: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			dataset.end();
			lock.unlock();
		}

	}

}
