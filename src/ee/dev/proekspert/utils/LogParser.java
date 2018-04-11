package ee.dev.proekspert.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
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
			Record r;

			// keep running average of resource request while parsing
			SortedMap<String, Long> runningAverages = new TreeMap<String, Long>();

			while ((line = fileBuffer.readLine()) != null) { 
				//System.out.print((i+1)+". line: " + line);
				i++;
				r = Record.getRecord(line);
				records.add(r);
				
				// keep running average duration of each resource
				updateRunningAverage(runningAverages, r);
				
				// keep running number of requests per hour
				updateNrOfRequests(r);
			}
			
			System.out.println("<<< Log file '" + logfile + "' parsing finished >>>");
			System.out.println(">>> Lines read: " + i + " / Records created : "+records.size());
			
			// print top n resources with highest average request duration
			printTopResources(runningAverages, top);
			
			// draw a histogram of hourly number of requests
			drawHistogram();
									
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

	}

	// methods for doing stuff with records

	private static void updateRunningAverage(SortedMap<String, Long> runningAverages, Record r) {
		if (runningAverages.containsKey(r.getResource())) {
			Long newAverage = (runningAverages.get(r.getResource()) + r.getDuration()) / 2;
			runningAverages.put(r.getResource(), newAverage);
		} else
			runningAverages.put(r.getResource(), r.getDuration());
	}

	private static void printTopResources(SortedMap<String, Long> runningAverages, int top) {
		System.out.println("<<< Top " + top + " resources with highest average request duration >>>");

		SortedSet<Map.Entry<String, Long>> sortedAverages = new TreeSet<Map.Entry<String, Long>>(
				new Comparator<Map.Entry<String, Long>>() {

					@Override
					public int compare(Entry<String, Long> arg0, Entry<String, Long> arg1) {
						return arg1.getValue().compareTo(arg0.getValue());
					}

				});
		sortedAverages.addAll(runningAverages.entrySet());

		int topLength = (top + "").length();
		int longest = sortedAverages.first().getValue().toString().length();
		String outputFormat = "%" + topLength + "d. %" + longest + "d - %s %n";

		System.out.println("<  Avg duration - resource name  >");

		int i = 0;
		for (Entry<String, Long> e : sortedAverages) {
			i++;
			System.out.format(outputFormat, i, e.getValue(), e.getKey()); // TODO: nr of occurences
			if (i == top)
				break;
		}

	}

	private static void updateNrOfRequests(Record r) {
		// TODO Auto-generated method stub
		
	}

	private static void drawHistogram() {
		System.out.println("<<< Histogram of hourly number of requests >>>");

	}
}
