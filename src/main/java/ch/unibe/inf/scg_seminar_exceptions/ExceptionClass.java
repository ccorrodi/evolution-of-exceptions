package ch.unibe.inf.scg_seminar_exceptions;

public class ExceptionClass {
	private String name;
	private boolean checked;
	private Scope scope;

	public ExceptionClass(String name, boolean checked, Scope scope) {
		this.name = name;
		this.checked = checked;
		this.scope = scope;
	}

	public String getName() {
		return name;
	}

	public boolean isChecked() {
		return checked;
	}

	public Scope getScope() {
		return scope;
	}
}
