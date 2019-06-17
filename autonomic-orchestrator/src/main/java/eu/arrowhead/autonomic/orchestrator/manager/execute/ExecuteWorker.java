package eu.arrowhead.autonomic.orchestrator.manager.execute;

public class ExecuteWorker implements Runnable {

	private Thread t;
	private boolean isRunning = false;
	protected Execute execute;
	private String name = "ExecuteWorker";
	private long interval;
	
	
	
	public ExecuteWorker(Execute execute, long period)
	{
		this.execute = execute;
		this.interval = period;
	}
	
	public void start() {
		if (t == null) {
			t = new Thread(this, name);
			t.start();
		}
	}

	public void run() {

		System.out.println("Thread " + name + " running.");
		isRunning = true;
		try {
			while (isRunning) {
				// consumeService();
				// Let the thread sleep for a while.
				execute.WorkerProcess();
				Thread.sleep(interval);
			}
		} catch (InterruptedException e) {
			System.out.println("Thread " + name + " interrupted.");
		}
		System.out.println("Thread " + name + " exiting.");
	}

	public void stop() {
		isRunning = false;
		t = null;
	}

	public void join() {
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
