package ee.dev.proekspert;

public class LogAnalyzer {

	public static void main(String[] args) {
		String logfile = args[0];
		int top = Integer.parseInt(args[1]);
		
		System.out.println (
				"Printing out top " + top 
				+ " resources with highest average request duration from " + logfile 
				+ " >>>"
			);
		
	}

}
