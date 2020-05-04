/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.autonomic.orchestrator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.arrowhead.autonomic.orchestrator.manager.analysis.Analysis;
import eu.arrowhead.autonomic.orchestrator.manager.analysis.AnalysisQueryRequest;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;
import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.Adaptation;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationType;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.OrchestrationRuleDelete;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.OrchestrationRuleRegister;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;


@Path(Constants.OrchestrationRegisterURI)
@Produces(MediaType.APPLICATION_JSON)
//REST service example
public class OrchestrationRegisterResource {

  //static final String SERVICE_URI = Constants.OrchestrationRegisterURI;
  public static Plan plan;
  public static Analysis analysis;

  @GET
  @Path("/")
  public Response getAllRule() {
	  
	  if(plan == null)
	  {
		  return Response.status(Status.BAD_REQUEST).build();
	  }
	  
	  List<OrchestrationRuleRegister> ret = new ArrayList<OrchestrationRuleRegister>();
	  List<String> systems = plan.getRegisterSystems();
	  for(String system : systems)
	  {
		  List<Rule> rules = plan.getRules(system);
		  List<String> rulesStr = new ArrayList<String>();
		  for(Rule r : rules)
			  rulesStr.add(r.toString());
		  OrchestrationRuleRegister orchRe = new OrchestrationRuleRegister();
		  orchRe.setSystemName(system);
		  orchRe.setRules(rulesStr);
		  ret.add(orchRe);
	  }
	   
	  return Response.status(Status.OK).header("Access-Control-Allow-Origin", "*").entity(ret).build();
  }
  
  @GET
  @Path("rules")
  public Response getAllRule2() {
	  
	  if(plan == null)
	  {
		  return Response.status(Status.BAD_REQUEST).build();
	  }
	  
	  List<OrchestrationRuleRegister> ret = new ArrayList<OrchestrationRuleRegister>();
	  List<String> systems = plan.getRegisterSystems();
	  
		//System.out.println(newRule.);
		
		PrintUtil.registerPrefix("sosa", OntologyNames.SOSA_URL);
		PrintUtil.registerPrefix("auto", OntologyNames.BASE_URL);
		PrintUtil.registerPrefix("rdfs", RDFS.uri);
		PrintUtil.registerPrefix("xsd", XSD.NS);
		PrintUtil.registerPrefix("rdf", RDF.getURI());
		
		//System.out.println(PrintUtil.print(newRule));
	  
	  for(String system : systems)
	  {
		  List<Rule> rules = plan.getRules(system);
		  List<String> rulesStr = new ArrayList<String>();
		  for(Rule r : rules)
			  rulesStr.add(PrintUtil.print(r));
		  OrchestrationRuleRegister orchRe = new OrchestrationRuleRegister();
		  orchRe.setSystemName(system);
		  orchRe.setRules(rulesStr);
		  ret.add(orchRe);
	  }
	   
	  return Response.status(Status.OK).entity(ret).header("Access-Control-Allow-Origin", "*").build();
  }
  
  @GET
  @Path("queries")
  public Response getAllQueries() {
	  TreeMap<String, String> queries = analysis.getAllQuries();
	  
	  List<AnalysisQueryRequest> queriesRequest = new ArrayList<AnalysisQueryRequest>();
	  for(String key : queries.keySet())
	  {
		  String query =  queries.get(key);
		  UpdateRequest request =  UpdateFactory.create(query);
		  //Query q = QueryFactory.create(query);
		  //queries.put(key, request.toString());
		  AnalysisQueryRequest q = new AnalysisQueryRequest();
		  q.setName(key);
		  q.setQuery(request.toString());
		  queriesRequest.add(q);
		  
	  }
	  return Response.status(Status.OK).entity(queriesRequest).header("Access-Control-Allow-Origin", "*").build();
  }
  
  
  @GET
  @Path("knowledge")
  public Response getKnowledgeBase()
  {
	  String fileContext = "";
	  
      try
      {
    	  fileContext = new String ( Files.readAllBytes( Paths.get(Constants.knowledgeBaseFileName) ) );
      } 
      catch (IOException e) 
      {
          e.printStackTrace();
      }
	  
	  return Response.status(Status.OK).entity(fileContext).header("Access-Control-Allow-Origin", "*").build();
  }
  
  //@PUT
  @POST
  @Path("register")
  public Response updateRule(OrchestrationRuleRegister rules) {
	  
	  OrchestrationRuleRegister updatedForm = null;
	  if(plan == null)
	  {
		  return Response.status(Status.BAD_REQUEST).build();
	  }
	  List<Rule> updatedRules = plan.RegisterRules(rules);
	  if(updatedRules == null || updatedRules.isEmpty() ) 
		  return Response.status(Status.BAD_REQUEST).build();
	  
	  updatedForm = new OrchestrationRuleRegister();
	  updatedForm.setSystemName(rules.getSystemName());
	  List<String> rulesList =  new ArrayList<String>();
	  for(Rule r : updatedRules)
		  rulesList.add(r.toString());
	  updatedForm.setRules(rulesList);
	  
	    //Return a response with Accepted status code
	    return Response.status(Status.OK).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "*")
				    .header("Access-Control-Allow-Headers", "*")
				    .header("Access-Control-Max-Age", "86400").entity(updatedForm).build();
	  }
	  
  //@PUT
  @POST
  @Path("delete")
  public Response deleteRule(OrchestrationRuleDelete rules)
  {
	  if(plan == null)
	  {
		  return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT").build();
	  }
	  boolean ret =  plan.deleteRules(rules);
	  if(!ret)
		  return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT").build();
	  
	  return Response.status(Status.OK).header("Access-Control-Allow-Origin", "*")
			  						    .header("Access-Control-Allow-Methods", "*")
			  						    .header("Access-Control-Allow-Headers", "*")
			  						    .header("Access-Control-Max-Age", "86400").build();
  }
  
	/*
	 * PUT requests are usually for updating existing resources. The ID is from the
	 * database, to identify the car instance. Usually PUT requests fully update a
	 * resource, meaning fields which are not specified by the client, will also be
	 * null in the database (overriding existing data). PATCH requests are used for
	 * partial updates.
	 */
	@PUT
	//@POST
	@Path("push")
	public Response sendOrchestrationResponse(AdaptationPlan adaptation) {
		/*
		 * Car carFromTheDatabase = cars.get(id); // Throw an exception if the car with
		 * the specified ID does not exist if (carFromTheDatabase != null) { throw new
		 * DataNotFoundException("Car with id " + id + " not found in the database!"); }
		 * // Update the car cars.put(id, updatedCar);
		 * 
		 * // Return a response with Accepted status code
		 */		
		System.out.println("Receive: " + adaptation);
		
		for(Adaptation tadap : adaptation.getAdaptations())
		{
			if(tadap.getType().equals(AdaptationType.SubstitutionAdaptation))
			{
				tadap.setStatus(PlanStatus.EXECUTED);
				break;
			}
		}
		
		
		sentAdapat = adaptation;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String sValue = gson.toJson(adaptation);
		System.out.println("Sending response: ");
		System.out.println(sValue);
		
		return Response.status(Status.OK).header("Access-Control-Allow-Origin", "*").entity(sValue).build();
	}
	
	
	private static AdaptationPlan sentAdapat = null;
	@GET
	@Path("getSentAdapations")
	public Response sentAdapationResponse() {
		String sValue = "";
		if(sentAdapat != null)
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			sValue = gson.toJson(sentAdapat);
			sentAdapat = null;
		}
		
		return Response.status(Status.OK).header("Access-Control-Allow-Origin", "*").entity(sValue).build();
	}
	
	
  

}
