package ee.dev.proekspert;

public class LogAnalyzer {

	public static void main(String[] args) {

		trackRunTime(true);
		
		
		
		String logfile = args[0];
		int top = Integer.parseInt(args[1]);
		
		System.out.println (
				"<<< Printing out top " + top 
				+ " resources with highest average request duration from " + logfile 
				+ " >>>"
			);
		
		
		
		trackRunTime(false);
	}

	static long startTime;	
	private static void trackRunTime (boolean isRunning) {
		if (isRunning) {
			startTime = System.currentTimeMillis();
			System.out.println("><Started tracking program run time...\n");
		}
		else {
			long runTime = System.currentTimeMillis() - startTime;
			System.out.println("\n><Current program run time was " + runTime + " milliseconds.");
		}
			
	}
	
}
