package ee.dev.proekspert.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Record {
	
	private static final Pattern URI_RECORD_PATTERN = 
			Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s+"			//date
					+ "\\d{2}:\\d{2}:\\d{2},\\d{3}\\s+"			//time stamp
					+ "\\((.+)\\)\\s+"							//thread-id
					+ "\\[[^\\]]*\\]\\s+"						//optional user context
					+ "([\\S])+\\s+"							//URI + query string
					+ "in\\s+\\d+");							//request duration in milliseconds
	private static final Pattern PAYLOAD_RECORD_PATTERN = 
			Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s+"			//date
					+ "\\d{2}:\\d{2}:\\d{2},\\d{3}\\s+"			//time stamp
					+ "\\((.+)\\)\\s+"							//thread-id
					+ "\\[[^\\]]*\\]\\s+"						//optional user context
					+ "(\\w)+"									//resource name
					+ "(\\s+([\\S])+)*\\s+"						//payload elements
					+ "in\\s+\\d+");							//request duration in milliseconds
	/* used with matcher methods
	private final Pattern DATE_PATTERN =
			Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s+"			//date
					+ "\\d{2}:\\d{2}:\\d{2},\\d{3}");			//timestamp
	*/
	private final Pattern URI_PATTERN = 
			Pattern.compile("([\\S])+");
	
	private final Pattern RESOURCE_PATTERN = 
			Pattern.compile ("(\\w)+");
	/*
	private final Pattern DURATION_PATTERN = 
			Pattern.compile("in\\\\s+\\\\d+");
	*/
	Date date;
	String resource;
	Long duration;
	
	
	public Record (Date date, String resource, long duration) {
		this.date = date;
		this.resource = resource;
		this.duration = duration;

		//System.out.println(">>> Record created :" + this.toString());
	}
	
	private Record (Type t, String recordString) throws ParseException {

		//this.date = parseDateFromString(recordString);
		//this.resource = parseResourceFromString(recordString, t);
		//this.duration = parseDurationFromString(recordString);
		
		//split string
		String[] recordData = recordString.split("\\s+");
		/*
		System.out.print("Split record String -> " + splitString.length + " ");
		for (String s : splitString)
			System.out.print(s+"/");
		System.out.println(" ");
		*/
		
		this.date = sdf.parse (recordData[0]+" "+recordData[1]);
		if (t == Type.URIQUERY && recordData.length == 7) {
			Matcher matcher = URI_PATTERN.matcher(recordData[4]);
			if (matcher.find())
				this.resource = recordData[4].split("\\?")[0];
			else
				System.err.println("xxx Record attribute 'resource' not set xxx.\nxxx Line - "+recordString+" xxx");
			this.duration = Long.parseLong(recordData[6]);
		}
		
		else if (t == Type.PAYLOAD && recordData.length >= 7) {
			Matcher matcher = RESOURCE_PATTERN.matcher(recordData[4]);
			if (matcher.find())
				this.resource = recordData[4];
			else
				System.err.println("xxx Record attribute 'resource' not set xxx.\nxxx Line - "+recordString+" xxx");
			this.duration = Long.parseLong(recordData[recordData.length-1]); // duration will be last
		}
		else
			System.err.println("xxx Not all record attributes set xxx.\nxxx Line - "+recordString+" xxx");
		
		//System.out.println(">>> Record created :" + this.toString());
		
	}

	SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss,SSS");
	/* matcher methods 
	Date parseDateFromString (String s) throws ParseException {
		Matcher matcher = DATE_PATTERN.matcher (s);
		String dateString = "";
		if (matcher.find())
			dateString = matcher.group();
		
		return sdf.parse(dateString);
	}
	
	//!!! Doesn't work
	private Long parseDurationFromString(String s) {
		Matcher matcher = DURATION_PATTERN.matcher(s);
		String durationString = "";
		if (matcher.find())
			durationString = matcher.group();
		if (durationString.length() > 3)
			return Long.parseLong(durationString.substring(3)); // duration starts after 'in '
		
		return (long) 0;
		
	}

	private String parseResourceFromString(String s, Type t) {
		if (t == Type.PAYLOAD) {
			Matcher matcher = RESOURCE_PATTERN.matcher(s);
			if (matcher.find())
				return matcher.group();
			
		}
		else if (t == Type.URIQUERY) {
			Matcher matcher = URI_PATTERN.matcher(s);
			if (matcher.find())
				return matcher.group().split("\\?")[0];
		}
		return "N/A";
	}
	*/
	public static Record getRecord (String logLine) {
		
		Type t = getRecordType(logLine);
		
		//System.out.println(">>> Logline matches " + t);		
		
		try {
			return new Record(t, logLine);
		} catch (ParseException e) {
			System.err.println("xxx Record could not be created. Line - "+logLine+" xxx");
			e.printStackTrace();
			return null;
		}
				
	}
	
	private enum Type {
		URIQUERY, PAYLOAD
	}
	
	private static Type getRecordType (String string) {
		Matcher matcher = URI_RECORD_PATTERN.matcher(string);
		int matches = 0;
		if (matcher.find())
			matches ++;
		matcher = PAYLOAD_RECORD_PATTERN.matcher(string);
		if (matcher.find())
			matches +=2;
		
		if (matches == 1)
			return Type.URIQUERY;
		if (matches == 2)
			return Type.PAYLOAD;
		if (matches == 3)
			System.err.println("xxx This string matched with both patterns !?! - " + string+" xxx");
		
		return null;
	}
	
	SimpleDateFormat sdfPrint = new SimpleDateFormat("dd-MM-yyyy HH:00");
	
	@Override
	public String toString() {
		String s = 
				" Date - "+sdfPrint.format(date)
				+ "; Resource - "+resource
				+ "; Duration - "+duration;
		return s;
	}
	
}
