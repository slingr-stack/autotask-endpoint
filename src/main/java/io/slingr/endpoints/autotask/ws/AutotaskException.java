package io.slingr.endpoints.autotask.ws;

public class AutotaskException extends Exception {
	static final long serialVersionUID = 1;
	
	public AutotaskException(String message) {
		super(message);
	}
	
	public AutotaskException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public AutotaskException(Throwable cause) {
		super(cause);
	}
}
