package eu.arrowhead.autonomic.orchestrator.push;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.mgmt.ArrowheadMgmtService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.http.HttpService;

public class AutonomicOrchestrationPushConsumerREST_WS implements AutonomicOrchestrationPushService {

    private String orchPushURL;
    private int orchPushPort;
    private String orchPushPath;
    private String name;

    // =================================================================================================
    // members

    @Autowired
    private ArrowheadService arrowheadService;

    @Autowired
    private ArrowheadMgmtService arrowheadMgmtService;

    @Autowired
    protected SSLProperties sslProperties;

    @Autowired
    private HttpService httpService;

    public AutonomicOrchestrationPushConsumerREST_WS(String name, String url, int port, String path) {
        this.orchPushURL = url;
        this.orchPushPort = port;
        this.orchPushPath = path;
        this.name = name;
    }

    // -------------------------------------------------------------------------------------------------
    private String getUriScheme() {
        return sslProperties.isSslEnabled() ? CommonConstants.HTTPS : CommonConstants.HTTP;
    }

    // private List buildStoreRulesPayload(AdaptationPlan plan) {
    // List payload = new List();
    // if (plan.get) {
    // return payload;
    // }
    // }

    @Override
    public AdaptationPlan sendApdationPlan(AdaptationPlan plan) {

        String updateURL = this.orchPushURL;
        Gson gson = new Gson();
        String sValue = gson.toJson(plan);
        AdaptationPlan response = null;

        try {
            // arrowheadMgmtService.addOrchestrationStoreEntry(null);
            // System.out.println("AutonomicOrchestrationPushConsumerREST_WS Sending apdatation to system: " + name
            // + " with endpoint: " + updateURL);
            // System.out.println(sValue);
            // final ResponseEntity<AdaptationPlan> getResponse = httpService.sendRequest(
            // Utilities.createURI(getUriScheme(), this.orchPushURL, this.orchPushPort, this.orchPushPath),
            // HttpMethod.PUT, AdaptationPlan.class, sValue);
            //
            // int status = getResponse.getStatusCodeValue();
            // int a = status;
            // if (getResponse.getStatusCodeValue() == 200) {
            // response = getResponse.getBody();
            // }

        } catch (Exception e) {
            System.err.println("AutonomicOrchestrationPushConsumerREST_WS Cannot send orchestrion to consumer: " + name
                    + "Reason: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

}
