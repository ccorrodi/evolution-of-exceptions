package ch.unibe.inf.scg_seminar_exceptions;

import java.io.File;

/**
 */
public class App 
{    
    public static void main( String[] args ) throws Exception
    {       
    	
        ExceptionVisitor.listAllReturnNullStatements(new File(args[0]));        
    	ExceptionVisitor.listAllTryStatements(new File(args[0]));
    	ExceptionVisitor.listAllThrowStatements(new File(args[0]));
        ExceptionVisitor.listClassesDerivedFromException(new File(args[0]));
        

    	ExceptionVisitor.listAllThrows(new File(args[0]));
    }
}
