package eu.arrowhead.autonomic.orchestrator;

import org.apache.jena.sys.JenaSystem;

import eu.arrowhead.autonomic.orchestrator.manager.analysis.Analysis;
import eu.arrowhead.autonomic.orchestrator.manager.execute.Execute;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.Monitor;
import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import no.hiof.tellu.demo.consumer.NewMQTTClient;
import no.prediktor.apis.demo.consumer.DemoConsumer;
import no.prediktor.apis.demo.consumer.DemoTemperatureConsumer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //System.out.println( "Hello World!" );
        
    	JenaSystem.init();
    	
    	
    	
		Monitor monitor = new Monitor();
		
		//DemoTemperatureConsumer demo1 = new DemoTemperatureConsumer("IndoorTemperature", "InsecureTemperatureSensor", "Room1");
		//monitor.AddConsumer(demo1);
		
		//DemoTemperatureConsumer demo2 = new DemoTemperatureConsumer("IndoorTemperature2", "InsecureTemperatureSensor2", "Room1");
		//monitor.AddConsumer(demo2);
		
		//DemoTemperatureConsumer demo3 = new DemoTemperatureConsumer("IndoorTemperature3", "InsecureTemperatureSensor3", "Room1");
		//monitor.AddConsumer(demo3);
		//DemoConsumer demo1 = new DemoConsumer("1199791");
		//DemoConsumer demo2 = new DemoConsumer("2999285");
		
		//monitor.AddConsumer(demo2);
		try {
			NewMQTTClient telluMonitor = new NewMQTTClient(null, null);
			telluMonitor.initialize();
			monitor.AddConsumer(telluMonitor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		Analysis analysis = new Analysis();
		Plan plan = new Plan();
		Execute execute = new Execute(plan);
		
		monitor.start();
		analysis.start();
		
		
		plan.start();
		execute.start();
		
		
		//KnowledgeBase.getInstance().start();
		
		
		OrchestrationRegisterResource.plan = plan;
		OrchestrationRegisterResource.analysis = analysis;
		new OrchestrationRegisterProvider();
		
		
    }
}
