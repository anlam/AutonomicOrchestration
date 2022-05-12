package eu.arrowhead.autonomic.orchestrator.manager.analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.Constants;
import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.monitor.Monitor;

@Service
public class Analysis {

    private TreeMap<String, String> queries;
    // private AnalysisWorker analysisWorker;
    private TreeMap<String, Long> queryLastUpdated;

    private static final Logger log = LoggerFactory.getLogger(Monitor.class);

    public Analysis() {
        queries = new TreeMap<String, String>();
        queryLastUpdated = new TreeMap<String, Long>();

    }

    @Scheduled(fixedDelay = Constants.AnalysisWorkerInterval)
    public void WorkerProcess() {
        UpdateQueries();

        log.debug("Analysis executing queries");
        // System.out.println("Analysis executing queris");

        KnowledgeBase.getInstance().ExecuteUpdateQueries(new ArrayList<String>(queries.values()));

        // KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");
    }

    // public void start() {
    // analysisWorker.start();
    // }

    public TreeMap<String, String> getAllQuries() {
        return (TreeMap<String, String>) this.queries.clone();
    }
    
    public boolean EditQuery(String name, String newQuery) {
    	this.queries.replace(name, newQuery);
    	
    	//write to file
    	try {
    		boolean valid = KnowledgeBase.getInstance().CheckValidQuery(newQuery);
    		
    		if (!valid) {
    			return false;
    		}
    		String fileName = name + ".sparql";
    		//File myObj = new File(fileName);
    		//myObj.createNewFile();
    		
    		FileWriter myWriter = new FileWriter(Constants.analysisQueriesDir + File.separator + fileName);
    		myWriter.write(newQuery);
    		myWriter.close();
    		return true;
	    } catch (IOException e) {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    	return false;
	    }
    }
    
    public boolean DeleteQuery(String name) {
    	this.queries.remove(name);
    	
    	//delete file
    	String fileName = name;
    	File myObj = new File(Constants.analysisQueriesDir + File.separator + fileName); 
        if (myObj.delete()) { 
          System.out.println("Deleted the file: " + myObj.getName());
          return true;
        } else {
          System.out.println("Failed to delete the file." + myObj.getName());
          return false;
        } 
    }

    private void UpdateQueries() {
        try {
            File dir = new File(Constants.analysisQueriesDir);
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    // System.out.println(filename);
                    return filename.endsWith(".sparql");
                }
            });

            for (File f : files) {

                String name = f.getName();

                // Check if file recently updated
                if (queryLastUpdated.containsKey(name)) {
                    long lastmodified = f.lastModified();
                    long lastupdated = queryLastUpdated.get(name);

                    if (lastupdated >= lastmodified) {
                        continue;
                    }
                }

                String query = readFile(f.getAbsolutePath());
                queries.put(name, query);
                queryLastUpdated.put(name, f.lastModified());

                log.debug("Analysis: New query registered: " + name);
                System.out.println("Analysis: New query registered: " + name);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

}
