package eu.arrowhead.autonomic.orchestrator.push;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.http.HttpService;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.CommonConstants;

public class AutonomicOrchestrationPushConsumerREST_WS implements AutonomicOrchestrationPushService{

	private String orchPushURL;
	private int orchPushPort;
	private String orchPushPath;
	private String name;
	
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
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
	
	//-------------------------------------------------------------------------------------------------
	private String getUriScheme() {
		return sslProperties.isSslEnabled() ? CommonConstants.HTTPS : CommonConstants.HTTP;
	}
	
	public AdaptationPlan sendApdationPlan(AdaptationPlan plan) {

		String updateURL = this.orchPushURL;
		Gson gson = new Gson();
		String sValue = gson.toJson(plan);
		AdaptationPlan response = null;

		try {
			System.out.println("AutonomicOrchestrationPushConsumerREST_WS Sending apdatation to system: " + name + " with endpoint: " + updateURL);
			System.out.println(sValue);
			final ResponseEntity<AdaptationPlan> getResponse = httpService.sendRequest(Utilities.createURI(getUriScheme(), this.orchPushURL, 
																				this.orchPushPort, this.orchPushPath), HttpMethod.PUT, AdaptationPlan.class, sValue);

			int status = getResponse.getStatusCodeValue();
			int a = status;
			if (getResponse.getStatusCodeValue() == 200) {
				response = getResponse.getBody();
			}

		} catch (Exception e) {
			System.err.println("AutonomicOrchestrationPushConsumerREST_WS Cannot send orchestrion to consumer: " + name + "Reason: " + e.getMessage());
			e.printStackTrace();
		}

		return response;
	}

}
