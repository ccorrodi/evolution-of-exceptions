package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class ThrowsVisitor {
	public static void listAllThrows(File projectDir, ArrayList<ExceptionClass> exceptionClasses) {
		new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {

			DatabaseManager dbManager = DatabaseManager.getInstance();

			try {
				new VoidVisitorAdapter<Object>() {
					@Override
					public void visit(MethodDeclaration n, Object arg) {
						super.visit(n, arg);
						boolean custom = false;
						boolean standard = false;
						boolean library = false;

						String types = "";

						for (ReferenceType methodThrows : n.getThrows()) {

							String type = methodThrows.getType().toString();
							String[] typePath = type.split("\\.");
							if (typePath.length > 0) {
								type = typePath[typePath.length - 1];
							}
							types += type + ", ";
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
						}

						dbManager.addMethodThrowsDeclaration(file.getPath(),
								n.getBegin().line, n.getEnd().line, n.toString().replaceAll("\0", ""), custom, standard, library, types);

	                       /* for(ReferenceType methodThrows : n.getThrows()){   	

	                        	dbManager.addMethodThrowsDeclationType(methodThrowsDeclarationId, 
	                        			methodThrows.toString(), !javaExceptions.contains(methodThrows.toString()));
	                        } */

					}
				}.visit(JavaParser.parse(file), null);
			} catch (Exception e) {
				Util.logException(e);
				dbManager.addParserException(path, e.toString(), "throws", "");
			}
		}).explore(projectDir);
	}
}
