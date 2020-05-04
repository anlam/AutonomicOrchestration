package eu.arrowhead.autonomic.orchestrator.manager.knowledge;

public class Constants {
	public static final String datasetDir = "./src/main/resources/dataset/";
	
	public static final String ModelName = "DemoModel";
	
	public static final String analysisQueriesDir = "./src/main/resources/queries/analysis/";
	
	public static final String planQueriesDir = "./src/main/resources/queries/plan/";
	
	public static final String knowledgeBaseFileName = "./dataset.txt";
	
	public static final long MonitorWorkerInterval = 1000;
	public static final long KnowledgeBaseWorkerInterval = 2000;
	public static final long AnalysisWorkerInterval = 1000;
	public static final long PlanWorkerInterval = 1000;
	public static final long ExecuteWorkerInterval = 1000;
	
	public static final String OrchestrationPushServiceDefinition = "AutonomicOrchestrationPush";
	public static final String OrchestrationRegisterServiceDefinition = "AutonomicOrchestrationRegister";
	public static final String AutonomicOrchestrationName = "AutonomicOrchestrationSystem";
	public static final int OrchestrationRegisterPort = 8474;
	public static final String OrchestrationRegisterURI = "auto/orchestration";
	
	public static final int SubstitutionParameterSize = 6;
	public static final int ConfigureParameterSize = 4;
}
