package ch.unibe.inf.scg_seminar_exceptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	
	private static DatabaseManager dbManagerInstance = null;
	private static Connection connection = null;
	
	public DatabaseManager() {
        // create a database connection
	       
	}
	
	private void openDbConnection() throws SQLException{
		if(connection == null){
	        connection = DriverManager.getConnection("jdbc:sqlite:exceptions.db");
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
            	Class.forName("org.sqlite.JDBC");

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
			
			PreparedStatement statement = connection.prepareStatement("insert into exceptions (project_name, version, class_name, path, type, start_line, end_line, content) values(?,?,?,?,?,?,?,?)");
			
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			
			
			statement.setString(1, entry.getProjectName());
			statement.setString(2, entry.getVersion());
			statement.setString(3, entry.getClassName());
			statement.setString(4, entry.getPathToFile());
			statement.setString(5, entry.getType());
			statement.setInt(6, entry.getStart_line());
			statement.setInt(7, entry.getEnd_line());
			statement.setString(8, entry.getContent());
			
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


         statement.executeUpdate("drop table if exists exceptions");
         statement.executeUpdate("create table exceptions (" 
         		+ "project_name STRING,"
        		+ "version STRING,"
         		+ "class_name STRING,"
        		+ "path STRING,"
         		+ "type STRING,"
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

}
