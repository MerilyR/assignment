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

	private final Pattern URI_PATTERN = 
			Pattern.compile("([\\S])+");	
	private final Pattern RESOURCE_PATTERN = 
			Pattern.compile ("(\\w)+");

	private Date date;
	private String resource;
	private Long duration;

	public Record (Date date, String resource, long duration) {
		
		this.date = date;
		this.resource = resource;
		this.duration = duration;

		//System.out.println(">>> Record created :" + this.toString());
		
	}
	
	private Record (Type t, String recordString) throws ParseException {

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
				throw new ParseException("Record attribute 'resource' not set . Line - "+recordString, 5);
			
			this.duration = Long.parseLong(recordData[6]);
			
		} else if (t == Type.PAYLOAD && recordData.length >= 7) {
			
			Matcher matcher = RESOURCE_PATTERN.matcher(recordData[4]);
			if (matcher.find())
				this.resource = recordData[4];
			else
				throw new ParseException("Record attribute 'resource' not set . Line - "+recordString, 5);
			
			this.duration = Long.parseLong(recordData[recordData.length-1]); // duration should be last
			
		} else
			
			throw new ParseException("Not all record attributes set xxx.\n Line - "+recordString, 5);
		
		//System.out.println(">>> Record created :" + this.toString());
		
	}

	SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss,SSS");

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
	
	private enum Type { URIQUERY, PAYLOAD }
	
	private static Type getRecordType (String record) {
		
		Matcher matcher = URI_RECORD_PATTERN.matcher(record);
		int matches = 0;
		if (matcher.find())
			matches ++;
		matcher = PAYLOAD_RECORD_PATTERN.matcher(record);
		if (matcher.find())
			matches +=2;
		
		switch (matches) {
			case 1:
				return Type.URIQUERY;
			case 2:
				return Type.PAYLOAD;
			case 3:
				System.err.println("xxx This record matched with both patterns !?! - " + record + " xxx");
				return null;
			default:
				System.err.println("xxx This record didn't match either of predefined patterns ?!? - " + record + " xxx");
				return null;
		}
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
