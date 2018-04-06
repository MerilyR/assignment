package ee.dev.proekspert.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import ee.dev.proekspert.data.Record;

public class LogParser {

	public static void parseLog(String logfile, int top) {
		
		BufferedReader fileBuffer = null;
		
		try {
			fileBuffer = Files.newBufferedReader(Paths.get(logfile));
		} catch (IOException e) {
			System.err.println("File not found: "+e.getMessage());
		}
		
		ArrayList<Record> records = new ArrayList<Record>();
		
		try {
			
			//TODO: remove temporarily ignore the first 'top' lines (for testing different lines)
			System.out.println("Skipping "+top+" first lines (for testing)");
			for (int i = 0; i < top; i++) {
				fileBuffer.readLine();
			}
			
			for (int i = top; i < top*2; i++) { //TODO i = 0; i < top; i++
				System.out.print((i+1)+". line: ");
				String line;
				if (fileBuffer != null && (line = fileBuffer.readLine()) != null) {
					System.out.println(line);
					records.add(Record.getRecord(line));
				}
			}
			fileBuffer.close();
		} catch (IOException e) {
			System.err.println("Not able to read line: "+e.getMessage());
		}
		
		
		
	}

}
