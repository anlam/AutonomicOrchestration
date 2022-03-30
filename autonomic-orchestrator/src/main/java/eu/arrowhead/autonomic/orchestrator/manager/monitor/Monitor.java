package eu.arrowhead.autonomic.orchestrator.manager.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.util.CoreServiceUri;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.model.BaseConsumer;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.model.BaseConsumerFactory;
import eu.arrowhead.autonomic.orchestrator.mgmt.ArrowheadMgmtService;
import eu.arrowhead.autonomic.orchestrator.store.OrchestrationStoreEntryDTO;
import eu.arrowhead.autonomic.orchestrator.store.OrchestrationStoreResponseDTO;
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
    private ArrowheadMgmtService arrowheadMgmtService;

    @Autowired
    protected SSLProperties sslProperties;

    private CoreServiceUri dataManagerUri = null;

    // private ReentrantLock lock;
    private TreeMap<String, BaseConsumer> consumers;

    private static final Logger log = LoggerFactory.getLogger(Monitor.class);

    private String getInterface() {
        return sslProperties.isSslEnabled() ? Constants.INTERFACE_SECURE : Constants.INTERFACE_INSECURE;
    }

    public Monitor() {

        // lock = new ReentrantLock();
        consumers = new TreeMap<String, BaseConsumer>();
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

    // @formatter:off
    private void UpdateCurrentTime() {
        String updateCurentTimeQuery = Constants.PREFIX_STRING
                + "delete { :DateTimeNow :hasValue ?Value} \n"
                + "insert { :DateTimeNow :hasValue \"" + System.currentTimeMillis() + "\"^^xsd:long } \n"
                + "where {  :DateTimeNow :hasValue ?Value . \n" + "}";

        List<String> queries = new ArrayList<String>();
        queries.add(updateCurentTimeQuery);

        KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);

    }

    // @formatter:off
    private void InitCurrentTime() {
        String updateCurentTimeQuery = Constants.PREFIX_STRING
                + "insert { :DateTimeNow :hasValue \""
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
                    KnowledgeBase.getInstance().AddSensor(response[0].getBn(), service, "unknown",
                            entry.getValue().getSystemName(), service);
                    AddObservation("Observation_" + response[0].getBn(), response[0].getBn(), time,
                            Double.toString(response[0].getV()), response[0].getN(), response[0].getU(), "double");
                }
            }
        }
    }

    private void AddConsumerFromOrchestrationStore() {
        OrchestrationStoreResponseDTO storeEntryList = arrowheadMgmtService.getAllStoreEntry();
        if (storeEntryList != null) {
            for (OrchestrationStoreEntryDTO entry : storeEntryList.getData()) {
                KnowledgeBase.getInstance().AddOrchestrationStoreEntry(entry);
                KnowledgeBase.getInstance().AddConsumer(entry.getConsumerSystem(),
                        entry.getServiceDefinition().getServiceDefinition(),
                        entry.getProviderSystem().getSystemName());
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
}
