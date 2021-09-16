package eu.arrowhead.autonomic.orchestrator.manager.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.model.BaseConsumer;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.model.BaseConsumerFactory;
//import no.prediktor.apis.demo.consumer.DemoConsumer;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.util.CoreServiceUri;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.core.CoreSystemService;
import eu.arrowhead.common.dto.shared.SenML;

@Service
@Configurable
public class Monitor {

    @Autowired
    private ArrowheadService arrowheadService;

    @Autowired
    protected SSLProperties sslProperties;

    private CoreServiceUri dataManagerUri = null;

    // private ReentrantLock lock;
    private TreeMap<String, BaseConsumer> consumers;
    private String consumerCSV = "consumer.csv";

    private static final Logger log = LoggerFactory.getLogger(Monitor.class);

    private String getInterface() {
        return sslProperties.isSslEnabled() ? Constants.INTERFACE_SECURE : Constants.INTERFACE_INSECURE;
    }

    public Monitor() {

        // lock = new ReentrantLock();
        consumers = new TreeMap<String, BaseConsumer>();
        init();
    }

    private void init() {
        File tmpDir = new File(consumerCSV);
        boolean exists = tmpDir.exists();

        try {
            if (!exists) {
                FileOutputStream fos;

                fos = new FileOutputStream(consumerCSV);

                fos.close();
            } else {
                BufferedReader csvReader = new BufferedReader(new FileReader(consumerCSV));
                String row;
                // Service name, service endpoint
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    BaseConsumer dummy = BaseConsumerFactory.createBaseConsumer(data[0], data[1], data[2]);
                    this.consumers.put(data[0], dummy);
                }
                csvReader.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean AddConsumer(BaseConsumer consumer) {
        try {
            // consumer.setMonitor(this);
            this.consumers.put(consumer.getServiceName(), consumer);
            saveConsumersToFile();
            // consumer.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public String GetAllConsumers() {
        JSONArray consumersArr = new JSONArray();
        Gson gson = new Gson();
        for (Map.Entry<String, BaseConsumer> entry : consumers.entrySet()) {
            JSONObject consumerObj = new JSONObject();
            consumerObj.put("consumer", entry.getKey());
            consumerObj.put("description", gson.toJson(entry.getValue()));
            consumersArr.add(consumerObj);
        }
        return consumersArr.toString();
    }

    @Scheduled(fixedDelay = Constants.MonitorWorkerInterval)
    public void WorkerProcess() {
        AddObservationFromDataManager();
        String updateCurentTimeQuery = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                + "prefix xsd: <" + XSD.getURI() + ">\n" + "delete { :DateTimeNow :hasValue ?Value} \n"
                + "insert { :DateTimeNow :hasValue \"" + System.currentTimeMillis() + "\"^^xsd:long } \n"
                + "where {  :DateTimeNow :hasValue ?Value . \n" + "}";

        // System.out.println(updateCurentTimeQuery);

        List<String> queries = new ArrayList<String>();
        queries.add(updateCurentTimeQuery);
        // queries.add(offlineString);
        // queries.add(onlineString);
        KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);

        // KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");

        // final CoreServiceUri uri = arrowheadService.getCoreServiceUri(CoreSystemService.HISTORIAN_SERVICE);
        //
        // DataManagerServicesResponseDTO response = arrowheadService.consumeServiceHTTP(
        // DataManagerServicesResponseDTO.class, HttpMethod.GET, uri.getAddress(), uri.getPort(), uri.getPath(),
        // getInterface(), null, null, new String[0]);
        //
        // System.out.println(response.toString());

    }

    private void AddObservationFromDataManager() {
        try {
            if (dataManagerUri == null) {
                dataManagerUri = arrowheadService.getCoreServiceUri(CoreSystemService.HISTORIAN_SERVICE);
            }
            if (dataManagerUri != null) {
                for (Entry<String, BaseConsumer> entry : consumers.entrySet()) {
                    String path = String.format("%s/%s/%s", dataManagerUri.getPath(), entry.getValue().getSystemName(),
                            entry.getValue().getServiceName());
                    SenML[] response = arrowheadService.consumeServiceHTTP(SenML[].class, HttpMethod.GET,
                            dataManagerUri.getAddress(), dataManagerUri.getPort(), path, getInterface(), null, null,
                            new String[0]);
                    if (response[1].getVs() != null) {
                        long time = 0;
                        if (response[1].getT() != null) {
                            time = response[1].getT().longValue();
                        }
                        AddObservation("Observation_" + response[0].getBn(), "Service_" + response[0].getBn(), time,
                                response[1].getVs(), response[1].getN(), response[1].getU(), "double");
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    // public void start() {
    // monitorWorker.start();
    // }
    //
    // public void stop() {
    // for (BaseConsumerWorker consumer : consumers.values()) {
    // consumer.stop();
    // }
    // monitorWorker.stop();
    // }

    public void AddObservation(String observationId, String sensorId, long timestamp, String value,
            String featureOfInterest, String unit, String datatype) {
        log.debug("Monitor Updating Observation: " + observationId);
        // System.out.println("Monitor Updating Observation: " + observationId);

        KnowledgeBase.getInstance().AddObservation(observationId, sensorId, timestamp, value, featureOfInterest, unit,
                datatype);
    }

    private void saveConsumersToFile() {
        try {
            FileWriter csvWriter = new FileWriter(consumerCSV);
            for (Entry<String, BaseConsumer> entry : consumers.entrySet()) {
                csvWriter.append(entry.getValue().getSystemName());
                csvWriter.append(",");
                csvWriter.append(entry.getValue().getServiceName());
                csvWriter.append(",");
                csvWriter.append(entry.getValue().getServiceEndpoint());
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {

        }
    }

    public static void main4(String[] args) throws FileNotFoundException {

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String updateCurentTimeQuery = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <"
                    + RDFS.getURI() + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <"
                    + OntologyNames.SOSA_URL + ">\n" + "prefix xsd: <" + XSD.getURI() + ">\n" +
                    // "delete { :DateTimeNow :hasValue ?Value} \n" +
                    "insert data { :DateTimeNow :hasValue \"" + new Date().getTime() + "\"^^xsd:long } \n"
            // "select distinct ?sensor ?service ?observation ?time \n" +
            // "where { :DateTimeNow :hasValue ?Value . \n" +
            // "}"
            ;

            System.out.println(updateCurentTimeQuery);

            UpdateAction.parseExecute(updateCurentTimeQuery, model);

            model.setNsPrefix("sosa", OntologyNames.SOSA_URL);
            model.setNsPrefix(":", OntologyNames.BASE_URL);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            model.write(new FileOutputStream(new File("./dataset.ttl")), "TTL");

            dataset.commit();

        } finally {
            dataset.end();

        }

    }

    public static void main5(String[] args) throws FileNotFoundException {

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String deviceID1 = "3244631";
            String deviceID2 = "2999285";

            Resource FeatureOfInterest = model.createResource(OntologyNames.SOSA_URL + "FeatureOfInterest", RDFS.Class);
            Resource Temperature = model.createResource(OntologyNames.BASE_URL + "Temperature", FeatureOfInterest);
            Property hasFeatureOfInterest = model.createProperty(OntologyNames.SOSA_URL + "hasFeatureOfInterest");

            Property hasLocation = model.createProperty(OntologyNames.SOSA_URL + "hasLocation");

            Resource Location = model.createResource(OntologyNames.BASE_URL + "PartLocation", RDFS.Class);
            Resource TopMiddle = model.createResource(OntologyNames.BASE_URL + "TopMiddle", Location);

            Resource observation1 = model.createResource(OntologyNames.BASE_URL + "Observation_" + deviceID1)
                    .addProperty(hasFeatureOfInterest, Temperature);
            Resource observation2 = model.createResource(OntologyNames.BASE_URL + "Observation_" + deviceID2)
                    .addProperty(hasFeatureOfInterest, Temperature);

            Resource Device1Rsr = model.createResource(OntologyNames.BASE_URL + "Device_" + deviceID1)
                    .addProperty(hasLocation, TopMiddle);

            Resource Device2Rsr = model.createResource(OntologyNames.BASE_URL + "Device_" + deviceID2)
                    .addProperty(hasLocation, TopMiddle);

            model.setNsPrefix("sosa", OntologyNames.SOSA_URL);
            model.setNsPrefix(":", OntologyNames.BASE_URL);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            model.write(new FileOutputStream(new File("./dataset.ttl")), "TTL");

            dataset.commit();

        } finally {
            dataset.end();

        }
    }

    public static void main3(String[] args) throws FileNotFoundException {
        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String offlineString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                    + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    + "prefix xsd: <" + XSD.getURI() + ">\n" + "delete { ?service :hasState :OnlineState} \n"
                    + "insert { ?service :hasState :OfflineState}  \n" +
                    // "select distinct ?sensor ?service ?observation ?time \n" +
                    "where { ?sensor rdf:type :SensorUnit . \n" + "?service rdf:type :Service . \n"
                    + "?observation rdf:type sosa:Observation . \n" + "?observation sosa:madeBySensor  ?sensor . \n"
                    + "?sensor :hasService  ?service . \n" + "?observation sosa:resultTime ?time . \n"
                    + "?service :hasState :OnlineState . \n" + ":DateTimeNow :hasValue ?now . \n"
                    + "filter(?time < ?now - 6000) . \n" + "}";

            String onlineString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                    + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    + "prefix xsd: <" + XSD.getURI() + ">\n" + "delete { ?service :hasState :OfflineState} \n"
                    + "insert { ?service :hasState :OnlineState}  \n" +
                    // "select distinct ?sensor ?service ?observation ?time \n" +
                    "where { ?sensor rdf:type :SensorUnit . \n" + "?service rdf:type :Service . \n"
                    + "?observation rdf:type sosa:Observation . \n" + "?observation sosa:madeBySensor  ?sensor . \n"
                    + "?sensor :hasService  ?service . \n" + "?observation sosa:resultTime ?time . \n"
                    + "?service :hasState :OfflineState . \n" + ":DateTimeNow :hasValue ?now . \n"
                    + "filter(?time > ?now - 6000) . \n" + "}";

            UpdateAction.parseExecute(offlineString, model);
            UpdateAction.parseExecute(onlineString, model);

            model.setNsPrefix("sosa", OntologyNames.SOSA_URL);
            model.setNsPrefix(":", OntologyNames.BASE_URL);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            model.write(new FileOutputStream(new File("./dataset.ttl")), "TTL");

            System.out.println(offlineString);

            dataset.commit();

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            dataset.end();
        }
    }

    public static void main1(String[] args) {
        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.READ);

        try {
            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + "DemoModel");

            StmtIterator iter = model.listStatements();

            // print out the predicate, subject and object of each statement
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement(); // get next statement
                Resource subject = stmt.getSubject(); // get the subject
                Property predicate = stmt.getPredicate(); // get the predicate
                RDFNode object = stmt.getObject(); // get the object

                System.out.print(subject.toString());
                System.out.print(" " + predicate.toString() + " ");
                if (object instanceof Resource) {
                    System.out.print(object.toString());
                } else {
                    // object is a literal
                    System.out.print(" \"" + object.toString() + "\"");
                }

                System.out.println(" .");

                // dataset.commit();
            }
        } finally {
            dataset.end();
        }
    }

    public static void main2(String[] args) throws FileNotFoundException {
        // TODO Auto-generated method stub

        // String directory = "./src/main/resources/dataset/" ;
        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);

        dataset.begin(ReadWrite.WRITE);
        try {
            // Model model = dataset.getDefaultModel() ;

            Model model = ModelFactory.createDefaultModel();

            String applicationName = "PrediktorApisServer";
            String deviceID1 = "3244631";
            String deviceID2 = "2999285";

            Resource applicationRsr = model.createResource(OntologyNames.BASE_URL + "ApplicationSystem", RDFS.Class);
            Resource providerRsr = model.createResource(OntologyNames.BASE_URL + "Provider", RDFS.Class)
                    .addProperty(RDFS.subClassOf, applicationRsr);
            Resource consumer = model.createResource(OntologyNames.BASE_URL + "Consumer", RDFS.Class)
                    .addProperty(RDFS.subClassOf, applicationRsr);

            Resource deviceRsr = model.createResource(OntologyNames.BASE_URL + "Device", RDFS.Class);
            Resource sensorUnitRsr = model.createResource(OntologyNames.BASE_URL + "SensorUnit", RDFS.Class)
                    .addProperty(RDFS.subClassOf, deviceRsr);

            Resource serviceRsr = model.createResource(OntologyNames.BASE_URL + "Service", RDFS.Class);

            Resource observationRsr = model.createResource(OntologyNames.SOSA_URL + "Observation", RDFS.Class);

            // Property
            Property hasServiceProp = model.createProperty(OntologyNames.BASE_URL + "hasService");
            Property consumesServiceProp = model.createProperty(OntologyNames.BASE_URL + "consumesService");
            // Property hasTimestampProp = model.createProperty(OntologyNames.BASE_URL + "hasTimestamp");
            Property hasIDProp = model.createProperty(OntologyNames.BASE_URL + "hasID");

            Property madeBySensorProp = model.createProperty(OntologyNames.SOSA_URL + "madeBySensor");
            Property hasSimpleResultProp = model.createProperty(OntologyNames.SOSA_URL + "hasSimpleResult");
            Property resultTimeProp = model.createProperty(OntologyNames.SOSA_URL + "resultTime");

            // Individuals
            Resource Service1Rsr = model.createResource(OntologyNames.BASE_URL + "Service_" + deviceID1, serviceRsr)
                    .addProperty(hasIDProp, deviceID1);

            Resource Device1Rsr = model.createResource(OntologyNames.BASE_URL + "Device_" + deviceID1, sensorUnitRsr)
                    .addProperty(hasIDProp, deviceID1).addProperty(hasServiceProp, Service1Rsr);

            Resource Service2Rsr = model.createResource(OntologyNames.BASE_URL + "Service_" + deviceID2, serviceRsr)
                    .addProperty(hasIDProp, deviceID2);

            Resource Device2Rsr = model.createResource(OntologyNames.BASE_URL + "Device_" + deviceID2, sensorUnitRsr)
                    .addProperty(hasIDProp, deviceID2).addProperty(hasServiceProp, Service2Rsr);

            Resource prediktorAppRsr = model.createResource(OntologyNames.BASE_URL + applicationName, consumer)
                    .addProperty(hasIDProp, applicationName).addProperty(consumesServiceProp, Service1Rsr);

            Resource observation1 = model
                    .createResource(OntologyNames.BASE_URL + "Observation_" + deviceID1, observationRsr)
                    .addProperty(madeBySensorProp, Device1Rsr)
                    .addProperty(resultTimeProp, model.createTypedLiteral(888888))
                    .addProperty(hasSimpleResultProp, model.createTypedLiteral(1.234));

            dataset.addNamedModel(OntologyNames.BASE_URL + "DemoModel", model);

            model.write(new FileOutputStream(new File("./dataset.ttl")), "RDF/XML-ABBREV");

            // list the statements in the Model
            StmtIterator iter = model.listStatements();

            // print out the predicate, subject and object of each statement
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement(); // get next statement
                Resource subject = stmt.getSubject(); // get the subject
                Property predicate = stmt.getPredicate(); // get the predicate
                RDFNode object = stmt.getObject(); // get the object

                System.out.print(subject.toString());
                System.out.print(" " + predicate.toString() + " ");
                if (object instanceof Resource) {
                    System.out.print(object.toString());
                } else {
                    // object is a literal
                    System.out.print(" \"" + object.toString() + "\"");
                }

                System.out.println(" .");
            }

            dataset.commit();
        } finally {
            dataset.end();
        }

    }

}
