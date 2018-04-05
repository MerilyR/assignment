package ee.dev.proekspert.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LogParser {

	public static void parseLog(String logfile, int top) {
		
		BufferedReader fileBuffer = null;
		
		try {
			fileBuffer = Files.newBufferedReader(Paths.get(logfile));
		} catch (IOException e) {
			System.err.println("File not found: "+e.getMessage());
		}
		
		try {
			for (int i = 0; i < top; i++) {
				System.out.print(i+1+". line: ");
				
				if (fileBuffer != null)
					System.out.println(fileBuffer.readLine());
			}
		} catch (IOException e) {
			System.err.println("Not able to read line: "+e.getMessage());
		}
		
	}

}
