package eu.arrowhead.autonomic.orchestrator.push.demo;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;

import javax.ws.rs.core.SecurityContext;

import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;
import eu.arrowhead.client.common.model.MeasurementEntry;
import eu.arrowhead.client.common.model.TemperatureReadout;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
//REST service example
public class OrchestrationPushResource {

  static final String SERVICE_URI = "temperature";

  @GET
  @Path(SERVICE_URI)
  public Response getIt(@Context SecurityContext context, @QueryParam("token") String token, @QueryParam("signature") String signature) {
    String providerName = "TemperatureSensors_InsecureTemperatureSensor";
   
      MeasurementEntry entry = new MeasurementEntry("Temperature_IndoorTemperature", 21.0, System.currentTimeMillis());
      TemperatureReadout readout = new TemperatureReadout(providerName, System.currentTimeMillis(), "celsius", 1);
      readout.getE().add(entry);
      return Response.status(200).entity(readout).build();
    
  }
  
	/*
	 * PUT requests are usually for updating existing resources. The ID is from the
	 * database, to identify the car instance. Usually PUT requests fully update a
	 * resource, meaning fields which are not specified by the client, will also be
	 * null in the database (overriding existing data). PATCH requests are used for
	 * partial updates.
	 */
	@PUT
	@Path("orchestration/auto")
	public Response updateCar(AdaptationPlan adaptation) {
		/*
		 * Car carFromTheDatabase = cars.get(id); // Throw an exception if the car with
		 * the specified ID does not exist if (carFromTheDatabase != null) { throw new
		 * DataNotFoundException("Car with id " + id + " not found in the database!"); }
		 * // Update the car cars.put(id, updatedCar);
		 * 
		 * // Return a response with Accepted status code
		 */		
		System.out.println("Receive: " + adaptation);
		adaptation.getAdaptations().get(0).setStatus(PlanStatus.EXECUTED);
		
		Gson gson = new Gson();
		String sValue = gson.toJson(adaptation);
		System.out.println("Sending response: ");
		System.out.println(sValue);
		
		return Response.status(Status.OK).entity(sValue).build();
	}

}

