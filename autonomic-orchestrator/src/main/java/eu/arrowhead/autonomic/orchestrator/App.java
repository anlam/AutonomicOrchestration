package eu.arrowhead.autonomic.orchestrator;

import eu.arrowhead.autonomic.orchestrator.manager.analysis.Analysis;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.Monitor;
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
        
		Monitor monitor = new Monitor();
		
		DemoConsumer demo1 = new DemoConsumer("3244631");
		DemoConsumer demo2 = new DemoConsumer("2999285");
		monitor.AddConsumer(demo1);
		monitor.AddConsumer(demo2);
		
		
		Analysis analysis = new Analysis();
		
		
		monitor.start();
		analysis.start();
		
    }
}
