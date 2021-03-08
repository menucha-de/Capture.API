package havis.capture;

public class AdapterException extends Exception {

	private static final long serialVersionUID = 1L;

	public AdapterException(String message) {
		super(message);
	}

	public AdapterException(String message, Throwable cause) {
		super(message, cause);
	}
}