package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ThrowVisitor {
    public static void listAllThrowStatements(File projectDir) {
        new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
        	DatabaseManager dbManager = DatabaseManager.getInstance();
        	
        	JavaExceptionNames jen = new JavaExceptionNames();
        	ArrayList<String> javaExceptions = jen.getExceptionNamesFrom("checked_exceptions.txt");
        	javaExceptions.addAll(jen.getExceptionNamesFrom("unchecked_exceptions.txt"));
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ThrowStmt n, Object arg) {
                        super.visit(n, arg);
                        //TODO cast is buggy
                        String exprClass = n.getExpr().getClass().getSimpleName();
                        String className = "";
                        if(exprClass.equals("ObjectCreationExpr")){
                        	//System.out.println(n.getExpr().getChildrenNodes().get(0));
                        	className = n.getExpr().getChildrenNodes().get(0).toString();
                        } else if (exprClass.equals("CastExpr")) {
                        	//System.out.println(n.getExpr().getChildrenNodes().get(0));
                        	className = n.getExpr().getChildrenNodes().get(0).toString();
                        } else if (exprClass.equals("NameExpr")) {	
                        	//System.out.println( n.getExpr().getParentNode().getParentNode().getParentNode().getParentNode());
                        	//for(Node node : n.getExpr().getParentNode().getChildrenNodes())
                        	className = "+++NameExpression";
                        		//System.out.println(n.getExpr());
                        } else if (exprClass.equals("MethodCallExpr")) {
                        	className = "+++MethodCallExpression";
                            
                        } else {
                        	className = "+++UnidentifiedExpression";
                        }
                        
                        dbManager.addThrow(file.getPath(), n.getBegin().line, n.toString(),className, !javaExceptions.contains(className));
                      //  System.out.println("Throw: [L " + n.getBegin().line + "] ");
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (Exception e) {
            	dbManager.addParserException(path, e.toString(), "throws", "");
            }
        }).explore(projectDir);
    }  
}
