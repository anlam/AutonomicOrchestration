package eu.arrowhead.autonomic.orchestrator.manager.knowledge;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBase {

    private static KnowledgeBase instance;

    private ReentrantLock lock;
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBase.class);

    // private KnowledgeBaseWorker knowledgeBaseWorker;

    private KnowledgeBase() {
        lock = new ReentrantLock();

        // knowledgeBaseWorker = new KnowledgeBaseWorker(this, Constants.KnowledgeBaseWorkerInterval);
        // knowledgeBaseWorker.start();
    }

    public static void main2(String[] args) {
        /*
         * String updateCurentTimeQuery = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
         * "prefix rdfs: <"+RDFS.getURI()+">\n" + "prefix rdf: <"+RDF.getURI()+">\n" +
         * "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
         * "prefix xsd: <"+XSD.getURI()+">\n" +
         * "delete { :DateTimeNow :hasValue ?Value} \n" +
         * "insert { :DateTimeNow :hasValue \"" + new Date().getTime() +
         * "\"^^xsd:long } \n" + "where {  :DateTimeNow :hasValue ?Value . \n" + "}";
         */

        String updateCurentTimeQuery = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                + "prefix xsd: <" + XSD.getURI() + ">\n"

                + "delete data { "

                + ":PrediktorApisServer :consumesService :Service_9575530. \n"

                + "}";

        String updateCurentTimeQuery2 = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                + "prefix xsd: <" + XSD.getURI() + ">\n"

                + "insert data { "

                + ":PrediktorApisServer :consumesService :Service_2999285. \n"

                + "}";

        System.out.println(updateCurentTimeQuery);

        List<String> queries = new ArrayList<String>();
        queries.add(updateCurentTimeQuery);
        queries.add(updateCurentTimeQuery2);
        // queries.add(offlineString);
        // queries.add(onlineString);
        KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
        KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");
        // KnowledgeBase.getInstance().ExecuteQuery(queries);

    }

    public static void main1(String[] args) {
        /*
         * String updateCurentTimeQuery = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
         * "prefix rdfs: <"+RDFS.getURI()+">\n" + "prefix rdf: <"+RDF.getURI()+">\n" +
         * "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
         * "prefix xsd: <"+XSD.getURI()+">\n" +
         * "delete { :DateTimeNow :hasValue ?Value} \n" +
         * "insert { :DateTimeNow :hasValue \"" + new Date().getTime() +
         * "\"^^xsd:long } \n" + "where {  :DateTimeNow :hasValue ?Value . \n" + "}";
         */

        // KnowledgeBase.getInstance().AddSensor("Device_1199791", "Service_1199791", "TopMiddle", "TellUConnector");
        KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");
        // KnowledgeBase.getInstance().ExecuteQuery(queries);

    }

    public static void main(String[] args) {

        /*
         * String updateCurentTimeQuery = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
         * "prefix rdfs: <"+RDFS.getURI()+">\n" +
         * "prefix rdf: <"+RDF.getURI()+">\n" +
         * "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
         * "prefix xsd: <"+XSD.getURI()+">\n"
         *
         *
         * + "delete data { "
         *
         * +
         * ":ANewConsumer  :hasJenaRule  :ANewConsumer_rule1 .\r\n" +
         * "\r\n" +
         * ":ANewConsumer_rule1  :hasBody  \"[ ANewConsumer_rule1: (?c rdf:type auto:Consumer) (?c auto:consumesService ?s1) (?s1 auto:hasState auto:OfflineState) (?o1 sosa:madeBySensor ?d1) (?o1 auto:hasUnit ?u1) (?p1 auto:producesService ?s1) (?d1 auto:hasService ?s1) (?d1 sosa:hasLocation ?l) (?d2 auto:hasService ?s2) (?o2 sosa:madeBySensor ?d2) (?o2 auto:hasUnit ?u2) notEqual(?u1 ?u2) (?d2 sosa:hasLocation ?l) (?s2 auto:hasState auto:OnlineState) (?p2 auto:producesService ?s2) -> substituteService(?c ?s1 ?p1 ?s2 ?p2) configure(?c 'unit' ?u2) ]\" ."
         * + "}";
         *
         * System.out.println(updateCurentTimeQuery);
         *
         * List<String> queries = new ArrayList<String>();
         * queries.add(updateCurentTimeQuery);
         *
         * KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);
         */
        String deleteString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI() + ">\n"
                + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                + "prefix xsd: <" + XSD.getURI() + ">\n" + "delete data{ " +

                ":Observation_5028208_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"23.01\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_5028208 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_5028208_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"33\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_5028208 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_5028208_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101186\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_5028208 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_5028208_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-961, -340, 165)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_5028208 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_8900302_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"18.95\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_8900302 ;\r\n"
                + "        sosa:resultTime            1602510128000 .\r\n" + "\r\n"
                + ":Observation_8900302_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"42\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_8900302 ;\r\n"
                + "        sosa:resultTime            1602510128000 .\r\n" + "\r\n"
                + ":Observation_8900302_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101143\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_8900302 ;\r\n"
                + "        sosa:resultTime            1602510128000 .\r\n" + "\r\n"
                + ":Observation_8900302_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-707, 61, -732)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_8900302 ;\r\n"
                + "        sosa:resultTime            1602510128000 .\r\n" + "\r\n"
                + ":Observation_9335843_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"21.34\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9335843 ;\r\n"
                + "        sosa:resultTime            1602510130000 .\r\n" + "\r\n"
                + ":Observation_9335843_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"60\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9335843 ;\r\n"
                + "        sosa:resultTime            1602510130000 .\r\n" + "\r\n"
                + ":Observation_9335843_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101394\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9335843 ;\r\n"
                + "        sosa:resultTime            1602510130000 .\r\n" + "\r\n"
                + ":Observation_9335843_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-581, 743, 404)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_9335843 ;\r\n"
                + "        sosa:resultTime            1602510130000 .\r\n" + "\r\n"
                + ":Observation_9575530_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"22.86\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9575530 ;\r\n"
                + "        sosa:resultTime            1602510135000 .\r\n" + "\r\n"
                + ":Observation_9575530_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"34\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9575530 ;\r\n"
                + "        sosa:resultTime            1602510135000 .\r\n" + "\r\n"
                + ":Observation_9575530_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101236\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9575530 ;\r\n"
                + "        sosa:resultTime            1602510135000 .\r\n" + "\r\n"
                + ":Observation_9575530_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(551, -26, 854)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_9575530 ;\r\n"
                + "        sosa:resultTime            1602510135000 .\r\n" + "\r\n"
                + ":Observation_9772819_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"22.67\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9772819 ;\r\n"
                + "        sosa:resultTime            1602510133000 .\r\n" + "\r\n"
                + ":Observation_9772819_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"36\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9772819 ;\r\n"
                + "        sosa:resultTime            1602510133000 .\r\n" + "\r\n"
                + ":Observation_9772819_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101243\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_9772819 ;\r\n"
                + "        sosa:resultTime            1602510133000 .\r\n" + "\r\n"
                + ":Observation_9772819_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-30, 43, 1009)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_9772819 ;\r\n"
                + "        sosa:resultTime            1602510133000 .\r\n" + "\r\n"
                + ":Observation_11565686_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"22.85\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_11565686 ;\r\n"
                + "        sosa:resultTime            1602510126000 .\r\n" + "\r\n"
                + ":Observation_11565686_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"33\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_11565686 ;\r\n"
                + "        sosa:resultTime            1602510126000 .\r\n" + "\r\n"
                + ":Observation_11565686_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101261\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_11565686 ;\r\n"
                + "        sosa:resultTime            1602510126000 .\r\n" + "\r\n"
                + ":Observation_11565686_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-229, 672, 726)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_11565686 ;\r\n"
                + "        sosa:resultTime            1602510126000 .\r\n" + "\r\n"
                + ":Observation_3244631_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"19.46\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_3244631 ;\r\n"
                + "        sosa:resultTime            1602510128000 .\r\n" + "\r\n"
                + ":Observation_3244631_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"41\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_3244631 ;\r\n"
                + "        sosa:resultTime            1602510128000 .\r\n" + "\r\n"
                + ":Observation_3244631_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101364\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_3244631 ;\r\n"
                + "        sosa:resultTime            1602510128000 .\r\n" + "\r\n"
                + ":Observation_3244631_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-112, 709, 762)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_3244631 ;\r\n"
                + "        sosa:resultTime            1602510128000 .\r\n" + "\r\n"
                + ":Observation_1199791_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"19.18\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_1199791 ;\r\n"
                + "        sosa:resultTime            1602510133000 .\r\n" + "\r\n"
                + ":Observation_1199791_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"40\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_1199791 ;\r\n"
                + "        sosa:resultTime            1602510133000 .\r\n" + "\r\n"
                + ":Observation_1199791_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101347\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_1199791 ;\r\n"
                + "        sosa:resultTime            1602510133000 .\r\n" + "\r\n"
                + ":Observation_1199791_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-208, 1005, 118)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_1199791 ;\r\n"
                + "        sosa:resultTime            1602510133000 .\r\n" + "\r\n"
                + ":Observation_2999285_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"22.87\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_2999285 ;\r\n"
                + "        sosa:resultTime            1602510129000 .\r\n" + "\r\n"
                + ":Observation_2999285_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"37\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_2999285 ;\r\n"
                + "        sosa:resultTime            1602510129000 .\r\n" + "\r\n"
                + ":Observation_2999285_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101116\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_2999285 ;\r\n"
                + "        sosa:resultTime            1602510129000 .\r\n" + "\r\n"
                + ":Observation_2999285_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-50, -2, 1045)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_2999285 ;\r\n"
                + "        sosa:resultTime            1602510129000 .\r\n" + "\r\n"
                + ":Observation_77741_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"23.02\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_77741 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n" + ":Observation_77741_Humidity\r\n"
                + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"33\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_77741 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n" + ":Observation_77741_Pressure\r\n"
                + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101162\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_77741 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_77741_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(348, -269, 979)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_77741 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_8116322_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"20.6\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_8116322 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_8116322_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"39\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_8116322 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_8116322_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101250\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_8116322 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_8116322_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-28, -37, 1023)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_8116322 ;\r\n"
                + "        sosa:resultTime            1602510136000 .\r\n" + "\r\n"
                + ":Observation_3665251_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"23.31\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_3665251 ;\r\n"
                + "        sosa:resultTime            1602510131000 .\r\n" + "\r\n"
                + ":Observation_3665251_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"33\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_3665251 ;\r\n"
                + "        sosa:resultTime            1602510131000 .\r\n" + "\r\n"
                + ":Observation_3665251_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101278\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_3665251 ;\r\n"
                + "        sosa:resultTime            1602510131000 .\r\n" + "\r\n"
                + ":Observation_3665251_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-494, -481, 759)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_3665251 ;\r\n"
                + "        sosa:resultTime            1602510131000 .\r\n" + "\r\n"
                + ":Observation_14672725_Temperature\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Temperature ;\r\n"
                + "        sosa:hasSimpleResult       \"19.14\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_14672725 ;\r\n"
                + "        sosa:resultTime            1602510127000 .\r\n" + "\r\n"
                + ":Observation_14672725_Humidity\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Percentage\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Humidity ;\r\n"
                + "        sosa:hasSimpleResult       \"41\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_14672725 ;\r\n"
                + "        sosa:resultTime            1602510127000 .\r\n" + "\r\n"
                + ":Observation_14672725_Pressure\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"Pa\" ;\r\n"
                + "        sosa:hasFeatureOfInterest  :Pressure ;\r\n"
                + "        sosa:hasSimpleResult       \"101342\"^^xsd:double ;\r\n"
                + "        sosa:madeBySensor          :Device_14672725 ;\r\n"
                + "        sosa:resultTime            1602510127000 .\r\n" + "\r\n"
                + ":Observation_14672725_Acceleration\r\n" + "        a                          sosa:Observation ;\r\n"
                + "        :hasUnit                   \"celsius\" ;\r\n" + "        sosa:hasFeatureOfInterest  :m ;\r\n"
                + "        sosa:hasSimpleResult       \"(-817, 441, -412)\" ;\r\n"
                + "        sosa:madeBySensor          :Device_14672725 ;\r\n"
                + "        sosa:resultTime            1602510127000 ." +

                "}";

        List<String> queries = new ArrayList<String>();
        queries.add(deleteString);
        KnowledgeBase.getInstance().ExecuteUpdateQueries(queries);

        KnowledgeBase.getInstance().WriteModelToFile("./dataset.ttl");

    }

    public static KnowledgeBase getInstance() {
        if (instance == null) {
            instance = new KnowledgeBase();
        }
        return instance;
    }

    public void AddStateMent(Statement s) {
        lock.lock();

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            model.add(s);

            dataset.commit();

        } catch (Exception e) {
            log.error("Fail to add statement: " + e.getMessage());
            System.err.println("Fail to add statement: " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }
    }

    @Scheduled(fixedDelay = Constants.KnowledgeBaseWorkerInterval)
    public void WorkerProcess() {
        WriteModelToFile(Constants.knowledgeBaseFileName);

    }

    public void WriteModelToFile(String filename) {

        lock.lock();

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);
        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            model.setNsPrefix("sosa", OntologyNames.SOSA_URL);
            model.setNsPrefix(":", OntologyNames.BASE_URL);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);

            FileOutputStream newFile = new FileOutputStream(new File(filename));
            model.write(newFile, "TTL");
            newFile.close();

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to WriteModelToFile: " + e.getMessage());
            System.err.println("Fail to WriteModelToFile: " + e.getMessage());
            // e.printStackTrace();
        } finally {

            dataset.end();
            lock.unlock();

        }
    }

    public List<QuerySolution> ExecuteSelectQuery(String queries) {

        lock.lock();
        List<QuerySolution> ret = new ArrayList<QuerySolution>();
        log.debug("Knowledgebase Executing Select query");
        // System.out.println("Knowledgebase Executing Select query");

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.READ);

        try {
            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            Query query = QueryFactory.create(queries);
            // System.out.println(query);
            QueryExecution qexec = QueryExecutionFactory.create(query, model);

            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                ret.add(soln);

            }

        } catch (Exception e) {
            log.error("Fail to execute query: " + e.getMessage());
            System.err.println("Fail to execute query: " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

        return ret;
    }

    public void ExecuteUpdateQueries(List<String> queries) {
        lock.lock();
        // AddService(serviceName);

        log.debug("Knowledgebase Executing Update queries");
        // System.out.println("Knowledgebase Executing Update queries");

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            for (String query : queries) {

                UpdateAction.parseExecute(query, model);
            }

            model.setNsPrefix("sosa", OntologyNames.SOSA_URL);
            model.setNsPrefix(":", OntologyNames.BASE_URL);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            // model.write(new FileOutputStream(new File("./dataset.txt")), "TTL" );

            dataset.commit();

        } catch (Exception e) {
            log.error("Fail to execute query: " + e.getMessage());
            System.err.println("Fail to execute query: " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }
    }

    public void Reasoning(List<Rule> rules) {
        lock.lock();
        log.debug("Knowledgebase Reasoning Rules");
        // System.out.println("Knowledgebase Reasoning Rules");

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.READ);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            Reasoner reasoner = new GenericRuleReasoner(rules);

            InfModel infModel = ModelFactory.createInfModel(reasoner, model);

            // System.out.println("Knowledgebase Reasoning Rules prepare");

            infModel.prepare();

        } catch (Exception e) {
            log.error("Fail to reason rules: " + e.getMessage());
            System.err.println("Fail to reason rules:  " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }
    }

    public void AddObservation(String observationId, String sensorId, long timestamp, String value,
            String featureOfInterest, String unit, String datatype) {
        lock.lock();

        log.debug(String.format("Knowledgebase Adding observation %s, %s, %d, %s", observationId, sensorId, timestamp,
                value));
        // System.out.println(String.format("Knowledgebase Adding observation %s, %s, %d, %s", observationId, sensorId,
        // timestamp, value));

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);
        boolean isObservationExisted = false;

        try {
            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            /*
             * String queryString = "prefix : <"+ OntologyNames.BASE_URL+">\n" +
             * "prefix rdfs: <"+RDFS.getURI()+">\n" +
             * "prefix rdf: <"+RDF.getURI()+">\n" +
             * "prefix sosa: <"+OntologyNames.SOSA_URL+">\n" +
             * //"select ?obs \n" +
             * "ask { ?obs rdf:type sosa:Observation . \n" +
             * "?obs sosa:madeBySensor :" + sensorId + " . \n" +
             * "}";
             */

            String queryString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                    + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    +
                    // "select ?obs \n" +
                    "ask {" + ":" + observationId + " rdf:type sosa:Observation . \n" + "}";

            String updateString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                    + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    + "prefix xsd: <" + XSD.getURI() + ">\n" + "delete { :" + observationId
                    + " sosa:hasSimpleResult ?value. \n" + ":" + observationId + " sosa:resultTime ?time. \n" + "} \n"
                    + "insert { :" + observationId + " sosa:hasSimpleResult \"" + value + "\"^^xsd:" + datatype
                    + " . \n" + ":" + observationId + " sosa:resultTime  \"" + timestamp + "\"^^xsd:long . \n" + "} \n"
                    + "where { :" + observationId + " sosa:hasSimpleResult ?value. \n" + ":" + observationId
                    + " sosa:resultTime ?time. \n" + "}";

            String addString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI() + ">\n"
                    + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    + "prefix xsd: <" + XSD.getURI() + ">\n" + "insert data { " + ":" + observationId
                    + " rdf:type sosa:Observation . \n" + ":" + observationId + " sosa:madeBySensor :" + sensorId
                    + " . \n" + ":" + observationId + " sosa:hasFeatureOfInterest :" + featureOfInterest + " . \n" + ":"
                    + observationId + " sosa:hasSimpleResult \"" + value + "\"^^xsd:" + datatype + " . \n" + ":"
                    + observationId + " sosa:resultTime  \"" + timestamp + "\"^^xsd:long . \n" + ":" + observationId
                    + " :hasUnit  \"" + unit + "\" . \n" + "}";

            // System.out.println(addString);

            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            isObservationExisted = qexec.execAsk();

            if (isObservationExisted) {
                UpdateAction.parseExecute(updateString, model);
            } else {
                UpdateAction.parseExecute(addString, model);
            }

            dataset.commit();
        } catch (Exception e) {
            log.error("Fail to AddObservation: " + e.getMessage());
            System.err.println("Fail to AddObservation:  " + e.getMessage());
            // e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

    public void AddSensor(String sensorName, String serviceName, String location, String producer,
            String serviceDefinition) {
        lock.lock();
        AddService(serviceName, producer, serviceDefinition);

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String deleteString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                    + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    + "prefix xsd: <" + XSD.getURI() + ">\n" + "delete data{ " + ":" + sensorName
                    + " rdf:type :SensorUnit . \n" +
                    // ":" + sensorName + " :hasID \"" + sensorName + "\" . \n" +
                    ":" + sensorName + " :hasService :" + serviceName + " . \n" + ":" + sensorName
                    + " sosa:hasLocation :" + location + " . \n" + "}";
            UpdateAction.parseExecute(deleteString, model);

            String addString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI() + ">\n"
                    + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    + "prefix xsd: <" + XSD.getURI() + ">\n" + "insert data{ " + ":" + sensorName
                    + " rdf:type :SensorUnit . \n" +
                    // ":" + sensorName + " :hasID \"" + sensorName + "\" . \n" +
                    ":" + sensorName + " :hasService :" + serviceName + " . \n" + ":" + sensorName
                    + " sosa:hasLocation :" + location + " . \n" + "}";

            UpdateAction.parseExecute(addString, model);

            dataset.commit();

        } catch (Exception e) {
            log.error("Fail to add sensor: " + e.getMessage());
            System.err.println("Fail to add sensor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

    public void AddService(String serviceName, String producer, String serviceDefinition) {
        lock.lock();

        Dataset dataset = TDBFactory.createDataset(Constants.datasetDir);
        dataset.begin(ReadWrite.WRITE);

        try {

            Model model = dataset.getNamedModel(OntologyNames.BASE_URL + Constants.ModelName);

            String deleteString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI()
                    + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    + "prefix xsd: <" + XSD.getURI() + ">\n" + "delete data{ " + ":" + serviceName
                    + " rdf:type :Service . \n" + ":" + serviceName + " :hasServiceDefinition " + "\""
                    + serviceDefinition + "\"" + " . \n" + ":" + producer + " rdf:type :Producer . \n" + ":" + producer
                    + " :producesService :" + serviceName + " . \n" +
                    // ":" + serviceName + " :hasID \"" + serviceName + "\" . \n" +
                    "}";

            UpdateAction.parseExecute(deleteString, model);

            String addString = "prefix : <" + OntologyNames.BASE_URL + ">\n" + "prefix rdfs: <" + RDFS.getURI() + ">\n"
                    + "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix sosa: <" + OntologyNames.SOSA_URL + ">\n"
                    + "prefix xsd: <" + XSD.getURI() + ">\n" + "insert data{ " + ":" + serviceName
                    + " rdf:type :Service . \n" + ":" + serviceName + " :hasServiceDefinition " + "\""
                    + serviceDefinition + "\"" + " . \n" + ":" + producer + " rdf:type :Producer . \n" + ":" + producer
                    + " :producesService :" + serviceName + " . \n" +
                    // ":" + serviceName + " :hasID \"" + serviceName + "\" . \n" +
                    "}";

            UpdateAction.parseExecute(addString, model);

            dataset.commit();

        } catch (Exception e) {
            log.error("Fail to add service: " + e.getMessage());
            System.err.println("Fail to add service: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataset.end();
            lock.unlock();
        }

    }

}
