package ch.unibe.inf.scg_seminar_exceptions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	
	private static DatabaseManager dbManagerInstance = null;
	private static Connection connection = null;
	private static String timestamp;
	private static String commit;
	private static String project;
	
	public DatabaseManager() {
        // create a database connection
	       
	}
	
	private void openDbConnection() throws SQLException{
		if(connection == null){
	        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/seminar",
	                "seminar", "seminar");
		}
	}
	
	private void closeDbConnection() throws SQLException{
		if(connection != null){
			connection.close();
	        connection = null;
		}
	}
	
	public static synchronized DatabaseManager getInstance() {
        if (dbManagerInstance == null){
        	dbManagerInstance = new DatabaseManager();
        	try {
            	Class.forName("org.postgresql.Driver");

            	dbManagerInstance.createTables();
        	} catch (Exception e) {
				// TODO: handle exception
			}
        }
        return dbManagerInstance;
	}

	public void addObject(DatabaseExceptionEntry entry) {
		

		try {
			openDbConnection();
			
			PreparedStatement statement = connection.prepareStatement("insert into exceptions (project_name, commitHash, timeStamp, class_name, path, type, start_line, end_line, content) values(?,?,?,?,?,?,?,?,?)");
			
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			
			
			statement.setString(1, project);
			statement.setString(2, commit);
			statement.setString(3, timestamp);
			statement.setString(4, entry.getClassName());
			statement.setString(5, entry.getPathToFile());
			statement.setString(6, entry.getType());
			statement.setInt(7, entry.getStart_line());
			statement.setInt(8, entry.getEnd_line());
			statement.setString(9, entry.getContent());
			
			statement.execute();
			

			closeDbConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public void createTables() throws Exception{
   	 // load the sqlite-JDBC driver using the current class loader
    	openDbConnection();
       Statement statement = connection.createStatement();
       statement.setQueryTimeout(30);  // set timeout to 30 sec.
       
       try
       {


    	   statement.execute("create table if not exists projects ("
    	   		+ "id SERIAL PRIMARY KEY,"
    	   		+ "project_name TEXT,"
    	   		+ "folder TEXT,"
    	   		+ "parse_date TIMESTAMP,"
    	   		+ "parser_version TEXT");
    	   
    	   statement.execute("create table if not exists commits ("
    	   		+ "id SERIAL PRIMARY KEY,"
    	   		+ "project_id INTEGER,"
    	   		+ "commit_hash TEXT,"
    	   		+ "commit_timestamp TIMESTAMP");
    	   
    	   statement.execute("create table if not exists exception_classes ("
    	   		+ "commit_id INTEGER,"
    	   		+ "source TEXT,"
    	   		+ "name TEXT,"
    	   		+ "path TEXT,"
    	   		+ "type TEXT");
    	   
    	   statement.execute("create table if not trycatchs ("
    			+ "id SERIAL PRIMARY KEY,"
    	   		+ "commit_id INTEGER,"
    	   		+ "path TEXT,"
    	   		+ "start_line INTEGER,"
    	   		+ "end_line INTEGER,"
    	   		+ "source TEXT");

       }
       catch(SQLException e)
       {
         // if the error message is "out of memory", 
         // it probably means no database file is found
         System.err.println(e.getMessage());
       }
       finally
       {
    	   closeDbConnection();
       }
   }

	public void setVersion(String timestamp, String commit, String project) {
		this.commit = commit;
		this.timestamp = timestamp;
		this.project = project;
	}

}
