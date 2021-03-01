package eu.arrowhead.autonomic.orchestrator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE})
public class OrchestrationRegisterProvider implements ApplicationRunner
{
	//=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
	private final Logger logger = LogManager.getLogger( OrchestrationRegisterProvider.class );
	
    //=================================================================================================
	// assistant methods
    
    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? Constants.INTERFACE_SECURE : Constants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(OrchestrationRegisterProvider.class, args);
    }

    //-------------------------------------------------------------------------------------------------
	  
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
//		final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(Constants.OrchestrationRegisterServiceDefinition)
//				.interfaces(getInterface())
//				.build();
//
//		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
//		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
//								   		 .flag(Flag.TRIGGER_INTER_CLOUD, true)
//								   		 .flag(Flag.OVERRIDE_STORE, true)
//								   		 .flag(Flag.ENABLE_INTER_CLOUD, true)
//								   		 .build();
//		
//		logger.info("Orchestration request for " + Constants.OrchestrationRegisterServiceDefinition + " service:");
//		printOut(orchestrationFormRequest);
//		
//		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
//		
//		logger.info("Orchestration response:");
//		printOut(orchestrationResponse);
//		
//		if (orchestrationResponse == null) {
//			logger.info("No orchestration response received");
//		} else if (orchestrationResponse.getResponse().isEmpty()) {
//			logger.info("No provider found during the orchestration");
//		} else {
//			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
//			//validateOrchestrationResult(orchestrationResult, tellu.no.model.Constants.AUTONOMIC_ORCHESTRATION_SERVICE);
//			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
//		
//		}
	}

//	  protected OrchestrationRegisterProvider() {
//	    //Register the application components the REST library need to know about
//	    Set<Class<?>> classes = new HashSet<>(Arrays.asList(OrchestrationRegisterResource.class));
//	    String[] packages = {"eu.arrowhead.client.common"};
//	    String[] args = {""};
//	    //This (inherited) method reads in the configuration properties, and starts the web server
//	    init(ClientType.PROVIDER, args, classes, packages);
//
//	    //Compile the base of the Service Registry URL
//	    getServiceRegistryUrl();
//	    //Compile the request payload
//	    ServiceRegistryEntry entry = compileRegistrationPayload();
//	    //Send the registration to the Service Registry
//	    registerToServiceRegistry(entry);
//
//	    //Listen for a stop command
//	    listenForInput();
//	  }
//	  
//	  private void getServiceRegistryUrl() {
//	    String srAddress = props.getProperty("sr_address", "0.0.0.0");
//	    int srPort = props.getIntProperty("sr_insecure_port", 8442);
//	    SR_BASE_URI = Utility.getUri(srAddress, srPort, "serviceregistry", false, false);
//	  }
//
//	  private ServiceRegistryEntry compileRegistrationPayload() {
//	    //Compile the ArrowheadService (providedService)
//	    String serviceDef = Constants.OrchestrationRegisterServiceDefinition;
//	    String serviceUri = Constants.OrchestrationRegisterURI;
//	    String interfaceList = "JSON";
//	    Set<String> interfaces = new HashSet<>();
//	    if (interfaceList != null && !interfaceList.isEmpty()) {
//	      //Interfaces are read from a comma separated list
//	      interfaces.addAll(Arrays.asList(interfaceList.replaceAll("\\s+", "").split(",")));
//	    }
//	    Map<String, String> metadata = new HashMap<>();
//	    String metadataString = "Version-1.0";
//	    if (metadataString != null && !metadataString.isEmpty()) {
//	      //Metadata in the properties file: key1-value1, key2-value2, ...
//	      String[] parts = metadataString.split(",");
//	      for (String part : parts) {
//	        String[] pair = part.split("-");
//	        metadata.put(pair[0], pair[1]);
//	      }
//	    }
//	    ArrowheadService service = new ArrowheadService(serviceDef, interfaces, metadata);
//
//	    //Compile the ArrowheadSystem (provider)
//	    URI uri;
//	    try {
//	      uri = new URI(baseUri);
//	    } catch (URISyntaxException e) {
//	      throw new AssertionError("Parsing the BASE_URI resulted in an error.", e);
//	    }
//	    String insecProviderName = Constants.AutonomicOrchestrationName;
//	    ArrowheadSystem provider = new ArrowheadSystem(insecProviderName, uri.getHost(), uri.getPort(), null);
//
//	    //Return the complete request payload
//	    return new ServiceRegistryEntry(service, provider, serviceUri);
//	  }
//
//	  private void registerToServiceRegistry(ServiceRegistryEntry entry) {
//	    //Create the full URL (appending "register" to the base URL)
//	    String registerUri = UriBuilder.fromPath(SR_BASE_URI).path("register").toString();
//
//	    //Send the registration request
//	    try {
//	      Utility.sendRequest(registerUri, "POST", entry);
//	    } catch (ArrowheadException e) {
//	      /*
//	        Service Registry might return duplicate entry exception, if a previous instance of the web server already registered this service,
//	        and the deregistration did not happen. It's better to unregister the old entry, in case the request payload changed.
//	       */
//	      if (e.getExceptionType() == ExceptionType.DUPLICATE_ENTRY) {
//	        System.out.println("Received DuplicateEntryException from SR, sending delete request and then registering again.");
//	        unregisterFromServiceRegistry(entry);
//	        Utility.sendRequest(registerUri, "POST", entry);
//	      } else {
//	        throw e;
//	      }
//	    }
//	    System.out.println("Registering service is successful!");
//	  }
//
//	  private void unregisterFromServiceRegistry(ServiceRegistryEntry entry) {
//	    //Create the full URL (appending "remove" to the base URL)
//	    String removeUri = UriBuilder.fromPath(SR_BASE_URI).path("remove").toString();
//	    Utility.sendRequest(removeUri, "PUT", entry);
//	    System.out.println("Removing service is successful!");
//	  }
}
