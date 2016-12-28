package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TryCatchVisitor {
	public static void listAllTryStatements(File projectDir, ArrayList<ExceptionClass> exceptionClasses) {
		new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
			DatabaseManager dbManager = DatabaseManager.getInstance();

			try {
				new VoidVisitorAdapter<Object>() {
					@Override
					public void visit(TryStmt n, Object arg) {
						super.visit(n, arg);

						int loc_finally = 0;

						boolean custom = false;
						boolean standard = false;
						boolean library = false;
						boolean finally_block = false;
						Scanner scanner;
						String types = "";

						// get finally
						if (n.getFinallyBlock() != null) {
							finally_block = true;
							scanner = new Scanner(n.getFinallyBlock().toStringWithoutComments());
							while (scanner.hasNextLine()) {
								String line = scanner.nextLine();
								if (line.contains(")") || (line.contains("{") && line.contains(")")) || line.contains(";")) {
									loc_finally++;
								}
							}
						}

						// get the catch clause
						List<CatchClause> catchClauses = n.getCatchs();

						// get the type of the caught exceptions
						for (CatchClause cc : catchClauses) {
							int loc_catch = 0;
							int loc_counter = 0;
							types = "";
							scanner = new Scanner(cc.toStringWithoutComments());
							while (scanner.hasNextLine()) {
								String line = scanner.nextLine();
								if (line.contains(")") || (line.contains("{") && line.contains(")")) || line.contains(";")) {
									loc_counter++;
								}
							}

							scanner.close();

							loc_catch += loc_counter - 1;
							String type = cc.getParam().getType().toStringWithoutComments();

							// remove packet
							String[] typePath = type.split("\\.");
							if (typePath.length > 0) {
								type = typePath[typePath.length - 1];
							}

							types += type + ",";

							for (ExceptionClass ec : exceptionClasses) {
								if (type.equals(ec.getName())) {
									if (ec.getScope() == Scope.CUSTOM) {
										custom = true;
									} else if (ec.getScope() == Scope.STANDARD) {
										standard = true;
									}
								}
							}
							if (!custom && !standard) {
								library = true;
							}

							// add tc block to db
							dbManager.addTryCatch(file.getPath(), n.getBegin().line, n.getEnd().line,
									n.toString().replaceAll("\0", ""), loc_catch, loc_finally, custom, standard, library, types,
									catchClauses.size(), finally_block);
							loc_finally = 0;
							finally_block = false;
							custom = false;
							standard = false;
							library = false;
						}
						if (catchClauses.size() == 0) {
							dbManager.addTryCatch(file.getPath(), n.getBegin().line, n.getEnd().line,
									n.toString().replaceAll("\0", ""), 0, loc_finally, custom, standard, library, types,
									catchClauses.size(), finally_block);
						}

					}
				}.visit(JavaParser.parse(file), null);
			} catch (Exception e) {
				dbManager.addParserException(path, e.toString(), "try catch", "");
				e.printStackTrace();
			}
		}).explore(projectDir);
	}
}
