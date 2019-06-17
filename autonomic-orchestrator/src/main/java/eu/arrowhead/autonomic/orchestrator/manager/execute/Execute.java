package eu.arrowhead.autonomic.orchestrator.manager.execute;

import java.util.TreeMap;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;

public class Execute {

	private ExecuteWorker executeWorker;
	private TreeMap<String, String> orchestrationPushEndpoint;
	
	public Execute() {
		executeWorker = new ExecuteWorker(this, Constants.ExecuteWorkerInterval);
		orchestrationPushEndpoint = new TreeMap<String, String>();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void WorkerProcess() {
		// TODO Auto-generated method stub
		
	}
	
	public void start()
	{
		executeWorker.start();
	}
	
	public void stop()
	{
		executeWorker.stop();
	}

}
