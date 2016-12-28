package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ReturnNullVisitor {
	public static void listAllReturnNullStatements(File projectDir) {
		new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
			DatabaseManager dbManager = DatabaseManager.getInstance();
			try {
				new VoidVisitorAdapter<Object>() {
					@Override
					public void visit(ReturnStmt n, Object arg) {
						super.visit(n, arg);
						Pattern pattern = Pattern.compile(".*null;", Pattern.DOTALL);
						if (pattern.matcher(n.toString()).matches()) {

							dbManager.addReturnNull(file.getPath(), n.getBegin().line, n.toString());

							// System.out.println("Return null: [L " + n.getBegin().line + "] " );
						}
					}
				}.visit(JavaParser.parse(file), null);
			} catch (Exception e) {
				dbManager.addParserException(path, e.toString(), "return null", "");
			}
		}).explore(projectDir);
	}

}
