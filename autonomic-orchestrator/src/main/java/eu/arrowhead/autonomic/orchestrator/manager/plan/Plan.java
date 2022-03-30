package eu.arrowhead.autonomic.orchestrator.manager.plan;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.PrintUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.Adaptation;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.OrchestrationRuleDelete;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.OrchestrationRuleRegister;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;

@Service
public class Plan {

    // private PlanWorker planWorker;
    private TreeMap<String, List<Rule>> ConsumerRulesTreeMap;
    private TreeMap<String, Long> ruleLastUpdated;

    private TreeMap<String, AdaptationPlan> ConsumerAdaptationPlansTreeMap;
    private ReentrantLock updatePlanLock;

    private static final Logger log = LoggerFactory.getLogger(Plan.class);

    public Plan() {
        ConsumerRulesTreeMap = new TreeMap<String, List<Rule>>();
        ruleLastUpdated = new TreeMap<String, Long>();
        // planWorker = new PlanWorker(this, Constants.PlanWorkerInterval);

        BuiltinRegistry.theRegistry.register(new SubstitutionServiceBuiltin(this));
        BuiltinRegistry.theRegistry.register(new ConfigureBuiltin(this));

        ConsumerAdaptationPlansTreeMap = new TreeMap<String, AdaptationPlan>();
        updatePlanLock = new ReentrantLock();
        // UpdateRules();
        UpdateRuleFromKnowledgeBase();
    }

    public void UpdateAdaptationPlan(String name, Adaptation adapt) {
        updatePlanLock.lock();

        try {
            AdaptationPlan adaptationPlan = ConsumerAdaptationPlansTreeMap.get(name);
            if (adaptationPlan == null) {
                adaptationPlan = new AdaptationPlan(name);
            }
            if (!adaptationPlan.getAdaptations().contains(adapt)) {
                adaptationPlan.getAdaptations().add(adapt);

                if (adapt.getStatus() == PlanStatus.NEW) {
                    adaptationPlan.setStatus(PlanStatus.NEW);
                }

                log.debug("Plan Updated Adaptation: " + adaptationPlan);
                System.out.println("Plan Updated Adaptation: " + adaptationPlan);
            }
            ConsumerAdaptationPlansTreeMap.put(name, adaptationPlan);

        } finally {
            updatePlanLock.unlock();
        }

    }

    public void UpdateAdaptationPlanStatus(String name, PlanStatus status) {
        updatePlanLock.lock();
        try {
            AdaptationPlan adaptationPlan = ConsumerAdaptationPlansTreeMap.get(name);
            if (adaptationPlan != null) {
                adaptationPlan.setStatus(status);
            }

            // ConsumerAdaptationPlansTreeMap.put(name, adaptationPlan);
        } finally {
            updatePlanLock.unlock();
        }
    }

    public AdaptationPlan GetAdaptationPlan(String name) {
        updatePlanLock.lock();

        AdaptationPlan ret = null;

        try {
            if (ConsumerAdaptationPlansTreeMap.containsKey(name)) {
                AdaptationPlan plan = ConsumerAdaptationPlansTreeMap.get(name);
                ret = (AdaptationPlan) plan.clone();
            }
        } finally {
            updatePlanLock.unlock();
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public TreeMap<String, AdaptationPlan> GetAllAdaptationPlans() {
        updatePlanLock.lock();

        TreeMap<String, AdaptationPlan> ret = null;

        try {
            ret = (TreeMap<String, AdaptationPlan>) ConsumerAdaptationPlansTreeMap.clone();

        } finally {
            updatePlanLock.unlock();
        }

        return ret;
    }

    public void RemoveAllAdaptations(String name) {
        updatePlanLock.lock();

        try {
            if (ConsumerAdaptationPlansTreeMap.containsKey(name)) {
                AdaptationPlan plan = ConsumerAdaptationPlansTreeMap.get(name);
                plan.getAdaptations().clear();

                log.debug("Plan RemoveAllAdaptations: " + plan);
                System.out.println("Plan RemoveAllAdaptations: " + plan);
            }
        } finally {
            updatePlanLock.unlock();
        }
    }

    public void RemoveAdaptationPlans(String name, List<Adaptation> adapts) {
        updatePlanLock.lock();

        try {
            if (ConsumerAdaptationPlansTreeMap.containsKey(name)) {
                AdaptationPlan plan = ConsumerAdaptationPlansTreeMap.get(name);
                plan.getAdaptations().removeAll(adapts);

                log.debug("Plan RemoveAdaptationPlans: " + plan);
                System.out.println("Plan RemoveAdaptationPlans: " + plan);
            }
        } finally {
            updatePlanLock.unlock();
        }
    }

    @Scheduled(fixedDelay = Constants.PlanWorkerInterval)
    public void WorkerProcess() {
        // UpdateRules();
        StartReasoning();
    }

    private void StartReasoning() {
        log.debug("Planning Reasoning Rules");
        // System.out.println("Planning Reasoning Rules");

        List<Rule> rules = new ArrayList<Rule>();
        for (List<Rule> rls : ConsumerRulesTreeMap.values()) {
            rules.addAll(rls);
        }
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

    // @formatter:off
    private void UpdateRuleFromKnowledgeBase() {
        String queryString = Constants.PREFIX_STRING
                                + "select ?s ?r ?b \n"
                                + "where { ?s :hasJenaRule ?r . \n" + "?r :hasBody ?b . \n" + "}";

        List<QuerySolution> results = KnowledgeBase.getInstance().ExecuteSelectQuery(queryString);

        for (QuerySolution soln : results) {
            Resource system = soln.getResource("s");
            String systemName = system.getLocalName();

            Literal body = soln.getLiteral("b");
            String bodyString = body.getString();

            List<Rule> rules = new ArrayList<Rule>();
            Rule r = Rule.parseRule(bodyString);

            log.debug("Plan: New rule registered for consumer: " + systemName);
            System.out.println("Plan: New rule registered for consumer: " + systemName);
            System.out.println(r);

            if (ConsumerRulesTreeMap.containsKey(systemName)) {
                rules.addAll(ConsumerRulesTreeMap.get(systemName));
            }
            rules.add(r);
            ConsumerRulesTreeMap.put(systemName, rules);

        }

        String consumerQueryString = Constants.PREFIX_STRING
                + "select ?s \n"
                + "where { ?s rdf:type sai:ArrowheadConsumer . \n" + "}";

        List<QuerySolution> consumers = KnowledgeBase.getInstance().ExecuteSelectQuery(consumerQueryString);

        for (QuerySolution consumer : consumers) {
            Resource system = consumer.getResource("s");
            String systemName = system.getLocalName();
            log.debug("Plan: New consumer registered: " + consumer);
            System.out.println("Plan: New consumer registered: " + consumer);
            if (!ConsumerRulesTreeMap.containsKey(systemName)) {
                ConsumerRulesTreeMap.put(systemName, new ArrayList<Rule>());
            }
        }
    }

    public List<Rule> RegisterRules(OrchestrationRuleRegister requestRules) {

        List<Rule> rules = new ArrayList<Rule>();
        List<Rule> updatedRules = new ArrayList<Rule>();

        for(String rule : requestRules.getRules())
        {
            try {
                rules.add(Rule.parseRule(PrintUtil.print(rule)));
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
            //Rule newRule = new Rule(requestRules.getSystemName() + "_rule" + i , r.getHead(), r.getBody());
            updatedRules.add(r);
            currentRules.add(r);
            i++;
            System.out.println(r);
        }

        ConsumerRulesTreeMap.put(requestRules.getSystemName(), currentRules);

        AddRuleToKnowledgeBase(requestRules.getSystemName(), updatedRules);


        return updatedRules;
    }

    // @formatter:off
    private void AddRuleToKnowledgeBase(String name, List<Rule> rules) {
        String updateCurentTimeQuery = Constants.PREFIX_STRING
                                    + "insert data { \n ";

        PrintUtil.removePrefix("sosa");
        PrintUtil.removePrefix("auto");
        PrintUtil.removePrefix("rdfs");
        PrintUtil.removePrefix("xsd");
        PrintUtil.removePrefix("rdf");
        PrintUtil.removePrefix("sai");
        PrintUtil.removePrefix("DOGONT");

        for (Rule r : rules) {
            updateCurentTimeQuery = updateCurentTimeQuery
                                    + ":" + name + " :hasJenaRule :" + r.getName() + " . \n"
                                    + ":" + r.getName() + " :hasBody \"" + r.toString() + "\" . \n";
        }

        updateCurentTimeQuery += " }";

        System.out.println(updateCurentTimeQuery);

        List<String> queries = new ArrayList<String>();
        queries.add(updateCurentTimeQuery);

        KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
    }

    // @formatter:off
    private void DeleteRuleFromKnowledgeBase(String name, List<Rule> rules) {
        List<String> queries = new ArrayList<String>();

        for (Rule r : rules) {
            String updateString = Constants.PREFIX_STRING
                                    + "delete { :" + name + " :hasJenaRule :" + r.getName() + " . \n" + ":" + r.getName() + " :hasBody ?body . \n" + "} \n"
                                    + "where { :" + name + " :hasJenaRule :" + r.getName() + " . \n" + ":" + r.getName() + " :hasBody ?body . \n" + "}";

            queries.add(updateString);
        }

        KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);

    }

    public List<Rule> getRules(String systemName) {
        List<Rule> ret = new ArrayList<Rule>();
        if (ConsumerRulesTreeMap.containsKey(systemName)) {
            ret.addAll(ConsumerRulesTreeMap.get(systemName));
        }
        return ret;
    }

    public List<String> getRegisterSystems() {
        List<String> ret = new ArrayList<String>();
        ret.addAll(ConsumerRulesTreeMap.keySet());

        return ret;
    }

    public boolean deleteRules(OrchestrationRuleDelete deleteRequest) {
        if (ConsumerRulesTreeMap.containsKey(deleteRequest.getSystemName())) {

            List<Rule> deleteRules = new ArrayList<Rule>();

            for(String rule : deleteRequest.getRules())
            {
                try {
                    deleteRules.add(Rule.parseRule(PrintUtil.print(rule)));
                }
                catch(Exception e)
                {
                    //e.printStackTrace();
                }
            }

            List<Rule> currentRulesList = new ArrayList<Rule>();
            List<Rule> updatedRulesList = new ArrayList<Rule>();
            currentRulesList.addAll(ConsumerRulesTreeMap.get(deleteRequest.getSystemName()));
            updatedRulesList.addAll(ConsumerRulesTreeMap.get(deleteRequest.getSystemName()));
            for (Rule r : currentRulesList) {
                if (deleteRules.contains(r)) {
                    updatedRulesList.remove(r);
                }
            }
            ConsumerRulesTreeMap.put(deleteRequest.getSystemName(), updatedRulesList);

            DeleteRuleFromKnowledgeBase(deleteRequest.getSystemName(), deleteRules);
            return true;
        }

        return false;
    }

    private void UpdateRules() {
        try {
            File dir = new File(Constants.planQueriesDir);
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    // System.out.println(filename);
                    return filename.endsWith(".rule");
                }
            });

            for (File f : files) {

                String name = f.getName();
                name = name.substring(0, name.length() - 5);

                // Check if file recently updated
                if (ruleLastUpdated.containsKey(name)) {
                    long lastmodified = f.lastModified();
                    long lastupdated = ruleLastUpdated.get(name);

                    if (lastupdated >= lastmodified) {
                        continue;
                    }
                }

                List<Rule> rules = Rule.rulesFromURL(f.getAbsolutePath());

                log.debug("Plan: New rule registered for consumer: " + name);
                System.out.println("Plan: New rule registered for consumer: " + name);

                List<Rule> modifiedRules = new ArrayList<Rule>();

                int i = 1;
                for (Rule r : rules) {
                    //Rule newRule = new Rule(name + "_rule" + i, r.getHead(), r.getBody());
                    modifiedRules.add(r);
                    i++;
                    System.out.println(r);

                    KnowledgeBase.getInstance().AddConsumerJenaRule(name, name + "_rule" + i);
                    // System.out.println(newRule.);

                    // PrintUtil.registerPrefix("sosa", OntologyNames.SOSA_URL);
                    // PrintUtil.registerPrefix("auto", OntologyNames.BASE_URL);
                    // PrintUtil.registerPrefix("rdfs", RDFS.uri);
                    // PrintUtil.registerPrefix("xsd", XSD.NS);
                    // PrintUtil.registerPrefix("rdf", RDF.getURI());

                    // System.out.println(PrintUtil.print(newRule));

                }

                ConsumerRulesTreeMap.put(name, modifiedRules);
                ruleLastUpdated.put(name, f.lastModified());

                // for(Rule r : rules)

            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}
