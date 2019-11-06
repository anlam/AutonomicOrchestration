package eu.arrowhead.autonomic.orchestrator.manager.plan;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.Adaptation;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.OrchestrationRuleDelete;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.OrchestrationRuleRegister;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;

public class Plan {

	private PlanWorker planWorker;
	private TreeMap<String, List<Rule>> ConsumerRulesTreeMap;
	private TreeMap<String, Long> ruleLastUpdated;
	
	
	private TreeMap<String, AdaptationPlan> ConsumerAdaptationPlansTreeMap;
	private ReentrantLock updatePlanLock;
	
	private static final Logger log = LoggerFactory.getLogger( Plan.class );
	
	public Plan()
	{
		ConsumerRulesTreeMap = new TreeMap<String, List<Rule>>();
		ruleLastUpdated = new TreeMap<String, Long>();
		planWorker = new PlanWorker(this, Constants.PlanWorkerInterval);
		
		BuiltinRegistry.theRegistry.register(new SubstitutionServiceBuiltin(this));
		BuiltinRegistry.theRegistry.register(new ConfigureBuiltin(this));
		
		ConsumerAdaptationPlansTreeMap = new TreeMap<String, AdaptationPlan>();
		updatePlanLock =  new ReentrantLock();
	}
	
	
	
	
	
	public void UpdateAdaptationPlan(String name, Adaptation adapt)
	{
		updatePlanLock.lock();
		
		
		try
		{
			AdaptationPlan adaptationPlan = ConsumerAdaptationPlansTreeMap.get(name);
			if(adaptationPlan == null)
				adaptationPlan = new AdaptationPlan(name);
			if(!adaptationPlan.getAdaptations().contains(adapt))
			{
				adaptationPlan.getAdaptations().add(adapt);
				
				if(adapt.getStatus() ==  PlanStatus.NEW)
					adaptationPlan.setStatus(PlanStatus.NEW);
				
				log.debug("Plan Updated Adaptation: " + adaptationPlan);
				System.out.println("Plan Updated Adaptation: " + adaptationPlan);
			}
			ConsumerAdaptationPlansTreeMap.put(name, adaptationPlan);
			
			
		}
		finally {
			updatePlanLock.unlock();
		}
		
		
	}
	
	public void UpdateAdaptationPlanStatus(String name, PlanStatus status)
	{
		updatePlanLock.lock();
		try
		{
			AdaptationPlan adaptationPlan = ConsumerAdaptationPlansTreeMap.get(name);
			if(adaptationPlan != null)
				adaptationPlan.setStatus(status);
			
			//ConsumerAdaptationPlansTreeMap.put(name, adaptationPlan);
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
			if(ConsumerAdaptationPlansTreeMap.containsKey(name))
			{
				AdaptationPlan plan = ConsumerAdaptationPlansTreeMap.get(name);
				ret = (AdaptationPlan) plan.clone();
			}
		}
		finally 
		{
			updatePlanLock.unlock();
		}
		
		return ret;
	}
	
	public void RemoveAllAdaptations(String name)
	{
		updatePlanLock.lock();
		
		try
		{
			if(ConsumerAdaptationPlansTreeMap.containsKey(name))
			{
				AdaptationPlan plan = ConsumerAdaptationPlansTreeMap.get(name);
				plan.getAdaptations().clear();
				
				log.debug("Plan RemoveAllAdaptations: " + plan);
				System.out.println("Plan RemoveAllAdaptations: " + plan);
			}
		}
		finally 
		{
			updatePlanLock.unlock();
		}
	}
	
	public void RemoveAdaptationPlans(String name, List<Adaptation> adapts)
	{
		updatePlanLock.lock();
		
		
		try
		{
			if(ConsumerAdaptationPlansTreeMap.containsKey(name))
			{
				AdaptationPlan plan = ConsumerAdaptationPlansTreeMap.get(name);
				plan.getAdaptations().removeAll(adapts);
				
				log.debug("Plan RemoveAdaptationPlans: " + plan);
				System.out.println("Plan RemoveAdaptationPlans: " + plan);
			}
		}
		finally 
		{
			updatePlanLock.unlock();
		}
	}
	
	public static void main(String[] args) {
		
		Plan plan = new Plan();
		plan.start();
		
		//BuiltinRegistry.theRegistry.register(new SubstitutionServiceBuiltin());
		//test();
	}
	
	public void start()
	{
		UpdateRuleFromKnowledgeBase();
		
		planWorker.start();
	}
	
	public void stop()
	{
		planWorker.stop();
	}
	
	public void WorkerProcess() {
		//UpdateRules();
		StartReasoning();
	}
	
	private void StartReasoning()
	{
		log.debug("Planning Reasoning Rules");
		//System.out.println("Planning Reasoning Rules");
		
		List<Rule> rules = new ArrayList<Rule>();
		for(List<Rule> rls : ConsumerRulesTreeMap.values())
			rules.addAll(rls);
		KnowledgeBase.getInstance().Reasoning(rules);
		/*
		 * Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
		 * dataset.begin(ReadWrite.READ);
		 * 
		 * try {
		 * 
		 * Model model = dataset.getNamedModel(OntologyNames.BASE_URL +
		 * Constants.ModelName);
		 * 
		 * 
		 * Reasoner reasoner = new GenericRuleReasoner(
		 * Rule.rulesFromURL(Constants.planQueriesDir + "sub.rule") );
		 * 
		 * InfModel infModel = ModelFactory.createInfModel( reasoner, model );
		 * 
		 * infModel.prepare();
		 * 
		 * } catch(Exception e) { e.printStackTrace(); } finally { dataset.end();
		 * 
		 * }
		 */
	}
	
	private void UpdateRuleFromKnowledgeBase()
	{
		String queryString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
				 "prefix rdfs: <"+RDFS.getURI()+">\n" +
				 "prefix rdf: <"+RDF.getURI()+">\n" +
				 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
				 "select ?s ?r ?b \n" +
				 "where { ?s :hasJenaRule ?r . \n" +
				 "?r :hasBody ?b . \n" +
				 "}";
		
		
		List<QuerySolution> results = KnowledgeBase.getInstance().ExecuteSelectQuery(queryString);
		
	    for (QuerySolution soln : results )
	    {
	      Resource system =  soln.getResource("s");
	      String systemName = system.getLocalName();
	      
	      Literal body =  soln.getLiteral("b");
	      String bodyString = body.getString();
	      
	      List<Rule> rules = new ArrayList<Rule>();
	      Rule r = Rule.parseRule(bodyString);
	      
	      log.debug("Plan: New rule registered for consumer: " + systemName);
		  System.out.println("Plan: New rule registered for consumer: " + systemName);
		  System.out.println(r);
		  
			
	      if(ConsumerRulesTreeMap.containsKey(systemName))
	    	  rules.addAll(ConsumerRulesTreeMap.get(systemName));
	      rules.add(r);
	      ConsumerRulesTreeMap.put(systemName, rules);
	      
	     
	    }
	}
	
	public List<Rule> RegisterRules(OrchestrationRuleRegister requestRules)
	{
		
		List<Rule> rules = new ArrayList<Rule>();
		List<Rule> updatedRules = new ArrayList<Rule>();
		
		for(String rule : requestRules.getRules())
		{
			try {
				rules.add(Rule.parseRule(rule));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		List<Rule> currentRules = new ArrayList<Rule>();
		if(ConsumerRulesTreeMap.containsKey(requestRules.getSystemName()))
		{
			currentRules.addAll(ConsumerRulesTreeMap.get(requestRules.getSystemName()));
		}
		
		System.out.println("Plan: New rule registered for consumer: " + requestRules.getSystemName());
		
		int i = currentRules.size() + 1;
		for(Rule r : rules)
		{
			Rule newRule = new Rule(requestRules.getSystemName() + "_rule" + i , r.getHead(), r.getBody());
			updatedRules.add(newRule);
			currentRules.add(newRule);
			i++;
			System.out.println(newRule);
		}
		
		ConsumerRulesTreeMap.put(requestRules.getSystemName(), currentRules);
		
		AddRuleToKnowledgeBase(requestRules.getSystemName(), updatedRules);
		
		
		return updatedRules;
	}
	
	
	private void AddRuleToKnowledgeBase(String name, List<Rule> rules)
	{
		String updateCurentTimeQuery = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
				 "prefix rdfs: <"+RDFS.getURI()+">\n" +
				 "prefix rdf: <"+RDF.getURI()+">\n" +
				 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
				 "prefix xsd: <"+XSD.getURI()+">\n" 
				
				 
				 + "insert data { \n ";
		
		
		PrintUtil.removePrefix("sosa");
		PrintUtil.removePrefix("auto");
		PrintUtil.removePrefix("rdfs");
		PrintUtil.removePrefix("xsd");
		PrintUtil.removePrefix("rdf");

		for(Rule r : rules)
		{
			updateCurentTimeQuery = updateCurentTimeQuery + ":" + name + " :hasJenaRule :"  + r.getName()  + " . \n" 
									+ ":"  + r.getName() + " :hasBody \"" + r.toString() + "\" . \n" ;
		}
				
				
				
				
				
		updateCurentTimeQuery +=  " }";
		
		System.out.println(updateCurentTimeQuery);
		
		List<String> queries = new ArrayList<String>();
		queries.add(updateCurentTimeQuery);
		
		
		KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
	}
	
	private void DeleteRuleFromKnowledgeBase(String name, List<String> rules)
	{
		List<String> queries = new ArrayList<String>();
		
		for(String r : rules)
		{
			String updateString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
					 "prefix rdfs: <"+RDFS.getURI()+">\n" +
					 "prefix rdf: <"+RDF.getURI()+">\n" +
					 "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
					 "prefix xsd: <"+XSD.getURI()+">\n" +
					 
					 "delete { :" + name + " :hasJenaRule :"  + r  + " . \n" 
					 + ":"  + r + " :hasBody ?body . \n" 
					 + "} \n" 
					 + "where { :" + name + " :hasJenaRule :"  + r  + " . \n" 
					 + ":"  + r + " :hasBody ?body . \n" 
					 + "}";
			
			queries.add(updateString);
		}
		
		KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
		
	}
	
	public List<Rule> getRules(String systemName)
	{
		List<Rule> ret = new ArrayList<Rule>();
		if(ConsumerRulesTreeMap.containsKey(systemName))
			ret.addAll(ConsumerRulesTreeMap.get(systemName));
		return ret;
	}
	
	public List<String> getRegisterSystems()
	{
		List<String> ret = new ArrayList<String>();
		ret.addAll(ConsumerRulesTreeMap.keySet());
		
		return ret;
	}
	
	public boolean deleteRules(OrchestrationRuleDelete rulesName)
	{
		if(ConsumerRulesTreeMap.containsKey(rulesName.getSystemName()))
		{
			List<Rule> currentRulesList =  new ArrayList<Rule>();
			List<Rule> updatedRulesList =  new ArrayList<Rule>();
			currentRulesList.addAll(ConsumerRulesTreeMap.get(rulesName.getSystemName()));
			updatedRulesList.addAll(ConsumerRulesTreeMap.get(rulesName.getSystemName()));
			for(Rule r : currentRulesList)
				if(rulesName.getRules().contains(r.getName()))
					updatedRulesList.remove(r);
			ConsumerRulesTreeMap.put(rulesName.getSystemName(), updatedRulesList);
			
			DeleteRuleFromKnowledgeBase(rulesName.getSystemName(), rulesName.getRules());
			return true;
		}
		
		return false;
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
				name = name.substring(0, name.length() - 5);
				
				//Check if file recently updated
				if(ruleLastUpdated.containsKey(name))
				{
					long lastmodified = f.lastModified();
					long lastupdated = ruleLastUpdated.get(name);
					
					if(lastupdated >= lastmodified)
						continue;
				}
				
				List<Rule> rules =  Rule.rulesFromURL(f.getAbsolutePath());
				
				
				
				
				
				log.debug("Plan: New rule registered for consumer: " + name);
				System.out.println("Plan: New rule registered for consumer: " + name);
				
				List<Rule> modifiedRules = new ArrayList<Rule>();
				
				int i = 1;
				for(Rule r : rules)
				{
					Rule newRule = new Rule(name + "_rule" + i , r.getHead(), r.getBody());
					modifiedRules.add(newRule);
					i++;
					System.out.println(newRule);
					//System.out.println(newRule.);
					
					//PrintUtil.registerPrefix("sosa", OntologyNames.SOSA_URL);
					//PrintUtil.registerPrefix("auto", OntologyNames.BASE_URL);
					//PrintUtil.registerPrefix("rdfs", RDFS.uri);
					//PrintUtil.registerPrefix("xsd", XSD.NS);
					//PrintUtil.registerPrefix("rdf", RDF.getURI());
					
					//System.out.println(PrintUtil.print(newRule));
				
				}
					
				ConsumerRulesTreeMap.put(name, modifiedRules);
				ruleLastUpdated.put(name, f.lastModified());
				
				
				//for(Rule r : rules)
				
				
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
