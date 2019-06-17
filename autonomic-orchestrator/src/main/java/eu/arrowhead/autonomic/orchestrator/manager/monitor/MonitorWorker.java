package eu.arrowhead.autonomic.orchestrator.manager.monitor;

public class MonitorWorker implements Runnable {

	private Thread t;
	private boolean isRunning = false;
	protected Monitor monitor;
	private String name = "MonitorWorker";
	private long interval;

	
	public MonitorWorker(Monitor monitor, long period)
	{
		this.monitor = monitor;
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
		
		while (isRunning) {
		try {
		
				// consumeService();
				// Let the thread sleep for a while.
				monitor.WorkerProcess();
				Thread.sleep(interval);
			
		} catch (Exception e) {
			System.out.println("Thread " + name + " interrupted. Reason: " + e.getMessage());
		}
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
