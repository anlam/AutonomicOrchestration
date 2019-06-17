package eu.arrowhead.autonomic.orchestrator.manager.knowledge;

public class Constants {
	public static final String datasetDir = "./src/main/resources/dataset/";
	
	public static final String ModelName = "DemoModel";
	
	public static final String analysisQueriesDir = "./src/main/resources/queries/analysis/";
	
	public static final String planQueriesDir = "./src/main/resources/queries/plan/";
	
	public static final long MonitorWorkerInterval = 1000;
	public static final long AnalysisWorkerInterval = 1000;
	public static final long PlanWorkerInterval = 1000;
	public static final long ExecuteWorkerInterval = 1000;
	
	public static final String OrchestrationPushServiceDefinition = "AutonomicOrchestrationPush";
}
