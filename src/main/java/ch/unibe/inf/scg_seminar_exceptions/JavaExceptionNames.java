package ch.unibe.inf.scg_seminar_exceptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JavaExceptionNames {
	
	public ArrayList<String> getExceptionNamesFrom(String fileName) {
		ArrayList<String> exceptionNames = new ArrayList<String>();
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

	    try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = br.readLine()) != null) {
		       exceptionNames.add(line);
		    }
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return exceptionNames; 
	}
	
}
