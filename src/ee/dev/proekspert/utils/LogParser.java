package ee.dev.proekspert.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
			Record r = null;

			// keep running average of resource request while parsing
			Map<String, Double> runningAverages = new HashMap<String, Double>();
			Map<String, Integer> runningOccurences = new HashMap<String, Integer>();

			// keep count of requests by hour
			SortedMap<String, Integer> runningHourlyRequests = new TreeMap<String, Integer>();

			while ((line = fileBuffer.readLine()) != null) { 
				//System.out.print((i+1)+". line: " + line);
				i++;

				if (!line.isEmpty())
					r = Record.getRecord(line);
				
				if (r != null) {
					records.add(r);

					// keep running average duration of each resource
					updateRunningAverage(runningAverages, runningOccurences, r);

					// keep running number of requests per hour
					updateNrOfRequests(runningHourlyRequests, r);
				}
			}
			
			System.out.println("<<< Log file '" + logfile + "' parsing finished >>>");
			System.out.println(">>> Lines read: " + i + " / Records created : "+records.size());
			
			// print top n resources with highest average request duration
			printTopResources(runningAverages, runningOccurences, top);
			
			// draw a histogram of hourly number of requests
			drawHistogram(runningHourlyRequests);
									
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

	private static void updateRunningAverage(Map<String, Double> runningAverages,
			Map<String, Integer> runningOccurences,
			Record r) {

		int occurences = 1;
		Double average = (double) r.getDuration();

		if (runningAverages.containsKey(r.getResource())) {

			if (runningOccurences.containsKey(r.getResource()))
				occurences = runningOccurences.get(r.getResource()) + 1;

			Double prevAverage = runningAverages.get(r.getResource());
			average = prevAverage + ((r.getDuration() - prevAverage) / occurences);

		}

		runningAverages.put(r.getResource(), average);
		runningOccurences.put(r.getResource(), occurences);

	}

	private static void printTopResources(Map<String, Double> runningAverages, Map<String, Integer> runningOccurences,
			int top) {
		System.out.println("<<< Top " + top + " resources with highest average request duration >>>");

		SortedSet<Map.Entry<String, Double>> sortedAverages = new TreeSet<Map.Entry<String, Double>>(
				new Comparator<Map.Entry<String, Double>>() {

					@Override
					public int compare(Entry<String, Double> arg0, Entry<String, Double> arg1) {
						return arg1.getValue().compareTo(arg0.getValue());
					}

				});
		sortedAverages.addAll(runningAverages.entrySet());

		int topLength = (top + "").length();
		int longest = String.format("%.2f", sortedAverages.first().getValue()).length();
		String outputFormat = "%" + topLength + "d. %" + longest + ".2f - %s (%d) %n";

		System.out.println("<  Avg duration - resource name (nr of occurences) >");

		int i = 0;
		for (Entry<String, Double> e : sortedAverages) {
			i++;
			System.out.format(outputFormat, i, e.getValue(), e.getKey(), runningOccurences.get(e.getKey()));
			if (i == top)
				break;
		}

	}

	private static void updateNrOfRequests(Map<String, Integer> runningHourlyRequests, Record r) {
		String hour = r.getDateString();
		if (runningHourlyRequests.containsKey(hour))
			runningHourlyRequests.put(hour, runningHourlyRequests.get(hour) + 1);
		else
			runningHourlyRequests.put(hour, 1);

	}

	private static void drawHistogram(Map<String, Integer> runningHourlyRequests) {
		System.out.println("<<< Histogram of hourly number of requests >>>");

		// insert missing hours
		Calendar c = Calendar.getInstance();
		try {
			// setting time to first time on the list
			c.setTime(Record.sdfPrint.parse(runningHourlyRequests.keySet().toArray()[0].toString()));

			Date d = c.getTime();
			String dateString = Record.sdfPrint.format(d);

			/*
			 * System.out.println(dateString); int i = 0;
			 */

			// while not at the end of the list
			while (!dateString
					.equals(runningHourlyRequests.keySet().toArray()[runningHourlyRequests.size() - 1].toString())
			) {
				c.add(Calendar.HOUR_OF_DAY, 1);
				d = c.getTime();
				dateString = Record.sdfPrint.format(d);
				if (!runningHourlyRequests.containsKey(dateString))
					runningHourlyRequests.put(dateString, 0);
				/*
				 * i++; System.out.println(dateString + " != " +
				 * runningHourlyRequests.keySet().toArray()[runningHourlyRequests.size() -
				 * 1].toString());
				 */
			}

			int highest = Collections.max(runningHourlyRequests.values());

			int divider = ((int) Math.ceil(highest / 100.0) * 2); // make it depend on highest value?
			System.out.println(highest + " - " + divider);

			for (Entry<String, Integer> s : runningHourlyRequests.entrySet()) {
				String histogramString = new String(new char[s.getValue() / divider]).replace("\0", "|");
				System.out.format("%s %5d %s%n", s.getKey(), s.getValue(), histogramString);
			}

		} catch (ParseException e) {
			System.err.println(
					"xxx ParseException occured while drawing Histogram xxx \n xxx " + e.getMessage() + " xxx");
		}

	}
}
