package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;
import project.industrial.benchmark.core.MutationBuilderStrategy;

/**
 * MutationBuilderStrategy for a CSV file;
 * For this class, the value of the row is the entire data
 */
public class CSVMutationBuilderStrategy implements MutationBuilderStrategy {

    private final String cq;
    private final String cf;
    private RowIdBuilderStrategy rowIdBuilderStrategy;

    public CSVMutationBuilderStrategy() {
        this("columnFamily", "columnQualifier");
    }

    public CSVMutationBuilderStrategy(String cf, String cq) {
        this.cf = cf;
        this.cq = cq;
        this.rowIdBuilderStrategy = new IncrementorRowIdIBuilderStrategy();
    }

    @Override
    public Mutation buildMutation(String value) {
        return this.buildMutation(this.rowIdBuilderStrategy.buildRowId(), this.cf, this.cq, value);
    }

    public Mutation buildMutation(String key, String cf, String cq, String value) {
        Mutation mutation = new Mutation(key);
        mutation.put(new Text(cf), new Text(cq), new ColumnVisibility(), new Value(value));
        return mutation;
    }

    @Override
    public Mutation buildMutation(String key, String value) {
        return this.buildMutation(key, this.cf, this.cq, value);
    }

    @Override
    public void setRowKeyBuilderStrategy(RowIdBuilderStrategy rowKeyBuilderStrategy) {
        this.rowIdBuilderStrategy = rowKeyBuilderStrategy;
    }

}