package ee.dev.proekspert;

import ee.dev.proekspert.utils.LogParser;

public class LogAnalyzer {

	public static void main(String[] args) {

		trackRunTime(true);
		
		if (args[0].equals("-h")) {
			System.out.println(
					"<<< This is the help of the program >>> \n"
					+ "- The program takes a logfile name as a first argument.\n"
					+ "- The program can also take a second number value argument for printing out top resources. "
					+ "* Default value is 3.\n"
					+ "// Example: java -jar assignment.jar timing.log 10\n"
					+ "<<< - Use '-h' for help. >>>");
			System.exit(0);
		}
		
		String logfile = args[0];
		if (logfile.isEmpty()) {
			System.err.println("xxx You must enter a filename. Use '-h' for help! xxx");
			System.exit(0);
		}
		
		int top = 0;
		if (args.length > 1) {
			try {
				top = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("xxx You must enter a number. Use '-h' for help! xxx");
				System.exit(0);
			}
		}

		LogParser.parseLog(logfile, top);		
		
		trackRunTime(false);
	}

	static long startTime;	
	private static void trackRunTime (boolean isRunning) {
		if (isRunning) {
			startTime = System.currentTimeMillis();
			System.out.println(">>> Started tracking program run time...");
		}
		else {
			long runTime = System.currentTimeMillis() - startTime;
			System.out.println(">>> Current program run time was " + runTime + " milliseconds.");
		}
			
	}
	
}
