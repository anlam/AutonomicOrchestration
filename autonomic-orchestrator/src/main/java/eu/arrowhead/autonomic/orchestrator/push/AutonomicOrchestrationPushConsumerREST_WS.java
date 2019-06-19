package eu.arrowhead.autonomic.orchestrator.push;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.client.common.Utility;

public class AutonomicOrchestrationPushConsumerREST_WS implements AutonomicOrchestrationPushService{

	private String orchPushURL;
	private String name;
	
	public AutonomicOrchestrationPushConsumerREST_WS(String name, String url) {
		this.orchPushURL = url;
		this.name = name;
	}
	
	@Override
	public AdaptationPlan sendApdationPlan(AdaptationPlan plan) {

		String updateURL = this.orchPushURL;
		Gson gson = new Gson();
		String sValue = gson.toJson(plan);
		AdaptationPlan response = null;

		try {
			System.out.println("AutonomicOrchestrationPushConsumerREST_WS Sending apdatation to system: " + name + " with endpoint: " + updateURL);
			System.out.println(sValue);
			Response getResponse = Utility.sendRequest(updateURL, "PUT", sValue);

			if (getResponse.getStatus() != 200) {
				response = new  ObjectMapper().readValue(getResponse.readEntity(String.class),  AdaptationPlan.class);
				//response = gson.fromJson(getResponse.readEntity(String.class), AdaptationPlan.class);
			}

		} catch (Exception e) {
			System.err.println("AutonomicOrchestrationPushConsumerREST_WS Cannot send orchestrion to consumer: " + name + "Reason: " + e.getMessage());
			e.printStackTrace();
		}

		return response;
	}

}
