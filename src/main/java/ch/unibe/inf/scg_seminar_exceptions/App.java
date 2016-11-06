package ch.unibe.inf.scg_seminar_exceptions;

/**
 */
public class App 
{    
    public static void main( String[] args ) throws Exception
    {      
    	DatabaseManager dbManager = DatabaseManager.getInstance();
    	
    	dbManager.setVersion(args[1], args[2], args[3]);
    	
    	
    /*
        ExceptionVisitor.listAllReturnNullStatements(new File(args[0]));        
    	ExceptionVisitor.listAllTryStatements(new File(args[0]));
    	ExceptionVisitor.listAllThrowStatements(new File(args[0]));
    	ExceptionVisitor.listAllThrows(new File(args[0]));
    	ExceptionVisitor.listClassesDerivedFromException(new File(args[0]));*/
    }
}
