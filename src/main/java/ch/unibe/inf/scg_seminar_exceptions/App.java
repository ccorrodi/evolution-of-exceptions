package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Takes some command-line arguments corresponding to a single commit.
 * Adds the project (if necessary) and commit to the database and
 * uses the various visitors to insert more data.
 */
public class App {
	public static void main(String[] args) {
		try {
			//path timestamp commithash foldername blanklines commentlines codelines
			String path = args[0], timestamp = args[1], commitHash = args[2], foldername = args[3];
			int blankLines = Integer.parseInt(args[4]),
					commentLines = Integer.parseInt(args[5]),
					codeLines = Integer.parseInt(args[6]);

			Util.initialize(foldername);
			Util.log.info("Started logging at: " + Util.timestampAsString());

			DatabaseManager dbManager = DatabaseManager.getInstance();

			dbManager.setVersion(path, timestamp, commitHash, blankLines, commentLines, codeLines);

			dbManager.addProject();
			dbManager.addCommit();

			ArrayList<ExceptionClass> exceptionClasses = ExceptionClassVisitor.listClassesDerivedFromException(new File(args[0]));
			ThrowsVisitor.listAllThrows(new File(args[0]), exceptionClasses);
			TryCatchVisitor.listAllTryStatements(new File(args[0]), exceptionClasses);
			ThrowVisitor.listAllThrowStatements(new File(args[0]), exceptionClasses);
		} catch (Exception e) {
			Util.log.log(Level.SEVERE, "Caught exception: ", e);
		} finally {
			Util.log.info("Finished at: " + Util.timestampAsString());
		}

	}
}
