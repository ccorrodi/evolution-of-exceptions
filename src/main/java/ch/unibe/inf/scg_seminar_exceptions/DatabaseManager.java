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
	private String timestamp;
	private String commit;
	private String project;
	private long project_id;
	private long commit_id;
	private int blankLines;
	private int commentLines;
	private int codeLines;
	
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
			PreparedStatement statement = connection.prepareStatement("insert into commits (project_id, "
					+ "commit_hash, commit_timestamp, blank_lines, comment_lines, "
					+ "code_lines, project_name) values(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, project_id);
			statement.setString(2, commit);
			
			statement.setTimestamp(3, new Timestamp((long)Integer.parseInt(timestamp)*1000));
			statement.setInt(4, blankLines);
			statement.setInt(5, commentLines);
			statement.setInt(6, codeLines);
			statement.setString(7, project);
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
			PreparedStatement statement = connection.prepareStatement("insert into returnnull (commit_id, "
					+ "path, line, source) values(?,?,?,?)");
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
			PreparedStatement statement = connection.prepareStatement("insert into exception_classes (commit_id, "
					+ "path, name, source,type) values(?,?,?,?,?)");
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
	
	public void addParserException(String path, String stacktrace, String type, String source) {
		try {
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into parser_exceptions (commit_id, "
					+ "path, stacktrace,type, source) values(?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setString(3, stacktrace);
			statement.setString(4, type);
			statement.setString(5, source);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public long addMethodThrowsDeclaration(String path, int start_line, int end_line, String source, boolean custom, boolean standard, boolean library, String types) {
		long methodThrowsDeclarationId = 0;
		try {

			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into method_throws_declarations "
					+ "(commit_id, path, start_line, end_line, source, custom, standard, library, types) values(?,?,?,?,?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setInt(3, start_line);
			statement.setInt(4, end_line);
			statement.setString(5, source);
			statement.setBoolean(6, custom);
			statement.setBoolean(7, standard);
			statement.setBoolean(8, library);
			statement.setString(9, types);
			
			statement.execute();
//			
//			statement.getGeneratedKeys().next();
//			methodThrowsDeclarationId = statement.getGeneratedKeys().getLong(1);
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return methodThrowsDeclarationId;
	}
	
	public void addMethodThrowsDeclationType(long methodThrowsDeclarationId, String type, boolean userdefined) {
		try {

			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into method_throws_declaration_types"
					+ " (method_throws_declaration_id, type, userdefined) values(?,?,?)");
			statement.setLong(1, methodThrowsDeclarationId);
			statement.setString(2, type);
			statement.setBoolean(3, userdefined);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public long addTryCatch(String path, int start_line, int end_line, String source, int loc_catch, int loc_finally, boolean custom, boolean standard, boolean library, String types, int catch_count, boolean finally_block) {
		long id = 0;
		try {    
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into trycatchs (commit_id, "
					+ "path, start_line, end_line, source, loc_catch, loc_finally, custom, standard, library,types, catch_count, finally_block) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setInt(3, start_line);
			statement.setInt(4, end_line);
			statement.setString(5, source);
			statement.setInt(6, loc_catch);
			statement.setInt(7, loc_finally);
			statement.setBoolean(8, custom);
			statement.setBoolean(9, standard);
			statement.setBoolean(10, library);
			statement.setString(11, types);
			statement.setInt(12, catch_count);
			statement.setBoolean(13, finally_block);
			statement.execute();
			closeDbConnection();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return id;
	}
	
	public void addThrow(String path, int start_line, String source, String exceptionClass, boolean custom, boolean standard, boolean library, boolean stringArg) {
		
		try {
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into throws "
					+ "(commit_id, path, start_line, source, exception_class, custom, standard, library, string_arg)"
					+ " values(?,?,?,?,?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setInt(3, start_line);
			statement.setString(4, source);
			statement.setString(5, exceptionClass);
			statement.setBoolean(6, custom);
			statement.setBoolean(7, standard);
			statement.setBoolean(8, library);
			statement.setBoolean(9, stringArg);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addTcbException(long tcb_id, String className, boolean userdefined) {
		try {
			openDbConnection();
			PreparedStatement statement = connection.prepareStatement("insert into tcbexceptions "
					+ "(tcb_id, class_name, userdefined)"
					+ " values(?,?,?)");
			statement.setLong(1, tcb_id);
			statement.setString(2, className);
			statement.setBoolean(3, userdefined);
			statement.execute();
			
			closeDbConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createTables() throws Exception{
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

    		statement.execute("create table if not exists parser_exceptions ("
        	   	+ "commit_id INTEGER,"
        	   	+ "path TEXT,"
        	   	+ "stacktrace TEXT,"
        	   	+ "type TEXT,"
        	   	+ "source TEXT)");
    	   
//    		statement.execute("create table if not exists tcbexceptions ("
//    			+ "id SERIAL PRIMARY KEY,"
//    			+ "tcb_id INTEGER,"
//    			+ "class_name TEXT,"
//    			+ "userdefined BOOLEAN)");
    		
    		statement.execute("create table if not exists commits ("
    	   		+ "id SERIAL PRIMARY KEY,"
    	   		+ "project_id INTEGER,"
    	   		+ "commit_hash TEXT,"
    	   		+ "commit_timestamp TIMESTAMP, "
    	   		+ "blank_lines INTEGER,"
    	   		+ "comment_lines INTEGER,"
    	   		+ "code_lines INTEGER,"
    	   		+ "project_name TEXT)");
    	   
    		statement.execute("create table if not exists exception_classes ("
    			+ "id SERIAL PRIMARY KEY,"
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
    	   		+ "source TEXT,"
    	   		+ "loc_catch INTEGER,"
    	   		+ "loc_finally INTEGER,"
    	   		+ "custom BOOLEAN,"
    	   		+ "standard BOOLEAN,"
    	   		+ "library BOOLEAN,"
    	   		+ "types TEXT,"
    	   		+ "catch_count INTEGER,"
    	   		+ "finally_block BOOLEAN)");
    	   
    		statement.execute("create table if not exists method_throws_declarations ("
    	   		+ "id SERIAL PRIMARY KEY,"
    	   		+ "commit_id INTEGER,"
    	   		+ "path TEXT,"
    	   		+ "start_line INTEGER,"
    	   		+ "end_line INTEGER,"
    	   		+ "source TEXT,"
    	   		+ "custom BOOLEAN,"
    	   		+ "standard BOOLEAN,"
    	   		+ "library BOOLEAN,"
    	   		+ "types TEXT)");
    		
//    		statement.execute("create table if not exists method_throws_declaration_types ("
//    				+ "id SERIAL PRIMARY KEY,"
//    				+ "method_throws_declaration_id INTEGER,"
//    				+ "type TEXT,"
//    				+ "userdefined BOOLEAN)");
    		
    		statement.execute("create table if not exists throws ("
    			+ "id SERIAL PRIMARY KEY,"
    			+ "commit_id INTEGER,"
    			+ "exception_class TEXT,"
    			+ "path TEXT,"
    			+ "start_line INTEGER,"
    			+ "source TEXT,"
    			+ "custom BOOLEAN,"
    			+ "standard BOOLEAN,"
    			+ "library BOOLEAN,"
    			+ "string_arg BOOLEAN)");
    	   
    		statement.execute("create table if not exists returnnull ("
       	   		+ "id SERIAL PRIMARY KEY,"
       	   		+ "commit_id INTEGER,"
       	   		+ "path TEXT,"
       	   		+ "line INTEGER,"
       	   		+ "source TEXT)");
    	   
    		/*statement.execute("create table if not exists catch_types ("
    	   		+ "trycatch_id INTEGER,"
    	   		+ "exception_class_id INTEGER)");*/
    	   
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

	public void setVersion(String timestamp, String commit, String project, String blankLines, String commentLines, String codeLines ) {
		this.commit = commit;
		this.timestamp = timestamp;
		this.project = project;
		this.blankLines = Integer.parseInt(blankLines);
		this.commentLines = Integer.parseInt(commentLines);
		this.codeLines = Integer.parseInt(codeLines);
	}

}
