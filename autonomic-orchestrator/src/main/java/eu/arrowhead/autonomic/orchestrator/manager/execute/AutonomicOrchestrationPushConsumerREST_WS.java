package eu.arrowhead.autonomic.orchestrator.manager.execute;

import javax.ws.rs.core.Response;

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

		String updateURL = this.orchPushURL + "push";
		Gson gson = new Gson();
		String sValue = gson.toJson(plan);
		AdaptationPlan response = null;

		try {
			Response getResponse = Utility.sendRequest(updateURL, "PUT", sValue);

			if (getResponse.getStatus() != 200) {
				response = gson.fromJson(getResponse.readEntity(String.class), AdaptationPlan.class);
			}

		} catch (Exception e) {
			System.err.println("Cannot send orchestrion to consumer: " + name);
			//e.printStackTrace();
		}

		return response;
	}

}
