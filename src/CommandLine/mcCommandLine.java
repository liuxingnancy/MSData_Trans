package CommandLine;

import java.io.File;

import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class mcCommandLine {
	
	private static final Logger log = Logger.getLogger(mcCommandLine.class);
	private static clusterFileMonitor filemonitor;
	private static CommandLineParser parser;
	private static CommandLine cl;
	private static Options options = new Options();
	
	static {
		options.addOption("h", "help", false, "List short help");
		options.addOption("r","remote", true, "The monitored remote directory path");
		options.addOption("d", "database", true, "The database directory path");
		options.addOption("t", "timeout", true, "The monitor timeout" );
		
		log.removeAllAppenders();
		log.setLevel(Level.INFO);
		log.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%p] %m%n")));
	}
	
	private static void printHelp(Options options) {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("Monitor of remote directory, and copy the new generated files to database", options);
	}
	
	public static void main(String[] args) {
		parser = new PosixParser();
		try {
			cl = parser.parse(options, args);
		} catch (ParseException e) {
			printHelp(options);
			e.printStackTrace();
		}
		if(cl.hasOption("h") || !cl.hasOption("r") || !cl.hasOption("d")) {
			log.warn("Please specify the option -r|--remote and -d|--database");
			printHelp(options);
			System.exit(1);
		}
		
		File remoteDir = new File(cl.getOptionValue("r"));
		if(!remoteDir.exists()) {
			log.error("The file path doesn't exists: " + remoteDir.getAbsolutePath());
			System.exit(2);
		}
		File databaseDir = new File(cl.getOptionValue("d"));
		if(!databaseDir.exists()) {
			log.info("The file path doesn't exists: " + databaseDir.getAbsolutePath());
			databaseDir.mkdirs();
			log.info("Maked the database directory!");
		}
		long timeout = cl.hasOption("t") ? Long.parseLong(cl.getOptionValue("t")) : 300;
		filemonitor = new clusterFileMonitor(remoteDir, databaseDir, timeout, log);
		filemonitor.start();
	}
	
}
