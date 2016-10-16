package ch.unibe.inf.scg_seminar_exceptions;

public class DatabaseExceptionEntry {
	
	private String projectName;
	private String version;
	private String className;
	private String type;
	private String pathToFile;
	private int start_line;
	private int end_line;
	private String content;


	public DatabaseExceptionEntry(String projectName, String className, String version, String type, String pathToFile, int start_line,
			int end_line, String content) {
		super();
		this.projectName = projectName;
		this.version = version;
		this.type = type;
		this.pathToFile = pathToFile;
		this.start_line = start_line;
		this.end_line = end_line;
		this.content = content;
		this.className = className;
	}
	
	public DatabaseExceptionEntry(String projectName, String version, String type, HierarchieEntry object){
		this.projectName = projectName;
		this.version = version;
		this.type = type;
		this.className = object.getClassName();
		this.pathToFile = object.getFile().getPath();
		this.start_line = object.getNode().getBegin().line;
		this.end_line = object.getNode().getEnd().line;
		this.content = object.getNode().toString();
	}


	public String getProjectName() {
		return projectName;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getPathToFile() {
		return pathToFile;
	}


	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}


	public int getStart_line() {
		return start_line;
	}


	public void setStart_line(int start_line) {
		this.start_line = start_line;
	}


	public int getEnd_line() {
		return end_line;
	}


	public void setEnd_line(int end_line) {
		this.end_line = end_line;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}
	
	

}
