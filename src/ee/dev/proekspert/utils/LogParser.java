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
			System.err.println("xxx File not found: "+e.getMessage()+" xxx");
			System.exit(0);
		}
			
		ArrayList<Record> records = new ArrayList<Record>();
		int i=0;
		//parse log, get records
		try {
			
			System.out.println("<<< Parsing log file >>> ");
			
			String line;
			while ((line = fileBuffer.readLine()) != null) { 
				//System.out.print((i+1)+". line: " + line);
				i++;
				records.add(Record.getRecord(line));
				//TODO: do stuff with each record
			}
			
			System.out.println("<<< Log file '" + logfile + "' parsing finished >>>");
			System.out.println(">>> Lines read: " + i + " / Records created : "+records.size());
			
			fileBuffer.close();
						
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

		//TODO: do stuff with recordlist ... if asked !?!
		
	}

}
