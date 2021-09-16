package eu.arrowhead.autonomic.orchestrator.manager.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DummyConsumer extends BaseConsumerWorker {

    public DummyConsumer(String name, String endpoint) {
        super(name, endpoint);
        // TODO Auto-generated constructor stub

        // Prints the working directory for extra information. Working directory should always contain a config folder
        // with the app.conf file!
        log.debug("Working directory: " + System.getProperty("user.dir"));
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        // Connect to the provider, consuming its service - THIS METHOD SHOULD BE MODIFIED ACCORDING TO YOUR USE CASE
        this.serviceEndpoint = endpoint;
        this.serviceName = name;
        this.DeviceID = name;
        // consumeService(providerUrl);
    }

    @Override
    public void consumeService() {
        // TODO Auto-generated method stub
        if (serviceEndpoint == null) {
            log.debug("No provider found.");
            System.out.print("No provider found.");
        }

        else if (monitor == null) {
            log.debug("No monitor component.");
            System.out.print("No monitor component.");
        }

        else {

        }
    }

    private static boolean isSecure;
    private static String orchestratorUrl;

    private String DeviceID;

    private static final Logger log = LogManager.getLogger(DummyConsumer.class);

}
