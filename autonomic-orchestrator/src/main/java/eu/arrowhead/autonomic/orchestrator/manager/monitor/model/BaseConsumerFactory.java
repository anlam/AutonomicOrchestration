package eu.arrowhead.autonomic.orchestrator.manager.monitor.model;

public class BaseConsumerFactory {
    public static BaseConsumer createBaseConsumer(String system, String service, String endpoint) {
        BaseConsumer instance = new BaseConsumer();
        instance.setSystemName(system);
        instance.setServiceName(service);
        instance.setServiceEndpoint(endpoint);
        return instance;
    }
}
