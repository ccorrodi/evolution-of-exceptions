package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;
import java.util.ArrayList;

/**
 */
public class App 
{    
    public static void main( String[] args )
    {      
    	DatabaseManager dbManager = DatabaseManager.getInstance();
    	
		//path timestamp commithash foldername blanklines commentlines codelines

    	dbManager.setVersion(args[1], args[2], args[3], args[4], args[5], args[6]);

    	dbManager.addProject();
    	dbManager.addCommit();
    	
    	ArrayList<ExceptionClass> exceptionClasses = ExceptionClassVisitor.listClassesDerivedFromException(new File(args[0]));
    	ThrowsVisitor.listAllThrows(new File(args[0]), exceptionClasses);
    	TryCatchVisitor.listAllTryStatements(new File(args[0]), exceptionClasses);
    	ThrowVisitor.listAllThrowStatements(new File(args[0]), exceptionClasses);
    	
//      ReturnNullVisitor.listAllReturnNullStatements(new File(args[0]));        
//    
    }
}
