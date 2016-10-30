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

            	dbManagerInstance.createTable();
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
	
    public void createTable() throws Exception{
   	 // load the sqlite-JDBC driver using the current class loader
    	openDbConnection();
       Statement statement = connection.createStatement();
       statement.setQueryTimeout(30);  // set timeout to 30 sec.
       
       try
       {


         //statement.executeUpdate("drop table if exists exceptions");
         statement.executeUpdate("create table if not exists exceptions (" 
         		+ "project_name TEXT,"
        		+ "commitHash TEXT,"
         		+ "timeStamp TEXT,"
         		+ "class_name TEXT,"
        		+ "path TEXT,"
         		+ "type TEXT,"
         		+ "start_line INTEGER,"
         		+ "end_line INTEGER,"
        		+ "content TEXT)");
//         statement.executeUpdate("insert into person values(1, 'leo')");
//         statement.executeUpdate("insert into person values(2, 'yui')");
//         ResultSet rs = statement.executeQuery("select * from exeptions");
//         while(rs.next())
//         {
//           // read the result set
//           System.out.println("name = " + rs.getString("name"));
//           System.out.println("id = " + rs.getInt("id"));
//         }
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
