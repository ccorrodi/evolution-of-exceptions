package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ExceptionClassVisitor {
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
	                        
//	                        Pattern pattern = Pattern.compile(".*Exception", Pattern.DOTALL);
//	                        if(pattern.matcher(n.getName()).matches()){
//	                            System.out.println(n.getName());
//	                        }
	                    }
	                }.visit(JavaParser.parse(file), null);
	            } catch (Exception e) {

	        		e.printStackTrace();
	            	dbManager.addParserException(path, e.toString(), "exception class", "");
	            }
	        }).explore(projectDir);       
	        
	        for (HierarchieEntry entry : classTree) {
	        	try {
	        		List<ClassOrInterfaceType> extendTypes = ((ClassOrInterfaceDeclaration)entry.getNode()).getExtends();
	 
	        		for(ClassOrInterfaceType coit : extendTypes){
			        	classTree.stream().filter(o -> o.getClassName().equals(coit.getName())).forEach(
			        			o -> {
			        				entry.setParent(o);
			        	        }
			        	);
	        		}
	        		
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        		dbManager.addParserException("build tree", e.toString(), "exception class", "");
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
		       			
						dbManager.addExceptionClass(hierarchieEntry.getFile().getPath(), hierarchieEntry.getClassName(), "unchecked", hierarchieEntry.getNode().toString());
		       		
						//System.out.println("Unchecked Exception: " + hierarchieEntry.getClassName());
						break;
		       		} else if( checkedExceptionNames.contains(tmp.getClassName()) ) {
		       			dbManager.addExceptionClass(hierarchieEntry.getFile().getPath(), hierarchieEntry.getClassName(), "checked", hierarchieEntry.getNode().toString());
						
			    		//System.out.println("Checked Exception: " + hierarchieEntry.getClassName());
			    		break;
		       		}
		    	}
		    }	
	    }
}
