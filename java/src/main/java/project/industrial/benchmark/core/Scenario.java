package project.industrial.benchmark.core;

import com.codahale.metrics.Counter;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Un scénario définit un ensemble d'actions (de lecture, d'écriture)
 * qui sont surveillées par des mesures et des métriques.
 * En général, un scénario fait appel à une ou plusieurs Tasks
 *
 * @author Yann Prono
 */
public abstract class Scenario {

    /** A name for the scenario, used in graphite */
    protected String name;
    protected static Logger logger = LoggerFactory.getLogger(Scenario.class);
    protected ScheduledExecutorService exe;

    /**
     * @param name Name of the new scenario
     */
    public Scenario(String name) {
        this.name = name;
        MetricsManager.initReporters();
    }

    public Scenario(Class cls) {
        this(cls.getSimpleName());
    }

    /**
     * @param message Logs this message if false
     * @param condition Condition to check
     * @throws Exception
     */
    public void assertTrue(String message, boolean condition) {
        if(!condition)
            System.err.println(message);
    }

    public void assertFalse(String message, boolean condition) {
        this.assertTrue(message, !condition);
    }

    /**
     * @param iterator Iterator from a ScannerBase class
     * @return Number of elements in the iterator.
     */
    public int countResults(Iterator<Map.Entry<Key, Value>> iterator) {
        Counter countObjects = MetricsManager.getMetricRegistry().counter("count_data_retrieved");
        while(iterator.hasNext()) {
            countObjects.inc();
            iterator.next();
        }
        return (int) countObjects.getCount();
    }

    public void assertEquals(String message, Object expected, Object given) throws Exception {
        String err = String.format("Expected %s, given %s, ", expected, given);
        this.assertTrue(message + " " + err, expected.equals(given));
    }

    /**
     * Save results of scanner into a CSV file
     * @throws IOException
     */
    public void saveResultsInCSV(Iterator<Map.Entry<Key, Value>> it) throws IOException {
        List<String> data = new ArrayList<>();
        while(it.hasNext()) {
            data.add(this.entryToCSV(it.next()));
        }
        this.saveResultsInCSV(data);
    }

    public void saveResultsInCSV(List<String> data) throws IOException {
        String filename = Paths.get(
                System.getProperty("user.dir"),
                this.getClass().getSimpleName() + ".csv"
        ).toString();
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        data.forEach(d -> {
            try { bw.write(d + "\n"); }
            catch (IOException e) { e.printStackTrace(); }
        });
        bw.close();
    }

    protected String entryToCSV(Map.Entry<Key, Value> entry) {
        return String.format("%s, %s, %s, %s, %d, %s\n",
                entry.getKey().getRow(),
                entry.getKey().getColumnFamily(),
                entry.getKey().getColumnQualifier(),
                entry.getKey().getColumnVisibility(),
                entry.getKey().getTimestamp(),
                entry.getValue()
        );
    }

    public static String askInput(String message) {
        Scanner sc  = new Scanner(System.in);
        System.out.print("\u001B[34m> " + message + "\u001B[0m ");
        return sc.nextLine().trim();
    }

    public void run() throws Exception {
        this.action();
    }

    /**
     * Close the scenario (like at the end of a shooting)
     */
    public void finish() {
        logger.info(String.format("Scenario '%s' finished",this.name));
        MetricsManager.close();
//        MetricsManager.getInstance().report();
    }

    /**
     * like at the beginning of a shooting,
     * we shout "action" and the story takes place in front of the camera.
     * Action contains your operations (actors) and your measurements (filmmaker)
     * @throws Exception
     */
    protected abstract void action() throws Exception;

}