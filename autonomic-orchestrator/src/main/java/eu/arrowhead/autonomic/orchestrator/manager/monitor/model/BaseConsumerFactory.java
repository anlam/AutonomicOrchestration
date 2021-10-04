package eu.arrowhead.autonomic.orchestrator.manager.monitor.model;

import java.util.List;

public class BaseConsumerFactory {
    public static BaseConsumer createBaseConsumer(String system, List<String> services) {
        BaseConsumer instance = new BaseConsumer();
        instance.setSystemName(system);
        instance.setServices(services);
        return instance;
    }
}
