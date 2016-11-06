package ch.unibe.inf.scg_seminar_exceptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class DatabaseManager {
	
	private static DatabaseManager dbManagerInstance = null;
	private static Connection connection = null;
	private static String timestamp;
	private static String commit;
	private static String project;
	private static long project_id;
	private static long commit_id;
	
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
	
	public void addProject() {
		try {
			openDbConnection();
			
			Statement queryStmt = connection.createStatement();
			
			ResultSet rs = queryStmt.executeQuery("SELECT id FROM projects WHERE project_name = '" + project + "';");
			while(rs.next()) {
				project_id = rs.getInt("id");
				return;
			}
			PreparedStatement statement = connection.prepareStatement("insert into projects (project_name, parser_version) values(?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, project);
			statement.setString(2, getClass().getPackage().getImplementationVersion());
			statement.execute();
			
			statement.getGeneratedKeys().next();
			project_id = statement.getGeneratedKeys().getLong(1);
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addCommit() {
		try {
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into commits (project_id, commit_hash, commit_timestamp) values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, project_id);
			statement.setString(2, commit);
			
			statement.setTimestamp(3, new Timestamp((long)Integer.parseInt(timestamp)*1000));
			statement.execute();
			statement.getGeneratedKeys().next();
			commit_id = statement.getGeneratedKeys().getLong(1);
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addReturnNull(String path, int line, String source) {
		try {
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into returnnull (commit_id, path, line, source) values(?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setInt(3, line);
			statement.setString(4, source);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addExceptionClass(String path, String name, String type, String source) {
		try {
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into exception_classes (commit_id, path, name, source,type) values(?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setString(3, name);
			statement.setString(4, source);
			statement.setString(5, type);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addThrowss(String path, int start_line, int end_line, String source) {
		try {

			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into throwss (commit_id, path, start_line, end_line, source) values(?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setInt(3, start_line);
			statement.setInt(4, end_line);
			statement.setString(5, source);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addTryCatch(String path, int start_line, int end_line, String source) {
		try {
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into trycatchs (commit_id, path, start_line, end_line, source) values(?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setInt(3, start_line);
			statement.setInt(4, end_line);
			statement.setString(5, source);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addThrow(String path, int start_line, String exception_class, String source ) {
		
		try {
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into trycatchs (commit_id, exception_class, path, start_line, source) values(?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, exception_class);
			statement.setString(3, path);
			statement.setInt(4, start_line);
			statement.setString(5, source);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
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
    	   		+ "timestamp TIMESTAMP default current_timestamp,"
    	   		+ "parser_version TEXT)");
    	   
    		statement.execute("create table if not exists commits ("
    	   		+ "id SERIAL PRIMARY KEY,"
    	   		+ "project_id INTEGER,"
    	   		+ "commit_hash TEXT,"
    	   		+ "commit_timestamp TIMESTAMP)");
    	   
    		statement.execute("create table if not exists exception_classes ("
    	   		+ "commit_id INTEGER,"
    	   		+ "source TEXT,"
    	   		+ "name TEXT,"
    	   		+ "path TEXT,"
    	   		+ "type TEXT)");
    	   
    		statement.execute("create table if not exists trycatchs ("
    			+ "id SERIAL PRIMARY KEY,"
    	   		+ "commit_id INTEGER,"
    	   		+ "path TEXT,"
    	   		+ "start_line INTEGER,"
    	   		+ "end_line INTEGER,"
    	   		+ "source TEXT)");
    	   
    		statement.execute("create table if not exists throwss ("
    	   		+ "id SERIAL PRIMARY KEY,"
    	   		+ "exception_class_id INTEGER,"
    	   		+ "commit_id INTEGER,"
    	   		+ "path TEXT,"
    	   		+ "start_line INTEGER,"
    	   		+ "end_line INTEGER,"
    	   		+ "source TEXT)");
    		
    		statement.execute("create table if not exists throws ("
    				+ "id SERIAL PRIMARY KEY,"
    				+ "exception_class TEXT,"
    				+ "path TEXT,"
    				+ "start_line INTEGER,"
    				+ "source TEXT)");
    	   
    		statement.execute("create table if not exists returnnull ("
       	   		+ "id SERIAL PRIMARY KEY,"
       	   		+ "commit_id INTEGER,"
       	   		+ "path TEXT,"
       	   		+ "line INTEGER,"
       	   		+ "source TEXT)");
    	   
    		statement.execute("create table if not exists trycatchsexceptionclasses ("
    	   		+ "trycatch_id INTEGER,"
    	   		+ "exception_class_id INTEGER)");
    	   
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
