package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TryCatchVisitor {
    public static void listAllTryStatements(File projectDir) {
        new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
        	DatabaseManager dbManager = DatabaseManager.getInstance();
        	
        	JavaExceptionNames jen = new JavaExceptionNames();
        	ArrayList<String> javaExceptions = jen.getExceptionNamesFrom("checked_exceptions.txt");
        	javaExceptions.addAll(jen.getExceptionNamesFrom("unchecked_exceptions.txt"));
        	
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(TryStmt n, Object arg) {
                        super.visit(n, arg);
                        
                        // add tc block to db
    					long tcb_id = dbManager.addTryCatch(file.getPath(), n.getBegin().line, n.getEnd().line, n.toString());
    					
                        // get the catch clause
                        List<CatchClause> catchClauses =  n.getCatchs();
                        
                        // get the type of the caught exceptions
                        for(CatchClause cc : catchClauses) {
                        	String className = cc.getParam().getType().toStringWithoutComments();
                        	dbManager.addTcbException(tcb_id, className, !javaExceptions.contains(className));
                        }
                        
                       // System.out.println("Try-catch: [L " + n.getBegin().line + "] ");
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (Exception e) {
            	dbManager.addParserException(path, e.toString(), "try catch", "");
            }
        }).explore(projectDir);
    }
}
