package project.industrial.features;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Range;
import org.apache.log4j.Logger;

/**
 * Classe de fonctionnalité.
 *
 * Cette classe effectue un GET BY KEY à partir d'une ROW ID donnée.
 *
 * @author Yann Prono
 */
public class GetByKey {

	private static Logger logger = Logger.getLogger(GetByKey.class);

	static class Opts extends ClientOnRequiredTable {
		@Parameter(names = "--id", required = true, description = "the rowId you want to to retrieve")
		String rowId = null;
	}

	public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
		Opts opts = new Opts();
		ScannerOpts sOpts = new ScannerOpts();
		opts.parseArgs(GetByKey.class.getName(), args, sOpts);
		Connector connector = opts.getConnector();

		if(!connector.tableOperations().exists(opts.getTableName()))
			logger.warn("Table " + opts.getTableName() + " doesn't exist");
		else {
			// Instanciation d'un scanner pour effectuer la lecture de la donnée
			Scanner scanner = connector.createScanner(opts.getTableName(), opts.auths);
			// On souhaite uniquement récupérer un range d'une seule ROW ID
			scanner.setRange(Range.exact(opts.rowId));
			logger.info(String.format("Retrieve row with rowID=%s in '%s'\n", opts.rowId, opts.getTableName()));
			Printer.printAll(scanner.iterator());
		}
	}
}
