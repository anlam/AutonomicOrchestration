package eu.arrowhead.autonomic.orchestrator.manager.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import eu.arrowhead.autonomic.orchestrator.TestConstants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.OntologyNames;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.model.BaseConsumer;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.model.BaseConsumerFactory;
import eu.arrowhead.autonomic.orchestrator.store.OrchestrationStoreEntryDTO;
import eu.arrowhead.autonomic.orchestrator.store.OrchestrationStoreResponseDTO;
//import no.prediktor.apis.demo.consumer.DemoConsumer;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.util.CoreServiceUri;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.core.CoreSystemService;
import eu.arrowhead.common.dto.shared.DataManagerServicesResponseDTO;
import eu.arrowhead.common.dto.shared.DataManagerSystemsResponseDTO;
import eu.arrowhead.common.dto.shared.SenML;

@Service
@Configurable
public class Monitor {

    @Autowired
    private ArrowheadService arrowheadService;

    @Autowired
    protected SSLProperties sslProperties;

    @Value(TestConstants.$MGMT_KEYSTORE_PATH)
    private Resource mgmtKeyStore;

    @Value(TestConstants.$MGMT_KEYSTORE_PASSWORD)
    private String keyStorePassword;

    private CloseableHttpClient httpClient = null;
    private HttpGet httpGet = null;

    private CoreServiceUri dataManagerUri = null;
    private CoreServiceUri orchestrationUri = null;

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
                    // BaseConsumer dummy = BaseConsumerFactory.createBaseConsumer(data[0]);
                    // this.consumers.put(data[1], dummy);
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
            this.consumers.put(consumer.getSystemName(), consumer);
            // saveConsumersToFile();
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
        AddConsumerFromOrchestrationStore();
        InitCurrentTime();
        UpdateCurrentTime();
        RequestDataManager();
    }

    private void RequestDataManager() {
        try {
            if (arrowheadService == null) {
                return;
            }
            if (dataManagerUri == null) {
                dataManagerUri = arrowheadService.getCoreServiceUri(CoreSystemService.PROXY_SERVICE);
            }
            if (dataManagerUri != null) {
                // GET LIST OF SERVICES FIRST
                GetSystemsFromDataManager();
                AddObservationFromDataManager();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void GetSystemsFromDataManager() {
        DataManagerSystemsResponseDTO systemsResponse = arrowheadService.consumeServiceHTTP(
                DataManagerSystemsResponseDTO.class, HttpMethod.GET, dataManagerUri.getAddress(),
                dataManagerUri.getPort(), dataManagerUri.getPath(), getInterface(), null, null, new String[0]);
        for (String system : systemsResponse.getSystems()) {
            String path = String.format("%s/%s", dataManagerUri.getPath(), system);
            DataManagerServicesResponseDTO serviceResponse = arrowheadService.consumeServiceHTTP(
                    DataManagerServicesResponseDTO.class, HttpMethod.GET, dataManagerUri.getAddress(),
                    dataManagerUri.getPort(), path, getInterface(), null, null, new String[0]);
            if (this.consumers.containsKey(system)
                    && (this.consumers.get(system).getServices().equals(serviceResponse.getServices()))) {
                continue;
            }
            // Create new entries
            BaseConsumer dummy = BaseConsumerFactory.createBaseConsumer(system, serviceResponse.getServices());
            this.consumers.put(system, dummy);

            // for (String service : serviceResponse.getServices()) {
            // KnowledgeBase.getInstance().AddService(service, system, service);
            // }
        }
    }

    private void UpdateCurrentTime() {
        String updateCurentTimeQuery = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                + "prefix xsd: <" + XSD.getURI() + ">\n" + "delete { :DateTimeNow :hasValue ?Value} \n"
                + "insert { :DateTimeNow :hasValue \"" + System.currentTimeMillis() + "\"^^xsd:long } \n"
                + "where {  :DateTimeNow :hasValue ?Value . \n" + "}";

        List<String> queries = new ArrayList<String>();
        queries.add(updateCurentTimeQuery);

        KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);

    }

    private void InitCurrentTime() {
        String updateCurentTimeQuery = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                + "prefix xsd: <" + XSD.getURI() + ">\n" + "insert { :DateTimeNow :hasValue \""
                + System.currentTimeMillis() + "\"^^xsd:long } \n"
                + "where {  minus { :DateTimeNow :hasValue ?Value . \n" + "} }";

        List<String> queries = new ArrayList<String>();
        queries.add(updateCurentTimeQuery);

        KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);

    }

    private void AddObservationFromDataManager() {
        for (Entry<String, BaseConsumer> entry : consumers.entrySet()) {
            for (String service : entry.getValue().getServices()) {
                String path = String.format("%s/%s/%s", dataManagerUri.getPath(), entry.getValue().getSystemName(),
                        service);
                final String[] queryParamDataManager = new String[0];
                SenML[] response = arrowheadService.consumeServiceHTTP(SenML[].class, HttpMethod.GET,
                        dataManagerUri.getAddress(), dataManagerUri.getPort(), path, getInterface(), null, null,
                        queryParamDataManager);
                if (response[0].getVs() != null) {
                    long time = 0;
                    if (response[0].getT() != null) {
                        time = response[0].getT().longValue();
                    }
                    KnowledgeBase.getInstance().AddSensor(response[0].getBn(), "Service_" + service, "unknown",
                            entry.getValue().getSystemName(), service);
                    AddObservation("Observation_" + response[0].getBn(), response[0].getBn(), time,
                            Double.toString(response[0].getV()), response[0].getN(), response[0].getU(), "double");
                }
            }
        }
    }

    private void InitStoreClient() {
        if (orchestrationUri == null) {
            CoreServiceUri orcUri = arrowheadService.getCoreServiceUri(CoreSystemService.ORCHESTRATION_SERVICE);
            orchestrationUri = new CoreServiceUri(orcUri.getAddress(), orcUri.getPort(), "/orchestrator/mgmt/store");
            try {
                SSLContext sslContext = SSLContexts.custom()
                        .loadKeyMaterial(mgmtKeyStore.getFile(), keyStorePassword.toCharArray(),
                                keyStorePassword.toCharArray())
                        .loadTrustMaterial(sslProperties.getTrustStore().getFile(),
                                sslProperties.getTrustStorePassword().toCharArray())
                        .build();

                SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslContext,
                        new NoopHostnameVerifier());

                httpClient = HttpClients.custom().setSSLSocketFactory(sslConSocFactory).setSSLContext(sslContext)
                        .build();

                URIBuilder builder = new URIBuilder();
                builder.setScheme("https");
                builder.setHost(orchestrationUri.getAddress());
                builder.setPath(orchestrationUri.getPath());
                builder.setPort(orchestrationUri.getPort());
                String url = builder.build().toString();

                httpGet = new HttpGet(url);
                Header header = new BasicHeader("Content-Type", "application/json");

                httpGet.setHeader(header);
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    private void AddConsumerFromOrchestrationStore() {
        InitStoreClient();
        if (httpClient != null) {
            try {
                HttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    OrchestrationStoreResponseDTO storeEntryList = objectMapper
                            .readValue(response.getEntity().getContent(), OrchestrationStoreResponseDTO.class);
                    for (OrchestrationStoreEntryDTO entry : storeEntryList.getData()) {
                        KnowledgeBase.getInstance().AddConsumer(entry.getConsumerSystem().getSystemName(),
                                entry.getServiceDefinition().getServiceDefinition(),
                                entry.getProviderSystem().getSystemName());
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

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
                Iterator<String> servicesIterator = entry.getValue().getServices().iterator();
                while (servicesIterator.hasNext()) {
                    csvWriter.append(entry.getValue().getSystemName());
                    csvWriter.append(",");
                    csvWriter.append(servicesIterator.next());
                    csvWriter.append("\n");
                }
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {

        }
    }
}
