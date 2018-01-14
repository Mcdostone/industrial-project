package project.industrial.benchmark.core;

import org.apache.accumulo.core.data.Mutation;

/**
 * This interface define a Strategy for creating a new mutation for accumulo
 * @author Yann Prono
 */
public interface MutationBuilderStrategy {

    public Mutation buildMutation(String data);
}

