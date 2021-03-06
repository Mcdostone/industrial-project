package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.PeopleMutationBuilder;
import project.industrial.benchmark.core.PeopleMutationBuilderWithCheck;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.InjectorWithMetrics;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LoopDataRateInjectionAndCheckAvailabilityScenario extends Scenario {

    private final String filename;
    private final Scanner scanner;
    private Injector injector;
    private String[] args;
    private ScheduledExecutorService executorService;

    public LoopDataRateInjectionAndCheckAvailabilityScenario(BatchWriter bw, String filename, Scanner sc, String[] args) {
        super(LoopDataRateInjectionAndCheckAvailabilityScenario.class);
        this.filename = filename;
        this.injector = new InjectorWithMetrics(bw);
        this.scanner = sc;
        this.executorService =  Executors.newScheduledThreadPool(50);
        this.args = args;
    }

    @Override
    public void action() {
        PeopleMutationBuilderWithCheck builder = new PeopleMutationBuilderWithCheck(this.scanner, new PeopleMutationBuilder(), args);
        builder.loopInjectFromCSV(this.filename, this.injector);
    }

    @Override
    public void finish() {
        try {
            this.injector.close();
            this.executorService.shutdown();
        } catch (MutationsRejectedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(LoopDataRateInjectionAndCheckAvailabilityScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        Scanner sc  = connector.createScanner(opts.getTableName(), opts.auths);

        // Initialisation
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc)
            System.out.println(entry.getKey() + " " + entry.getValue());

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new LoopDataRateInjectionAndCheckAvailabilityScenario(bw, opts.csv, sc, args);
        scenario.run();
        scenario.finish();
    }

}
