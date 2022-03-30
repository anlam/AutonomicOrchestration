package eu.arrowhead.autonomic.orchestrator.manager.knowledge;

import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

public class Constants {
    public static final String datasetDir = "./src/main/resources/dataset/";

    public static final String ModelName = "DemoModel";

    public static final String analysisQueriesDir = "./src/main/resources/queries/analysis/";

    public static final String planQueriesDir = "./src/main/resources/queries/plan/";

    public static final String knowledgeBaseFileName = "./dataset.ttl";

    public static final long MonitorWorkerInterval = 1000;
    public static final long KnowledgeBaseWorkerInterval = 2000;
    public static final long AnalysisWorkerInterval = 1000;
    public static final long PlanWorkerInterval = 1000;
    public static final long ExecuteWorkerInterval = 1000;
    public static final long DataManagerClientWorkerInterval = 1000;

    public static final String AutonomicOrchestrationName = "AutonomicOrchestrationSystem";
    public static final int OrchestrationRegisterPort = 8461;

    public static final String OrchestrationGetAllRulesDefinition = "auto-orchestration-get-all-rules";
    public static final String OrchestrationGetAllRulesURI = "auto/orchestration";

    public static final String OrchestrationGetAllRules2Definition = "auto-orchestration-get-all-rules-2";
    public static final String OrchestrationGetAllRules2URI = "auto/orchestration/rules";

    public static final String OrchestrationGetAllQueriesDefinition = "auto-orchestration-get-all-queries";
    public static final String OrchestrationGetAllQueriesURI = "auto/orchestration/queries";

    public static final String OrchestrationGetAllKnowledgeDefinition = "auto-orchestration-get-knowledge";
    public static final String OrchestrationGetAllKnowledgeURI = "auto/orchestration/knowledge";

    public static final String OrchestrationRegisterDefinition = "auto-orchestration-register";
    public static final String OrchestrationRegisterURI = "auto/orchestration/register";

    public static final String OrchestrationDeleteDefinition = "auto-orchestration-delete";
    public static final String OrchestrationDeleteURI = "auto/orchestration/delete";

    public static final String OrchestrationPushDefinition = "auto-orchestration-push";
    public static final String OrchestrationPushURI = "auto/orchestration/push";

    public static final String OrchestrationGetDefinition = "auto-orchestration-get-adapations";
    public static final String OrchestrationGetURI = "auto/orchestration/getSentAdapations";

    public static final String OrchestrationGetAllConsumersDefinition = "auto-orchestration-get-all-consumers";
    public static final String OrchestrationGetAllConsumersURI = "auto/orchestration/consumers";

    public static final String OrchestrationServiceRegisterDefinition = "auto-orchestration-service-register";
    public static final String OrchestrationServiceRegisterURI = "auto/orchestration/serviceregister";

    public static final int SubstitutionParameterSize = 4;
    public static final int ConfigureParameterSize = 4;

    public static final String INTERFACE_SECURE = "HTTPS-SECURE-JSON";
    public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
    public static final String HTTP_METHOD = "http-method";

    // @formatter:off
    public static final String PREFIX_STRING = "prefix : <" + OntologyNames.BASE_URL + ">\n"
                                        + "prefix sai: <" + OntologyNames.SAI_URL + ">\n"
                                        + "prefix rdfs: <" + RDFS.getURI() + ">\n"
                                        + "prefix rdf: <" + RDF.getURI() + ">\n"
                                        + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                                        + "prefix xsd: <" + XSD.getURI() + ">\n"
                                        + "prefix DOGONT: <" + OntologyNames.DOGONT_URL + ">\n";
}
