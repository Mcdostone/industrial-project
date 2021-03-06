package project.industrial.features.mining;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Range;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import project.industrial.features.GetByKey;
import project.industrial.features.Printer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe de fonctionnalité.
 *
 * Cette classe récupère les données appartenant à une liste de ROW IDs.
 * @author Yann Prono
 */
public class GetByList {

    private static Logger logger = Logger.getLogger(GetByList.class);

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--id", required = true, description = "the list of ID you want to retrieve")
        List<String> rowIds = null;
    }

    public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
        GetByList.Opts opts = new GetByList.Opts();
        ScannerOpts sOpts = new ScannerOpts();
        opts.parseArgs(GetByKey.class.getName(), args, sOpts);
        Connector connector = opts.getConnector();

        if(!connector.tableOperations().exists(opts.getTableName()))
            logger.warn("Table " + opts.getTableName() + " doesn't exist");
        else {
            // Prépare un batchScanner pour la lecture
            BatchScanner scanner = connector.createBatchScanner(opts.getTableName(), opts.auths, 10);
            // Indique au scanner la liste des ROW IDs à fetch
            scanner.setRanges(opts.rowIds.stream().map(id -> Range.exact(new Text(id))).collect(Collectors.toList()));
            logger.info("Looking for rows where ID in {" + String.join(",", opts.rowIds) + "}");
            Printer.printAll(scanner.iterator());
        }
    }
}
