package ee.dev.proekspert.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import ee.dev.proekspert.data.Record;

public class LogParser {

	public static void parseLog(String logfile, int top) {
		
		BufferedReader fileBuffer = null;
		
		// try to open file		
		try {
			fileBuffer = Files.newBufferedReader(Paths.get(logfile));
		} catch (IOException e) {
			System.err.println("xxx Error when opening the file: " + e.getMessage() + " xxx");
			System.exit(0);
		}
		
		//parse log, get records
		ArrayList<Record> records = new ArrayList<Record>();
		int i=0;
		try {
			
			System.out.println("<<< Parsing log file >>> ");
			
			String line;
			while ((line = fileBuffer.readLine()) != null) { 
				//System.out.print((i+1)+". line: " + line);
				i++;
				records.add(Record.getRecord(line));
				
				//TODO: keep running average duration of each resource
				
				//TODO: keep running number of requests per hour
			}
			
			System.out.println("<<< Log file '" + logfile + "' parsing finished >>>");
			System.out.println(">>> Lines read: " + i + " / Records created : "+records.size());
			
			//TODO: print top n resources with highest average request duration
			
			//TODO: draw a histogram of hourly number of requests
									
		} catch (IOException e) {
			System.err.println("xxx Not able to read line "+i+": "+e.getMessage()+" xxx");
		} catch (PatternSyntaxException pse) {
			System.console().format("There is a problem" +
                    		" with the regular expression!%n");
			System.console().format("The pattern in question is: %s%n",
		                    pse.getPattern());
			System.console().format("The description is: %s%n",
		                    pse.getDescription());
			System.console().format("The message is: %s%n",
		                    pse.getMessage());
			System.console().format("The index is: %s%n",
		                    pse.getIndex());
		}

		try {
			fileBuffer.close();
		} catch (IOException e) {
			System.err.println("xxx Error when closing the file - " + e.getMessage() + " xxx");
		}

		//TODO: methods for doing stuff with records 
		
	}

}
