package eu.arrowhead.autonomic.orchestrator.manager.monitor;

public abstract class BaseConsumerWorker implements Runnable {
	
	protected String serviceEndpoint;
	protected String serviceName;
	private Thread t;
	private boolean isRunning = false;
	protected Monitor monitor;
	
	public BaseConsumerWorker(String name, String endpoint)
	{
		this.serviceEndpoint = endpoint;
		this.serviceName = name;
	}
	
	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public void start () {
		      System.out.println("Starting consuming service: " + serviceName );
		      if (t == null) {
		         t = new Thread (this, serviceName);
		         t.start ();
		      }
		   }
	   
	   public void run() {
		   
		      System.out.println("Consuming " +  serviceName );
		      isRunning = true;
		      try {
		         while(isRunning) {
		            consumeService();
		            // Let the thread sleep for a while.
		            Thread.sleep(500);
		         }
		      } catch (InterruptedException e) {
		         System.out.println("Thread " +  serviceName + " interrupted.");
		      }
		      System.out.println("Thread " +  serviceName + " exiting.");
		   }
		   
	   public void stop()
	   {
		   isRunning = false;
		   t = null;
	   }
	   
	   public void join()
	   {
		   try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	
	
	public abstract void consumeService();
	
}
