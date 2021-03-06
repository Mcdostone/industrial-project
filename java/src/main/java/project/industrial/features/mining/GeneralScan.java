package project.industrial.features.mining;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Range;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;

import project.industrial.features.Printer;

/**
 * Classe de fonctionnalité
 *
 * Cette classe effectue un scan complet d'une table.
 * Il est également possible de custom la lecture des données.
 *
 * @author Yann Prono
 * @author Pierre Maeckereel
 */
public class GeneralScan {


	/**
	 * Optional arguments for the Scan class
	 */
	public static class Opts extends ClientOnRequiredTable {
		@Parameter(names = "--ranges", description = "',' delimited list of row ranges 'min-max' that will be deleted")
	    String ranges = "";
	    @Parameter(names = "--colfam", description = "column family of row that will be deleted")
	    String colfam = "";
	    @Parameter(names = "--colqual", description = "column qualifier row that will be deleted")
	    String colqual = "";
	  }
	
	private static Logger logger = Logger.getLogger(GeneralScan.class);

	/**
	   * Scans an Accumulo table using a {@link Scanner}.
	   * Prints the raw_ID, column family, column qualifier, visibility and value.
	   * Possibility to specify a range of raw_ID with options max and min
	   */
	  public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
		Opts opts = new Opts();
	    ScannerOpts sOpts = new ScannerOpts();
	    opts.parseArgs(GeneralScan.class.getName(), args, sOpts);

	    Connector connector = opts.getConnector();
	    BatchScanner scanner = connector.createBatchScanner(opts.getTableName(), opts.auths, 2);

	 // Get the ranges
	    
	    Collection<Range> ranges = new ArrayList<Range>();
	    
	    if(opts.ranges.equals("")) {
	    	ranges.add(new Range());
	    }
	    else {
	    	String[] splitRanges = opts.ranges.split(",");
		    int i;
		    for (i=0; i < splitRanges.length; i++ ) {
		    	String[] array = splitRanges[i].split("-");
		    	Text min = null;
		    	Text max = null;
		    	if (!splitRanges[i].startsWith("-")) {
		    		min = new Text(array[0]);
		    		if (array.length > 1) {
		    			max = new Text(array[1]);
		    		}
		    	}
		    	else {
		    		if (array.length < 3) {
		    			max = new Text(array[1]);
		    		}
		    	}
		    	ranges.add(new Range(min, max));
		    }
	    }
	    
		scanner.setRanges(ranges);
		
		// restrict to colqual and colfam if specified
		if (opts.colqual.equals("")) {
			if (!opts.colfam.equals(""))
				scanner.fetchColumnFamily(new Text(opts.colfam));
		}
		else
			scanner.fetchColumn(new Text(opts.colfam), new Text(opts.colqual));
	    // Printing all the scanned rows
	    System.out.println("Results ->");
	    logger.info("Scanning " + opts.getTableName() + "\n");
	    Printer.printAll(scanner.iterator());
	  }
}
