package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ExceptionVisitor {

	public ExceptionVisitor() {
		// TODO Auto-generated constructor stub
	}
    
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
        	       			DatabaseExceptionEntry entry = new DatabaseExceptionEntry(projectDir.getName(), n.getName(), "not implemented", "throws", file.getPath(), n.getBegin().line,
                        			n.getEnd().line, n.toString());
        					dbManager.addObject(entry);
                            System.out.println("Throws: " + n.getName());
                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (Exception e) {
            	System.out.println(e);
            }
        }).explore(projectDir);
    }
    
    public static void listClassesDerivedFromException(File projectDir) {
    	JavaExceptionNames jen = new JavaExceptionNames();
    	ArrayList<String> checkedExceptionNames = jen.getExceptionNamesFrom("checked_exceptions.txt");
    	ArrayList<String> uncheckedExceptionNames = jen.getExceptionNamesFrom("unchecked_exceptions.txt");
    	
    	ArrayList<HierarchieEntry> classTree = new ArrayList<HierarchieEntry>();
    	DatabaseManager dbManager = DatabaseManager.getInstance();
    	
        new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        
                        classTree.add(new HierarchieEntry(n,file));
                        
//                        Pattern pattern = Pattern.compile(".*Exception", Pattern.DOTALL);
//                        if(pattern.matcher(n.getName()).matches()){
//                            System.out.println(n.getName());
//                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (Exception e) {
            }
        }).explore(projectDir);       
        
        for (HierarchieEntry entry : classTree) {
        	try {
        		String parentClassName = ((ClassOrInterfaceDeclaration)entry.getNode()).getExtends().get(0).getName();

        		classTree.stream().filter(o -> o.getClassName().equals(parentClassName)).forEach(
        	            o -> {
        	            	entry.setParent(o);
        	            }
        	    );

        	} catch (Exception e) {
        		
        	}
        }
	    for (HierarchieEntry hierarchieEntry : classTree) {
	    	HierarchieEntry tmp = hierarchieEntry;
	    	ArrayList<HierarchieEntry> pathHistory = new ArrayList<>();

	    	while(tmp.hasNext()){
	    		if(pathHistory.contains(tmp)){
	    			break; // loop detected
	    		} else {
	    			pathHistory.add(tmp);
	    		}
	    		tmp = tmp.next();
	       		if(uncheckedExceptionNames.contains(tmp.getClassName())){
	       			
	       			DatabaseExceptionEntry entry = new DatabaseExceptionEntry(projectDir.getName(), "not implemented", "unchecked exception class", hierarchieEntry);
					dbManager.addObject(entry);
	       		
					System.out.println("Unchecked Exception: " + hierarchieEntry.getClassName());
					break;
	       		} else if( checkedExceptionNames.contains(tmp.getClassName()) ) {
	       			
	       			DatabaseExceptionEntry entry = new DatabaseExceptionEntry(projectDir.getName(), "not implemented", "checked exception class", hierarchieEntry);
					dbManager.addObject(entry);
					
		    		System.out.println("Checked Exception: " + hierarchieEntry.getClassName());
		    		break;
	       		}
	    	}
	    }	
    }
    
    public static void listAllTryStatements(File projectDir) {
        new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
        	DatabaseManager dbManager = DatabaseManager.getInstance();
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(TryStmt n, Object arg) {
                        super.visit(n, arg);
                        
    	       			DatabaseExceptionEntry entry = new DatabaseExceptionEntry(projectDir.getName(), "", "not implemented", "try-catch-block", file.getPath(), n.getBegin().line,
                    			n.getEnd().line, n.toString());
    					dbManager.addObject(entry);
                        
                        System.out.println("Try-catch: [L " + n.getBegin().line + "] ");
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (Exception e) {
            	System.out.println(e);
            }
        }).explore(projectDir);
    }
    
    public static void listAllReturnNullStatements(File projectDir) {
        new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
        	DatabaseManager dbManager = DatabaseManager.getInstance();
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ReturnStmt n, Object arg) {
                        super.visit(n, arg);
                        Pattern pattern = Pattern.compile(".*null;", Pattern.DOTALL);
                        if(pattern.matcher(n.toString()).matches()){
        	       			DatabaseExceptionEntry entry = new DatabaseExceptionEntry(projectDir.getName(), "", "not implemented", "return null", file.getPath(), n.getBegin().line,
                        			n.getEnd().line, n.toString());
        					dbManager.addObject(entry);
        					
                            System.out.println("Return null: [L " + n.getBegin().line + "] " );
                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (Exception e) {
            	System.out.println(e);
            }
        }).explore(projectDir);
    }
    
    public static void listAllThrowStatements(File projectDir) {
        new FileExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
        	DatabaseManager dbManager = DatabaseManager.getInstance();
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ThrowStmt n, Object arg) {
                        super.visit(n, arg);
    	       			DatabaseExceptionEntry entry = new DatabaseExceptionEntry(projectDir.getName(), "", "not implemented", "throws", file.getPath(), n.getBegin().line,
                    			n.getEnd().line, n.toString());
    					dbManager.addObject(entry);
                        System.out.println("Throw: [L " + n.getBegin().line + "] ");
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (Exception e) {
            	System.out.println(e);
            }
        }).explore(projectDir);
    }  

}
