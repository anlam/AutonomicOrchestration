package eu.arrowhead.client.skeleton.provider;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.config.ApplicationInitListener;
import ai.aitia.arrowhead.application.library.util.ApplicationCommonConstants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.mgmt.ArrowheadMgmtService;
import eu.arrowhead.client.skeleton.provider.security.ProviderSecurityConfig;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;

@Component
public class ProviderApplicationInitListener extends ApplicationInitListener {

    // =================================================================================================
    // members

    @Autowired
    private ArrowheadService arrowheadService;

    @Autowired
    private ArrowheadMgmtService arrowheadMgmtService;

    @Autowired
    private ProviderSecurityConfig providerSecurityConfig;

    @Value(ApplicationCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
    private boolean tokenSecurityFilterEnabled;

    @Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
    private boolean sslEnabled;

    @Value(ApplicationCommonConstants.$APPLICATION_SYSTEM_NAME)
    private String mySystemName;

    @Value(ApplicationCommonConstants.$APPLICATION_SERVER_ADDRESS_WD)
    private String mySystemAddress;

    @Value(ApplicationCommonConstants.$APPLICATION_SERVER_PORT_WD)
    private int mySystemPort;

    private final Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);

    // =================================================================================================
    // methods

    // -------------------------------------------------------------------------------------------------
    @Override
    protected void customInit(final ContextRefreshedEvent event) {
        checkConfiguration();

        // Checking the availability of necessary core systems
        checkCoreSystemReachability(CoreSystem.SERVICEREGISTRY);
        // Initialize Arrowhead Context
        arrowheadService.updateCoreServiceURIs(CoreSystem.ORCHESTRATOR);
        arrowheadService.updateCoreServiceURIs(CoreSystem.DATAMANAGER);

        if (sslEnabled && tokenSecurityFilterEnabled) {
            checkCoreSystemReachability(CoreSystem.AUTHORIZATION);

            // Initialize Arrowhead Context
            arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);

            setTokenSecurityFilter();
        } else {
            logger.info("TokenSecurityFilter in not active");
        }

        // Register services into ServiceRegistry
        // Register each APIs
        // @Get all rules
        final ServiceRegistryRequestDTO serviceRegistryRequest_1 = createServiceRegistryRequest(
                Constants.OrchestrationGetAllRulesDefinition, Constants.OrchestrationGetAllRulesURI, HttpMethod.GET);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_1);

        // @Get all rules 2
        final ServiceRegistryRequestDTO serviceRegistryRequest_2 = createServiceRegistryRequest(
                Constants.OrchestrationGetAllRules2Definition, Constants.OrchestrationGetAllRules2URI, HttpMethod.GET);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_2);

        // @Get all queries
        final ServiceRegistryRequestDTO serviceRegistryRequest_3 = createServiceRegistryRequest(
                Constants.OrchestrationGetAllQueriesDefinition, Constants.OrchestrationGetAllQueriesURI,
                HttpMethod.GET);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_3);
        
     // @Get all queries
        final ServiceRegistryRequestDTO serviceRegistryRequest_31 = createServiceRegistryRequest(
                Constants.OrchestrationEditQueryDefinition, Constants.OrchestrationEditQueryURI,
                HttpMethod.POST);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_31);
        
     // @Get all queries
        final ServiceRegistryRequestDTO serviceRegistryRequest_32 = createServiceRegistryRequest(
                Constants.OrchestrationDeleteQueryDefinition, Constants.OrchestrationDeleteQueryURI,
                HttpMethod.DELETE);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_32);

        // @Get all knowledge
        final ServiceRegistryRequestDTO serviceRegistryRequest_4 = createServiceRegistryRequest(
                Constants.OrchestrationGetAllKnowledgeDefinition, Constants.OrchestrationGetAllKnowledgeURI,
                HttpMethod.GET);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_4);

        // @Post register
        final ServiceRegistryRequestDTO serviceRegistryRequest_5 = createServiceRegistryRequest(
                Constants.OrchestrationRegisterDefinition, Constants.OrchestrationRegisterURI, HttpMethod.POST);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_5);

        // @Delete register
        final ServiceRegistryRequestDTO serviceRegistryRequest_6 = createServiceRegistryRequest(
                Constants.OrchestrationDeleteDefinition, Constants.OrchestrationDeleteURI, HttpMethod.DELETE);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_6);

        // @Put Orchestration Response
        final ServiceRegistryRequestDTO serviceRegistryRequest_7 = createServiceRegistryRequest(
                Constants.OrchestrationPushDefinition, Constants.OrchestrationPushURI, HttpMethod.PUT);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_7);

        // @Get Orchestration Response
        final ServiceRegistryRequestDTO serviceRegistryRequest_8 = createServiceRegistryRequest(
                Constants.OrchestrationGetDefinition, Constants.OrchestrationGetURI, HttpMethod.GET);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_8);

        // @Get All Consumers Response
        final ServiceRegistryRequestDTO serviceRegistryRequest_9 = createServiceRegistryRequest(
                Constants.OrchestrationGetAllConsumersDefinition, Constants.OrchestrationGetAllConsumersURI,
                HttpMethod.GET);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_9);

        // @Get Orchestration Response
        final ServiceRegistryRequestDTO serviceRegistryRequest_10 = createServiceRegistryRequest(
                Constants.OrchestrationServiceRegisterDefinition, Constants.OrchestrationServiceRegisterURI,
                HttpMethod.POST);
        arrowheadService.forceRegisterServiceToServiceRegistry(serviceRegistryRequest_10);

        // MGMT
        arrowheadMgmtService.InitMgmt();
    }

    // -------------------------------------------------------------------------------------------------
    @Override
    public void customDestroy() {
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationGetAllRulesDefinition,
                Constants.OrchestrationGetAllRulesURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationGetAllRules2Definition,
                Constants.OrchestrationGetAllRules2URI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationGetAllQueriesDefinition,
                Constants.OrchestrationGetAllQueriesURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationEditQueryDefinition,
                Constants.OrchestrationEditQueryURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationDeleteQueryDefinition,
                Constants.OrchestrationDeleteQueryURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationGetAllKnowledgeDefinition,
                Constants.OrchestrationGetAllKnowledgeURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationRegisterDefinition,
                Constants.OrchestrationRegisterURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationDeleteDefinition,
                Constants.OrchestrationDeleteURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationPushDefinition,
                Constants.OrchestrationPushURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationGetDefinition,
                Constants.OrchestrationGetURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationGetAllConsumersDefinition,
                Constants.OrchestrationGetAllConsumersURI);
        arrowheadService.unregisterServiceFromServiceRegistry(Constants.OrchestrationServiceRegisterDefinition,
                Constants.OrchestrationServiceRegisterURI);
    }

    // =================================================================================================
    // assistant methods

    // -------------------------------------------------------------------------------------------------
    private void checkConfiguration() {
        if (!sslEnabled && tokenSecurityFilterEnabled) {
            logger.warn("Contradictory configuration:");
            logger.warn("token.security.filter.enabled=true while server.ssl.enabled=false");
        }
    }

    private void setTokenSecurityFilter() {
        final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();
        if (authorizationPublicKey == null) {
            throw new ArrowheadException("Authorization public key is null");
        }

        KeyStore keystore;
        try {
            keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
            keystore.load(sslProperties.getKeyStore().getInputStream(),
                    sslProperties.getKeyStorePassword().toCharArray());
        } catch (final KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
            throw new ArrowheadException(ex.getMessage());
        }
        final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());

        providerSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
        providerSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);
    }

    // -------------------------------------------------------------------------------------------------
    private ServiceRegistryRequestDTO createServiceRegistryRequest(final String serviceDefinition,
            final String serviceUri, final HttpMethod httpMethod) {
        final ServiceRegistryRequestDTO serviceRegistryRequest = new ServiceRegistryRequestDTO();
        serviceRegistryRequest.setServiceDefinition(serviceDefinition);
        final SystemRequestDTO systemRequest = new SystemRequestDTO();
        systemRequest.setSystemName(mySystemName);
        systemRequest.setAddress(mySystemAddress);
        systemRequest.setPort(mySystemPort);

        if (sslEnabled && tokenSecurityFilterEnabled) {
            systemRequest.setAuthenticationInfo(
                    Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
            serviceRegistryRequest.setSecure(ServiceSecurityType.TOKEN.name());
            serviceRegistryRequest.setInterfaces(List.of(Constants.INTERFACE_SECURE));
        } else if (sslEnabled) {
            systemRequest.setAuthenticationInfo(
                    Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
            serviceRegistryRequest.setSecure(ServiceSecurityType.CERTIFICATE.name());
            serviceRegistryRequest.setInterfaces(List.of(Constants.INTERFACE_SECURE));
        } else {
            serviceRegistryRequest.setSecure(ServiceSecurityType.NOT_SECURE.name());
            serviceRegistryRequest.setInterfaces(List.of(Constants.INTERFACE_INSECURE));
        }
        serviceRegistryRequest.setProviderSystem(systemRequest);
        serviceRegistryRequest.setServiceUri(serviceUri);
        serviceRegistryRequest.setMetadata(new HashMap<>());
        serviceRegistryRequest.getMetadata().put(Constants.HTTP_METHOD, httpMethod.name());
        return serviceRegistryRequest;
    }
}
