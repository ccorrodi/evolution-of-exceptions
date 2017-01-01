package ch.unibe.inf.scg_seminar_exceptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JavaExceptionNames {

	public ArrayList<String> getExceptionNamesFrom(String fileName) {
		ArrayList<String> exceptionNames = new ArrayList<String>();

		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);


		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				exceptionNames.add(line);
			}
			br.close();
		} catch (IOException e) {
			Util.logException(e);
		}

		return exceptionNames;
	}
}
