package ch.unibe.inf.scg_seminar_exceptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Maintains connection to postgres database and provides methods for writing
 * to the database.
 * <p>
 * Also contains fields representing some commit and related information, which
 * will be used for writing to the database.
 */
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
		try {
			openConnection();
		} catch (SQLException e) {
			Util.logException(e);
		}
	}

	public void openConnection() throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection(Settings.databaseConnection(),
					Settings.databaseUser(), Settings.databasePassword());
		}
	}

	public void closeConnection() throws SQLException {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			Util.log.info("Could not close database connection.");
			Util.logException(e);
		}

	}

	public static synchronized DatabaseManager getInstance() {
		if (dbManagerInstance == null) {
			dbManagerInstance = new DatabaseManager();
			try {
				Class.forName("org.postgresql.Driver");

				dbManagerInstance.createTables();
			} catch (Exception e) {
				// TODO: Handle exception
				Util.logException(e);
			}
		}
		return dbManagerInstance;
	}

	public void addProject() {
		try {
			Statement queryStmt = connection.createStatement();

			ResultSet rs = queryStmt.executeQuery("SELECT id FROM projects WHERE project_name = '" + project + "';");
			while (rs.next()) {
				project_id = rs.getInt("id");
				return;
			}
			PreparedStatement statement = connection.prepareStatement("insert into projects (project_name, parser_version) values(?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, project);
			statement.setString(2, getClass().getPackage().getImplementationVersion());
			statement.execute();

			statement.getGeneratedKeys().next();
			project_id = statement.getGeneratedKeys().getLong(1);
		} catch (Exception e) {
			Util.logException(e);
		}
	}

	/**
	 * Insert the commit currently stured in our private fields into the 'commits' table.
	 */
	public void addCommit() {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into commits (project_id, "
					+ "commit_hash, commit_timestamp, blank_lines, comment_lines, "
					+ "code_lines, project_name) values(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, project_id);
			statement.setString(2, commit);

			statement.setTimestamp(3, new Timestamp((long) Integer.parseInt(timestamp) * 1000));
			statement.setInt(4, blankLines);
			statement.setInt(5, commentLines);
			statement.setInt(6, codeLines);
			statement.setString(7, project);
			statement.execute();
			statement.getGeneratedKeys().next();
			commit_id = statement.getGeneratedKeys().getLong(1);
		} catch (Exception e) {
			Util.logException(e);
		}
	}

	public void addReturnNull(String path, int line, String source) {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into returnnull (commit_id, "
					+ "path, line, source) values(?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setInt(3, line);
			statement.setString(4, source);
			statement.execute();
		} catch (Exception e) {
			Util.logException(e);
		}
	}

	public void addExceptionClass(String path, String name, String type, String source) {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into exception_classes (commit_id, "
					+ "path, name, source,type) values(?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setString(3, name);
			statement.setString(4, source);
			statement.setString(5, type);
			statement.execute();
		} catch (Exception e) {
			Util.logException(e);
		}
	}

	public void addParserException(String path, String stacktrace, String type, String source) {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into parser_exceptions (commit_id, "
					+ "path, stacktrace,type, source) values(?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setString(3, stacktrace);
			statement.setString(4, type);
			statement.setString(5, source);
			statement.execute();
		} catch (Exception e) {
			Util.logException(e);
		}
	}

	public long addMethodThrowsDeclaration(String path, int start_line, int end_line, String source, boolean custom, boolean standard, boolean library, String types) {
		long methodThrowsDeclarationId = 0;
		try {
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

		} catch (Exception e) {
			Util.logException(e);
		}

		return methodThrowsDeclarationId;
	}

	/**
	 * CC: This is never used. Do we need it? Do we need the method_throws_declaration_Types table?
	 * See also addMethodThrowsDeclaration above.
	 *
	 * @param methodThrowsDeclarationId
	 * @param type
	 * @param userdefined
	 */
	public void addMethodThrowsDeclationType(long methodThrowsDeclarationId, String type, boolean userdefined) {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into method_throws_declaration_types"
					+ " (method_throws_declaration_id, type, userdefined) values(?,?,?)");
			statement.setLong(1, methodThrowsDeclarationId);
			statement.setString(2, type);
			statement.setBoolean(3, userdefined);
			statement.execute();
		} catch (Exception e) {
			Util.logException(e);
		}
	}


	public long addTryCatch(String path, int start_line, int end_line, String source, int loc_catch, int loc_finally, boolean custom, boolean standard, boolean library, String types, int catch_count, boolean finally_block) {
		long id = 0;
		try {
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
		} catch (Exception e) {
			Util.logException(e);
		}

		return id;
	}


	public void addThrow(String path, int start_line, String source, String exceptionClass, boolean custom, boolean standard, boolean library, boolean stringArg, String stringLiteral) {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into throws "
					+ "(commit_id, path, start_line, source, exception_class, custom, standard, library, string_arg, string_literal)"
					+ " values(?,?,?,?,?,?,?,?,?,?)");
			statement.setLong(1, commit_id);
			statement.setString(2, path);
			statement.setInt(3, start_line);
			statement.setString(4, source);
			statement.setString(5, exceptionClass);
			statement.setBoolean(6, custom);
			statement.setBoolean(7, standard);
			statement.setBoolean(8, library);
			statement.setBoolean(9, stringArg);
			statement.setString(10, stringLiteral);
			statement.execute();
		} catch (Exception e) {
			Util.logException(e);
		}
	}

	/**
	 * CC: This is never used. Do we need it? What are tcbexceptions?
	 *
	 * @param tcb_id
	 * @param className
	 * @param userdefined
	 */
	public void addTcbException(long tcb_id, String className, boolean userdefined) {
		try {
			PreparedStatement statement = connection.prepareStatement("insert into tcbexceptions "
					+ "(tcb_id, class_name, userdefined)"
					+ " values(?,?,?)");
			statement.setLong(1, tcb_id);
			statement.setString(2, className);
			statement.setBoolean(3, userdefined);
			statement.execute();
		} catch (Exception e) {
			Util.logException(e);
		}
	}

	public void createTables() throws Exception {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30);  // set timeout to 30 sec.

		try {
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
					+ "string_arg BOOLEAN,"
					+ "string_literal TEXT)");

			statement.execute("create table if not exists returnnull ("
					+ "id SERIAL PRIMARY KEY,"
					+ "commit_id INTEGER,"
					+ "path TEXT,"
					+ "line INTEGER,"
					+ "source TEXT)");
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
//			System.err.println(e.getMessage());
			Util.logException(e);
		} finally {
			closeConnection();
		}
	}

	/**
	 * Set data for a line in the 'commits' table. Contains information
	 * related to the project at a given commit and seems to correspond
	 * to a 'cloc' call.
	 *
	 * @param timestamp
	 * @param commit
	 * @param project
	 * @param blankLines
	 * @param commentLines
	 * @param codeLines
	 */
	public void setVersion(String timestamp, String commit, String project, int blankLines, int commentLines, int codeLines) {
		this.commit = commit;
		this.timestamp = timestamp;
		this.project = project;
		this.blankLines = blankLines;
		this.commentLines = commentLines;
		this.codeLines = codeLines;
	}

}
