package eu.arrowhead.autonomic.orchestrator;

import org.apache.jena.sys.JenaSystem;

import eu.arrowhead.autonomic.orchestrator.manager.analysis.Analysis;
import eu.arrowhead.autonomic.orchestrator.manager.execute.Execute;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.Monitor;
import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import no.prediktor.apis.demo.consumer.DemoConsumer;

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
		
		//DemoConsumer demo1 = new DemoConsumer("3244631");
		DemoConsumer demo1 = new DemoConsumer("9575530");
		DemoConsumer demo2 = new DemoConsumer("2999285");
		monitor.AddConsumer(demo1);
		monitor.AddConsumer(demo2);
		
		
		Analysis analysis = new Analysis();
		Plan plan = new Plan();
		Execute execute = new Execute(plan);
		
		monitor.start();
		analysis.start();
		
		plan.start();
		execute.start();
		
    }
}
