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

import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.arrowhead.autonomic.orchestrator.manager.analysis.Analysis;
import eu.arrowhead.autonomic.orchestrator.manager.analysis.AnalysisQueryRequest;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.Monitor;
import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.Adaptation;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationType;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.OrchestrationRuleDelete;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.OrchestrationRuleRegister;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;

@RestController
public class OrchestrationRegisterResource {

    @Autowired
    public Plan plan;

    @Autowired
    public Monitor monitor;

    @Autowired
    public Analysis analysis;

    @GetMapping(path = Constants.OrchestrationGetAllRulesURI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<OrchestrationRuleRegister> getAllRule() {
        System.out.print("receive request");
        if (plan == null) {
            return null;
        }

        List<OrchestrationRuleRegister> ret = new ArrayList<OrchestrationRuleRegister>();
        List<String> systems = plan.getRegisterSystems();

        PrintUtil.registerPrefix("sosa", OntologyNames.SOSA_URL);
        PrintUtil.registerPrefix("auto", OntologyNames.BASE_URL);
        PrintUtil.registerPrefix("rdfs", RDFS.uri);
        PrintUtil.registerPrefix("xsd", XSD.NS);
        PrintUtil.registerPrefix("rdf", RDF.getURI());
        PrintUtil.registerPrefix("sai", OntologyNames.SAI_URL);
        PrintUtil.registerPrefix("san", OntologyNames.SAN_URL);
        PrintUtil.registerPrefix("dul", OntologyNames.DUL_URL);
        PrintUtil.registerPrefix("DOGONT", OntologyNames.DOGONT_URL);
        PrintUtil.registerPrefix("msm", OntologyNames.MSM_URL);
        PrintUtil.registerPrefix("ioto", OntologyNames.IOTO_URL);
        PrintUtil.registerPrefix("ssn", OntologyNames.SSN_URL);
        PrintUtil.registerPrefix("muo", OntologyNames.MUO_URL);

        for (String system : systems) {
            List<Rule> rules = plan.getRules(system);
            List<String> rulesStr = new ArrayList<String>();
            for (Rule r : rules) {
                rulesStr.add(PrintUtil.print(r));
            }
            OrchestrationRuleRegister orchRe = new OrchestrationRuleRegister();
            orchRe.setSystemName(system);
            orchRe.setRules(rulesStr);
            ret.add(orchRe);
        }

        return ret;
    }

    @GetMapping(path = Constants.OrchestrationGetAllQueriesURI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AnalysisQueryRequest> getAllQueries() {
        TreeMap<String, String> queries = analysis.getAllQuries();

        List<AnalysisQueryRequest> queriesRequest = new ArrayList<AnalysisQueryRequest>();
        for (String key : queries.keySet()) {
            String query = queries.get(key);
            UpdateRequest request = UpdateFactory.create(query);
            AnalysisQueryRequest q = new AnalysisQueryRequest();
            q.setName(key);
            q.setQuery(request.toString());
            queriesRequest.add(q);

        }
        return queriesRequest;
    }

    @GetMapping(path = Constants.OrchestrationGetAllKnowledgeURI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getKnowledgeBase() {
        String fileContext = "";
        System.out.println("request knowledge base");

        try {
            fileContext = new String(Files.readAllBytes(Paths.get(Constants.knowledgeBaseFileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContext;
    }

    // @POST
    @PostMapping(path = Constants.OrchestrationRegisterURI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OrchestrationRuleRegister updateRule(@RequestBody OrchestrationRuleRegister rules) {

        OrchestrationRuleRegister updatedForm = null;
        if (plan == null) {
            return null;
        }
        List<Rule> updatedRules = plan.RegisterRules(rules);
        if (updatedRules == null || updatedRules.isEmpty()) {
            return null;
        }

        updatedForm = new OrchestrationRuleRegister();
        updatedForm.setSystemName(rules.getSystemName());
        List<String> rulesList = new ArrayList<String>();
        for (Rule r : updatedRules) {
            rulesList.add(r.toString());
        }
        // updatedForm.setRules(rulesList);

        // Return a response with Accepted status code
        return updatedForm;
    }

    // @PUT
    @DeleteMapping(path = Constants.OrchestrationDeleteURI)
    public void deleteRule(@RequestBody OrchestrationRuleDelete rules) {
        if (plan == null) {
            return;
        }
        boolean ret = plan.deleteRules(rules);
        if (!ret) {
            return;
        }

    }

    /*
     * PUT requests are usually for updating existing resources. The ID is from the
     * database, to identify the car instance. Usually PUT requests fully update a
     * resource, meaning fields which are not specified by the client, will also be
     * null in the database (overriding existing data). PATCH requests are used for
     * partial updates.
     */
    @PutMapping(path = Constants.OrchestrationPushURI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String sendOrchestrationResponse(@RequestBody AdaptationPlan adaptation) {
        /*
         * Car carFromTheDatabase = cars.get(id); // Throw an exception if the car with
         * the specified ID does not exist if (carFromTheDatabase != null) { throw new
         * DataNotFoundException("Car with id " + id + " not found in the database!"); }
         * // Update the car cars.put(id, updatedCar);
         *
         * // Return a response with Accepted status code
         */
        System.out.println("Receive: " + adaptation);

        for (Adaptation tadap : adaptation.getAdaptations()) {
            if (tadap.getType().equals(AdaptationType.SubstitutionAdaptation)) {
                tadap.setStatus(PlanStatus.EXECUTED);
                break;
            }
        }

        sentAdapat = adaptation;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String sValue = gson.toJson(adaptation);
        System.out.println("Sending response: ");
        System.out.println(sValue);

        return sValue;
    }

    private static AdaptationPlan sentAdapat = null;

    @GetMapping(path = Constants.OrchestrationGetURI)
    @ResponseBody
    public String sentAdapationResponse() {
        String sValue = "";
        if (sentAdapat != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            sValue = gson.toJson(sentAdapat);
            sentAdapat = null;
        }

        return sValue;
    }

    // @POST
    // @PostMapping(path = Constants.OrchestrationServiceRegisterURI, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ResponseBody
    // public BaseConsumer registerService(@RequestBody BaseConsumer service) {
    // if (monitor == null) {
    // return null;
    // }
    // BaseConsumer dummy = BaseConsumerFactory.createBaseConsumer(service.getSystemName(), service.getServiceName(),
    // service.getServiceEndpoint());
    // boolean updated = monitor.AddConsumer(dummy);
    // if (!updated) {
    // return null;
    // }
    //
    // // Return a response with Accepted status code
    // return service;
    // }

    @GetMapping(path = Constants.OrchestrationGetAllConsumersURI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TreeMap<String, List<Rule>> getAllConsumers() {
        return null;
    }

}
