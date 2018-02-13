package project.industrial.benchmark.core;

import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.injectors.Injector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PeopleMutationBuilderConcurrent implements MutationBuilder {

    private static final Logger logger = LoggerFactory.getLogger(PeopleMutationBuilderConcurrent.class);
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private final int endKey;
    private final int startKey;
    private int current;

    public PeopleMutationBuilderConcurrent(int startKey, int endKey) {
        this.startKey = startKey;
        this.endKey = endKey;
        this.current = startKey;
    }

    @Override
    public List<Mutation> build(String data) {
        List<Mutation> mutations = new ArrayList<>();
        // date, name, firstname, email, url, ip
        String[] parts = data.split(",");
        String key = this.generateRandomKey();
        mutations.add(this.buildMutation(key, "meta", "date", parts[0]));
        mutations.add(this.buildMutation(key, "identity", "name", parts[1]));
        mutations.add(this.buildMutation(key, "identity", "firstname", parts[2]));
        mutations.add(this.buildMutation(key, "meta", "email", parts[3]));
        mutations.add(this.buildMutation(key, "access", "url", parts[4]));
        mutations.add(this.buildMutation(key, "access", "ip", parts[5]));
        return mutations;
    }

    public String generateRandomKey() {
        StringBuilder key = new StringBuilder();
        key.append(this.current++);
        return key.toString();
    }

    private Mutation buildMutation(String key, String cf, String cq, String value) {
        Mutation m = new Mutation(key.trim());
        m.put(cf.trim(), cq.trim(), value.trim());
        return m;
    }

    public static int buildFromCSV(String filename, Injector injector, int start, int end) {
        PeopleMutationBuilderConcurrent builder = new PeopleMutationBuilderConcurrent(start, end);
        logger.info(String.format("Reading '%s'", filename));
        BufferedReader reader;
        int countLine = 0;
        String line;
        try {
            reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                injector.inject(builder.build(line.trim()));
                countLine++;
            }
        } catch (IOException | MutationsRejectedException e) {
            e.printStackTrace();
        }
        return countLine;
    }

}
