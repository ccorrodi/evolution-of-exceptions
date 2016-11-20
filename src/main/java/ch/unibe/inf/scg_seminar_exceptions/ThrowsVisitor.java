package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ThrowsVisitor {
	 public static void listAllThrows(File projectDir) {
	        new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {

	        	DatabaseManager dbManager = DatabaseManager.getInstance();
	        	
	        	JavaExceptionNames jen = new JavaExceptionNames();
	        	ArrayList<String> javaExceptions = jen.getExceptionNamesFrom("checked_exceptions.txt");
	        	javaExceptions.addAll(jen.getExceptionNamesFrom("unchecked_exceptions.txt"));
	        	
	            try {
	                new VoidVisitorAdapter<Object>() {
	                    @Override
	                    public void visit(MethodDeclaration n, Object arg) {
	                        super.visit(n, arg);
	                        long methodThrowsDeclarationId = dbManager.addMethodThrowsDeclaration(file.getPath(), 
																	n.getBegin().line, n.getEnd().line, n.toString());
	                        
	                        for(ReferenceType methodThrows : n.getThrows()){   	
	                        	
	                        	dbManager.addMethodThrowsDeclationType(methodThrowsDeclarationId, 
	                        			methodThrows.toString(), !javaExceptions.contains(methodThrows.toString()));
	                        }

	                    }
	                }.visit(JavaParser.parse(file), null);
	            } catch (Exception e) {
	            	dbManager.addParserException(path, e.toString(), "throws", "");
	            }
	        }).explore(projectDir);
	    }
}
