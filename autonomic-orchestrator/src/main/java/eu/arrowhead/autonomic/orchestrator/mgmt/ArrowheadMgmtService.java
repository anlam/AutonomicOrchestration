package eu.arrowhead.autonomic.orchestrator.mgmt;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.util.CoreServiceUri;
import eu.arrowhead.autonomic.orchestrator.store.OrchestrationStoreRequestDTO;
import eu.arrowhead.autonomic.orchestrator.store.OrchestrationStoreResponseDTO;
import eu.arrowhead.autonomic.orchestrator.store.ServiceRegistryEntryListDTO;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.core.CoreSystemService;

@Component("ArrowheadMgmtService")
public class ArrowheadMgmtService {

    @Autowired
    private ArrowheadService arrowheadService;

    @Autowired
    protected SSLProperties sslProperties;

    @Value(MgmtConstants.$MGMT_KEYSTORE_PATH)
    private Resource mgmtKeyStore;

    @Value(MgmtConstants.$MGMT_KEYSTORE_PASSWORD)
    private String keyStorePassword;

    @Value(CommonConstants.$SERVICEREGISTRY_ADDRESS_WD)
    private String serviceReqistryAddress;

    @Value(CommonConstants.$SERVICEREGISTRY_PORT_WD)
    private int serviceRegistryPort;

    private CloseableHttpClient httpClient = null;
    private HttpGet orchestrationStoreGetHttp = null;
    private HttpPost orchestrationStorePostHttp = null;
    private HttpDelete orchestrationStoreDeleteHttp = null;

    private HttpGet serviceRegistryServiceDefinitionGetHttp = null;

    public void InitMgmt() {
        InitMgmtClient();
        InitOrchestrationStoreClient();
    }

    private void InitMgmtClient() {
        if (httpClient == null) {
            try {
                SSLContext sslContext = SSLContexts.custom()
                        .loadKeyMaterial(mgmtKeyStore.getFile(), keyStorePassword.toCharArray(),
                                keyStorePassword.toCharArray())
                        .loadTrustMaterial(sslProperties.getTrustStore().getFile(),
                                sslProperties.getTrustStorePassword().toCharArray())
                        .build();

                SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslContext,
                        new NoopHostnameVerifier());

                httpClient = HttpClients.custom().setSSLSocketFactory(sslConSocFactory).setSSLContext(sslContext)
                        .build();
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    public ServiceRegistryEntryListDTO GetInforFromServiceDefinition(String serviceDefinition) {
        if (serviceRegistryServiceDefinitionGetHttp == null) {
            String serviceRegistryPath = "/serviceregistry/mgmt/servicedef/" + serviceDefinition;
            try {
                URIBuilder builder = new URIBuilder();
                builder.setScheme("https");
                builder.setHost(serviceReqistryAddress);
                builder.setPort(serviceRegistryPort);
                builder.setPath(serviceRegistryPath);
                String url = builder.build().toString();

                Header header = new BasicHeader("Content-Type", "application/json");

                serviceRegistryServiceDefinitionGetHttp = new HttpGet(url);
                serviceRegistryServiceDefinitionGetHttp.setHeader(header);

                HttpResponse response = httpClient.execute(serviceRegistryServiceDefinitionGetHttp);
                if (response.getStatusLine().getStatusCode() == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ServiceRegistryEntryListDTO entryList = objectMapper.readValue(response.getEntity().getContent(),
                            ServiceRegistryEntryListDTO.class);
                    return entryList;
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return null;
    }

    private void InitOrchestrationStoreClient() {
        if (orchestrationStoreGetHttp == null || orchestrationStorePostHttp == null) {
            CoreServiceUri orcUri = arrowheadService.getCoreServiceUri(CoreSystemService.ORCHESTRATION_SERVICE);
            String orchestrationStorePath = "/orchestrator/mgmt/store";
            try {
                URIBuilder builder = new URIBuilder();
                builder.setScheme("https");
                builder.setHost(orcUri.getAddress());
                builder.setPort(orcUri.getPort());
                builder.setPath(orchestrationStorePath);
                String url = builder.build().toString();

                Header header = new BasicHeader("Content-Type", "application/json");

                orchestrationStoreGetHttp = new HttpGet(url);
                orchestrationStoreGetHttp.setHeader(header);

                orchestrationStorePostHttp = new HttpPost(url);
                orchestrationStorePostHttp.setHeader(header);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public OrchestrationStoreResponseDTO getAllStoreEntry() {
        if (httpClient != null && orchestrationStoreGetHttp != null) {
            try {
                HttpResponse response = httpClient.execute(orchestrationStoreGetHttp);
                if (response.getStatusLine().getStatusCode() == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    OrchestrationStoreResponseDTO storeEntryList = objectMapper
                            .readValue(response.getEntity().getContent(), OrchestrationStoreResponseDTO.class);
                    return storeEntryList;
                }
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
        }
        return null;
    }

    public boolean addOrchestrationStoreEntry(OrchestrationStoreRequestDTO payload) {
        if (httpClient != null && orchestrationStorePostHttp != null) {
            try {

                OrchestrationStoreRequestDTO[] entries = { payload };
                Gson gson = new Gson();
                String jsonInString = gson.toJson(entries);
                orchestrationStorePostHttp.setEntity(new StringEntity(jsonInString));
                HttpResponse response = httpClient.execute(orchestrationStorePostHttp);
                if (response.getStatusLine().getStatusCode() == 200) {
                    // DO STH
                    return true;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }

    public boolean deleteOrchestrationStoreEntry(String entryId) {
        if (httpClient != null) {
            try {
                if (orchestrationStoreDeleteHttp == null) {
                    CoreServiceUri orcUri = arrowheadService.getCoreServiceUri(CoreSystemService.ORCHESTRATION_SERVICE);
                    String orchestrationStorePath = "/orchestrator/mgmt/store/" + entryId;
                    URIBuilder builder = new URIBuilder();
                    builder.setScheme("https");
                    builder.setHost(orcUri.getAddress());
                    builder.setPort(orcUri.getPort());
                    builder.setPath(orchestrationStorePath);
                    String url = builder.build().toString();
                    orchestrationStoreDeleteHttp = new HttpDelete(url);
                }

                HttpResponse response = httpClient.execute(orchestrationStoreDeleteHttp);
                if (response.getStatusLine().getStatusCode() == 200) {
                    // DO STH
                    return true;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }
}
