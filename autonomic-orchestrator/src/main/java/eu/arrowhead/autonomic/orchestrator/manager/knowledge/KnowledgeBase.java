package eu.arrowhead.autonomic.orchestrator.manager.knowledge;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
import org.apache.jena.rdf.model.Literal;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.arrowhead.autonomic.orchestrator.store.OrchestrationStoreEntryDTO;
import eu.arrowhead.common.dto.shared.SystemResponseDTO;

@Service
public class KnowledgeBase {

    private static KnowledgeBase instance;

    private ReentrantLock lock;
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBase.class);

    // private TreeMap<String, >

    // private KnowledgeBaseWorker knowledgeBaseWorker;

    private KnowledgeBase() {
        lock = new ReentrantLock();
    }

    public static KnowledgeBase getInstance() {
        if (instance == null) {
            instance = new KnowledgeBase();
        }
        return instance;
    }

    public void AddStateMent(Statement s) {
        lock.lock();

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            model.add(s);

            dataset.commit();

        } catch (Exception e) {
            log.error("Fail to add statement: " + e.getMessage());
            System.err.println("Fail to add statement: " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }
    }

    @Scheduled(fixedDelay = Constants.KnowledgeBaseWorkerInterval)
    public void WorkerProcess() {
        WriteModelToFile(Constants.knowledgeBaseFileName);

    }

    public void WriteModelToFile(String filename) {

        lock.lock();

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);
        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            model.setNsPrefix("sosa", OntologyNames.SOSA_URL);
            model.setNsPrefix(":", OntologyNames.BASE_URL);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);

            model.setNsPrefix("rdf", RDF.getURI());
            model.setNsPrefix("sai", OntologyNames.SAI_URL);
            model.setNsPrefix("san", OntologyNames.SAN_URL);
            model.setNsPrefix("dul", OntologyNames.DUL_URL);
            model.setNsPrefix("DOGONT", OntologyNames.DOGONT_URL);
            model.setNsPrefix("msm", OntologyNames.MSM_URL);
            model.setNsPrefix("ioto", OntologyNames.IOTO_URL);
            model.setNsPrefix("ssn", OntologyNames.SSN_URL);
            model.setNsPrefix("muo", OntologyNames.MUO_URL);

            FileOutputStream newFile = new FileOutputStream(new File(filename));
            model.write(newFile, "TTL");
            newFile.close();

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to WriteModelToFile: " + e.getMessage());
            System.err.println("Fail to WriteModelToFile: " + e.getMessage());
            // e.printStackTrace();
        } finally {

            dataset.end();
            lock.unlock();

        }
    }

    public List<QuerySolution> ExecuteSelectQuery(String queries) {

        lock.lock();
        List<QuerySolution> ret = new ArrayList<QuerySolution>();
        log.debug("Knowledgebase Executing Select query");
        // System.out.println("Knowledgebase Executing Select query");

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.READ);

        try {
            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            Query query = QueryFactory.create(queries);
            // System.out.println(query);
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

    public void ExecuteUpdateQueries(List<String> queries) {
        lock.lock();
        // AddService(serviceName);

        log.debug("Knowledgebase Executing Update queries");
        // System.out.println("Knowledgebase Executing Update queries");

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            for (String query : queries) {

                UpdateAction.parseExecute(query, model);
            }

            model.setNsPrefix("sosa", OntologyNames.SOSA_URL);
            model.setNsPrefix(":", OntologyNames.BASE_URL);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);

            model.setNsPrefix("rdf", RDF.getURI());
            model.setNsPrefix("sai", OntologyNames.SAI_URL);
            model.setNsPrefix("san", OntologyNames.SAN_URL);
            model.setNsPrefix("dul", OntologyNames.DUL_URL);
            model.setNsPrefix("DOGONT", OntologyNames.DOGONT_URL);
            model.setNsPrefix("msm", OntologyNames.MSM_URL);
            model.setNsPrefix("ioto", OntologyNames.IOTO_URL);
            model.setNsPrefix("ssn", OntologyNames.SSN_URL);
            model.setNsPrefix("muo", OntologyNames.MUO_URL);
            // model.write(new FileOutputStream(new File("./dataset.txt")), "TTL" );

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to execute query: " + e.getMessage());
            System.err.println("Fail to execute query: " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }
    }

    public void Reasoning(List<Rule> rules) {
        lock.lock();
        log.debug("Knowledgebase Reasoning Rules");
        // System.out.println("Knowledgebase Reasoning Rules");

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.READ);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            Reasoner reasoner = new GenericRuleReasoner(rules);

            InfModel infModel = ModelFactory.createInfModel(reasoner, model);

            // System.out.println("Knowledgebase Reasoning Rules prepare");

            infModel.prepare();

        } catch (Exception e) {
            log.error("Fail to reason rules: " + e.getMessage());
            System.err.println("Fail to reason rules:  " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }
    }

    // @formatter:off
    public void AddConsumerJenaRule(String consumerName, String jenaRule) {
        lock.lock();
        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);
        try {
             Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String insertString = Constants.PREFIX_STRING
                                + "insert data { "
                                    + ":" + consumerName + " :hasJenaRule :" + jenaRule + " . \n"
//                                + "} where {"
//                                    + ":" + consumerName + " rdf:type :ArrowheadConsumer . \n"
                                + "}";
            UpdateAction.parseExecute(insertString, model);

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to add sensor: " + e.getMessage());
            System.err.println("Fail to add sensor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

 // @formatter:off
    public void AddOrchestrationStoreEntry(OrchestrationStoreEntryDTO entry) {
        lock.lock();
        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String entryName = "StoreRule_" + entry.getId();

            String insertString = Constants.PREFIX_STRING
                                + "insert data { "
                                    + ":" + entryName + " :hasContext :OrchestrationStoreRule . \n"
                                    + ":" + entryName + " :hasId \"" + entry.getId() + "\"^^xsd:int . \n"
                                    + ":" + entryName + " :usedInContext " + ":" + entry.getConsumerSystem().getSystemName() + " . \n"
                                    + ":" + entryName + " :hasServiceUsage " + ":" + entry.getServiceDefinition().getServiceDefinition() + " . \n"
//                                + "} where {"
//                                    + ":" + consumerName + " rdf:type :ArrowheadConsumer . \n"
                                + "}";
            UpdateAction.parseExecute(insertString, model);

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to add sensor: " + e.getMessage());
            System.err.println("Fail to add sensor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

 // @formatter:off
    public String DeleteOrchestrationStoreEntry(String consumerName, String serviceDefinition) {
        String entryId = null;
        lock.lock();
        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String queryString = Constants.PREFIX_STRING
                    + "select ?id \n"
                    + "where {"
                        + "?storeRule :usedInContext " + ":" + consumerName + " . \n"
                        + "?storeRule :hasServiceUsage " + ":" + serviceDefinition + " . \n"
                        + "?storeRule :hasId ?id . \n"
                    + "}";

            List<QuerySolution> results = KnowledgeBase.getInstance().ExecuteSelectQuery(queryString);

            for (QuerySolution soln : results) {
                Literal body = soln.getLiteral("id");
                entryId = body.getString();

            }

            String deleteString = Constants.PREFIX_STRING
                                + "delete { "
                                    + "?storeRule :hasContext :OrchestrationStoreRule . \n"
                                    + "?storeRule :hasId ?id . \n"
                                    + "?storeRule :usedInContext " + ":" + consumerName + " . \n"
                                    + "?storeRule :hasServiceUsage " + ":" + serviceDefinition + " . \n"
                                + "} where {"
                                    + "?storeRule :usedInContext " + ":" + consumerName + " . \n"
                                    + "?storeRule :hasServiceUsage " + ":" + serviceDefinition + " . \n"
                                + "}";
            UpdateAction.parseExecute(deleteString, model);

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to add sensor: " + e.getMessage());
            System.err.println("Fail to add sensor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }
        return entryId;
    }

    // @formatter:off
    public void AddConsumer(SystemResponseDTO consumer, String serviceName, String providerName) {
        lock.lock();
        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String deleteString = Constants.PREFIX_STRING
                                + "delete data{ "
                                    + ":" + consumer.getSystemName() + " rdf:type sai:ArrowheadConsumer . \n"
                                    + ":" + consumer.getSystemName() + " sai:consumesService :" + serviceName + " . \n"
                                    + ":" + consumer.getSystemName() + " :hasId \"" + consumer.getId() + "\"^^xsd:int . \n"
                                + "}";
            UpdateAction.parseExecute(deleteString, model);

            String addString = Constants.PREFIX_STRING
                                + "insert data{ "
                                    + ":" + consumer.getSystemName() + " rdf:type sai:ArrowheadConsumer . \n"
                                    + ":" + consumer.getSystemName() + " sai:consumesService :" + serviceName + " . \n"
                                    + ":" + consumer.getSystemName() + " :hasId \"" + consumer.getId() + "\"^^xsd:int . \n"
                                + "}";

            UpdateAction.parseExecute(addString, model);

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to add sensor: " + e.getMessage());
            System.err.println("Fail to add sensor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

    // @formatter:off
    public void AddObservation(String observationId, String sensorId, long timestamp, String value,
            String featureOfInterest, String unit, String datatype) {
        lock.lock();

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);
        boolean isObservationExisted = false;

        try {
            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String queryString = Constants.PREFIX_STRING
                                    // "select ?obs \n" +
                                    + "ask {"
                                        + ":" + observationId + " rdf:type sosa:Observation . \n"
                                    + "}";

            String updateString = Constants.PREFIX_STRING +
                    "delete { :" + observationId + " sosa:hasSimpleResult ?value. \n" +
                    ":" + observationId + " sosa:resultTime ?time. \n" +
                    "} \n" +
                    "insert { :" + observationId + " sosa:hasSimpleResult \"" + value + "\"^^xsd:" + datatype + " . \n" +
                    ":" + observationId + " sosa:resultTime  \"" + timestamp + "\"^^xsd:long . \n" +
                    "} \n" +
                    "where { :" + observationId + " sosa:hasSimpleResult ?value. \n" +
                    ":" + observationId + " sosa:resultTime ?time. \n" +
                    "}";

            String addString = Constants.PREFIX_STRING
                                + "insert data { "
                                    + ":" + observationId + " rdf:type sosa:Observation . \n"
                                    + ":" + observationId + " sosa:madeBySensor :" + sensorId + " . \n"
                                    + ":" + observationId + " sosa:hasFeatureOfInterest :" + featureOfInterest + " . \n"
                                    + ":" + observationId + " sosa:hasSimpleResult \"" + value + "\"^^xsd:" + datatype + " . \n"
                                    + ":" + observationId + " sosa:resultTime  \"" + timestamp + "\"^^xsd:long . \n"
                                    + ":" + observationId + " :hasUnit  \"" + unit + "\" . \n"
                                + "}";

            // System.out.println(addString);

            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            isObservationExisted = qexec.execAsk();

            if (isObservationExisted) {
                UpdateAction.parseExecute(updateString, model);
            } else {
                UpdateAction.parseExecute(addString, model);
            }

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to AddObservation: " + e.getMessage());
            System.err.println("Fail to AddObservation:  " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

    // @formatter:off
    public void AddSensor(String sensorName, String serviceName, String location, String producer,
            String serviceDefinition) {
        lock.lock();
        AddService(serviceName, producer, serviceDefinition);

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String deleteString = Constants.PREFIX_STRING
                                    + "delete data{ "
                                        + ":" + sensorName + " rdf:type :SensorUnit . \n"
                                    // ":" + sensorName + " :hasID \"" + sensorName + "\" . \n" +
                                        + ":" + sensorName + " sai:hasService :" + serviceName + " . \n"
                                        + ":" + sensorName + " sai:hasLocation :" + location + " . \n"
                                    + "}";
            UpdateAction.parseExecute(deleteString, model);

            String addString = Constants.PREFIX_STRING
                                + "insert data{ "
                                    + ":" + sensorName + " rdf:type :SensorUnit . \n"
                    // ":" + sensorName + " :hasID \"" + sensorName + "\" . \n" +
                                    + ":" + sensorName + " sai:hasService :" + serviceName + " . \n"
                                    + ":" + sensorName + " sai:hasLocation :" + location + " . \n"
                                + "}";

            UpdateAction.parseExecute(addString, model);

            dataset.commit();

        } catch (Exception e) {
            log.error("Fail to add sensor: " + e.getMessage());
            System.err.println("Fail to add sensor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

    // @formatter:off
    public void AddService(String serviceName, String producer, String serviceDefinition) {
        lock.lock();

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String deleteString = Constants.PREFIX_STRING +
                    "delete data{ "   +
                    ":" + serviceName + " rdf:type sai:ArrowheadService . \n" +
                    ":" + serviceName + " sai:hasServiceDefinition " + "\"" + serviceDefinition +  "\"" + " . \n" +
                    ":" + producer + " rdf:type sai:ArrowheadProducer . \n" +
                    ":" + producer + " sai:producesService :" + serviceName + " . \n" +
                    //":" + serviceName + " :hasID \"" + serviceName + "\" . \n" +
                    "}";

            UpdateAction.parseExecute(deleteString, model);

            String addString = Constants.PREFIX_STRING
                                + "insert data{ "
                                    + ":" + serviceName + " rdf:type sai:ArrowheadService . \n"
                                    + ":" + serviceName + " sai:hasServiceDefinition " + "\"" + serviceDefinition + "\"" + " . \n"
                                    + ":" + producer + " rdf:type sai:ArrowheadProducer . \n"
                                    + ":" + producer + " sai:producesService :" + serviceName + " . \n"
                                    // ":" + serviceName + " :hasID \"" + serviceName + "\" . \n" +
                                + "}";

            UpdateAction.parseExecute(addString, model);

            dataset.commit();

        } catch (Exception e) {
            log.error("Fail to add service: " + e.getMessage());
            System.err.println("Fail to add service: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

}
