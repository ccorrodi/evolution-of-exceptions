package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ThrowsVisitor {
	 public static void listAllThrows(File projectDir) {
	        new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {

	        	DatabaseManager dbManager = DatabaseManager.getInstance();
	            try {
	                new VoidVisitorAdapter<Object>() {
	                    @Override
	                    public void visit(MethodDeclaration n, Object arg) {
	                        super.visit(n, arg);
	                        Pattern pattern = Pattern.compile(".*throws.*", Pattern.DOTALL);
	                        if(pattern.matcher(n.toString()).matches()){
	     
	        					dbManager.addThrows(file.getPath(), n.getBegin().line, n.getEnd().line, n.toString());
	                            //System.out.println("Throws: " + n.getName());
	                        }
	                    }
	                }.visit(JavaParser.parse(file), null);
	            } catch (Exception e) {
	            	dbManager.addParserException(path, e.toString(), "throws", "");
	            }
	        }).explore(projectDir);
	    }
}
