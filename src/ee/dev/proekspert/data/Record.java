package ee.dev.proekspert.data;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Record {
	
	private static final Pattern URI_RECORD_PATTERN = 
			Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s+"			//date
					+ "\\d{2}:\\d{2}:\\d{2},\\d{3}\\s+"			//timestamp
					+ "\\((.+)\\)\\s+"							//thread-id
					+ "\\[[^\\]]*\\]\\s+"						//optional user context
					+ "([\\S])+\\s+"							//URI + query string
					+ "in\\s+\\d+");							//request duration in milliseconds
	private static final Pattern PAYLOAD_RECORD_PATTERN = 
			Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s+"			//date
					+ "\\d{2}:\\d{2}:\\d{2},\\d{3}\\s+"			//timestamp
					+ "\\((.+)\\)\\s+"							//thread-id
					+ "\\[[^\\]]*\\]\\s+"						//optional user context
					+ "(\\w)+(\\s+"								//resource name
					+ "([\\S])+)*\\s+"							//payload elements
					+ "in\\s+\\d+");							//request duration in milliseconds
	
	Date date;
	String resource;
	Long duration;
	
	
	public Record (Date date, String resource, long duration) {
		this.date = date;
		this.resource = resource;
		this.duration = duration;
		
		
		System.out.println("Record created :"
							+ " Date - "+this.date
							+ "; Resource - "+this.resource
							+ "; Duration - "+this.duration
		);
	}

	public static Record getRecord (String logLine) {
		
		Type t = getRecordType(logLine);
		
		System.out.println("Logline matches " + t);		
		
		return new Record(null, "", 0); //TODO new Record(type, string)
				
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
			System.err.println("This string matched with both patterns !?! - " + string);
		
		return null;
	}
	
}
