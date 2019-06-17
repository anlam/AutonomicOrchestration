package eu.arrowhead.autonomic.orchestrator.manager.plan;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;

public class Plan {

	private PlanWorker planWorker;
	private TreeMap<String, List<Rule>> rules;
	private TreeMap<String, Long> ruleLastUpdated;
	
	
	private TreeMap<String, AdaptationPlan> AdaptationPlans;
	private ReentrantLock updatePlanLock;
	
	private static final Logger log = LoggerFactory.getLogger( Plan.class );
	

	
	
	
	public Plan()
	{
		rules = new TreeMap<String, List<Rule>>();
		ruleLastUpdated = new TreeMap<String, Long>();
		planWorker = new PlanWorker(this, Constants.PlanWorkerInterval);
		
		BuiltinRegistry.theRegistry.register(new SubstitutionServiceBuiltin(this));
		
		AdaptationPlans = new TreeMap<String, AdaptationPlan>();
		updatePlanLock =  new ReentrantLock();
	}
	
	
	
	public void UpdateAdaptationPlan(String name, AdaptationPlan plan)
	{
		updatePlanLock.lock();
		try
		{
			AdaptationPlans.put(name, plan);
		}
		finally {
			updatePlanLock.unlock();
		}
	}
	
	public AdaptationPlan GetAdaptationPlan(String name)
	{
		updatePlanLock.lock();
		
		AdaptationPlan ret = null;
		
		try
		{
			if(AdaptationPlans.containsKey(name))
				ret = AdaptationPlans.get(name);
		}
		finally 
		{
			updatePlanLock.unlock();
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		
		Plan plan = new Plan();
		plan.start();
		
		//BuiltinRegistry.theRegistry.register(new SubstitutionServiceBuiltin());
		//test();
	}
	
	public void start()
	{
		planWorker.start();
	}
	
	public void stop()
	{
		planWorker.stop();
	}
	
	public void ExecuteSustitionPlan()
	{
		
	}
	
	public void WorkerProcess() {
		UpdateRules();
		StartReasoning();
	}
	
	private void StartReasoning()
	{
		log.debug("Planning Reasoning Rules");
		System.out.println("Planning Reasoning Rules");
		
		
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.READ);
		
		try {
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			
			Reasoner reasoner = new GenericRuleReasoner( Rule.rulesFromURL(Constants.planQueriesDir + "sub.rule") );
			
			InfModel infModel = ModelFactory.createInfModel( reasoner, model );

			infModel.prepare();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			dataset.end();
			
		}
	}
	
	
	private void UpdateRules()
	{
		try {
			File dir = new File(Constants.planQueriesDir);
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					//System.out.println(filename);
					return filename.endsWith(".rule");
				}
			});
			
			for(File f : files)
			{
				
				
				String name = f.getName();
				
				//Check if file recently updated
				if(ruleLastUpdated.containsKey(name))
				{
					long lastmodified = f.lastModified();
					long lastupdated = ruleLastUpdated.get(name);
					
					if(lastupdated >= lastmodified)
						continue;
				}
				
				List<Rule> query =  Rule.rulesFromURL(f.getAbsolutePath());
				rules.put(name, query);
				ruleLastUpdated.put(name, f.lastModified());
				
				log.debug("New rule registered: " + name);
				System.out.println("New query registered: " + name);
				
				
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	
	public static void test()
	{
		Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		dataset.begin(ReadWrite.READ);
		
		try {
			
			Model model = dataset.getNamedModel(OntologyNames.BASE_URL +  Constants.ModelName);
			
			
			/*
			 * model.setNsPrefix("sosa", OntologyNames.SOSA_URL); model.setNsPrefix(":",
			 * OntologyNames.BASE_URL); model.setNsPrefix("rdfs", RDFS.uri);
			 * model.setNsPrefix("rdf", RDF.uri); model.setNsPrefix("xsd", XSD.NS);
			 */
			
			
			  String rule = "@prefix : <"+ OntologyNames.BASE_URL+"> .\n" +
			  "@prefix rdfs: <"+RDFS.getURI()+"> .\n" +
			  "@prefix rdf: <"+RDF.getURI()+"> . \n" +
			  "@prefix sosa: <"+OntologyNames.SOSA_URL+"> . \n" +
			  "@prefix xsd: <"+XSD.getURI()+"> . \n" +
			  
			  "[rule1: (?c rdf:type :Consumer) (?c :consumesService ?s1) (?s1  :hasState  :OfflineState) (?d1 :hasService ?s1) "
			  +
			  " (?d1 sosa:hasLocation ?l) (?d2 :hasService ?s2) (?d2  sosa:hasLocation ?l) (?s2  :hasState  :OnlineState)"
			  + " -> (?c :substitudeFrom ?s1) (?c :substitudeTo ?s2) ]"
			  
			  ;
			 
			
			System.out.println(rule);
			
			Reasoner reasoner = new GenericRuleReasoner( Rule.rulesFromURL(Constants.planQueriesDir + "sub.rule") );
			
			InfModel infModel = ModelFactory.createInfModel( reasoner, model );
			
			//infModel.prepare();
			
			//infModel.createRe
			
			infModel.listStatements();

			//StmtIterator it = infModel.listStatements();
			
			/*
			 * while ( it.hasNext() ) { Statement stmt = it.nextStatement();
			 * 
			 * Resource subject = stmt.getSubject(); Property predicate =
			 * stmt.getPredicate(); RDFNode object = stmt.getObject();
			 * 
			 * if(predicate.getLocalName().endsWith("substitudeFrom") ||
			 * predicate.getLocalName().endsWith("substitudeTo")) System.out.println(
			 * subject.getLocalName() + " " + predicate.getLocalName() + " " +
			 * object.toString() ); }
			 */
			
			//dataset.commit();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally {
			dataset.end();
			
		}
	}


}
