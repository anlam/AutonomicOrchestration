package eu.arrowhead.autonomic.orchestrator.manager.analysis;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("application")
public class AnalysisWorker implements Runnable {

    private Thread t;
    private boolean isRunning = false;
    protected Analysis analysis;
    private String name = "MonitorWorker";
    private long interval;

    public AnalysisWorker(Analysis analysis, long period) {
        this.analysis = analysis;
        this.interval = period;
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    @Override
    public void run() {

        System.out.println("Thread " + name + " running.");
        isRunning = true;

        while (isRunning) {
            try {

                // consumeService();
                // Let the thread sleep for a while.
                analysis.WorkerProcess();
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
