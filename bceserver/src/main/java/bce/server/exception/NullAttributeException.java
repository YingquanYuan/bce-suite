package bce.server.exception;

/**
 * 空子段异常
 * 
 * @author robins
 *
 */
public class NullAttributeException extends RuntimeException {

	private static final long serialVersionUID = 5887575018967083314L;

	public NullAttributeException() {
		// TODO Auto-generated constructor stub
	}

	public NullAttributeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NullAttributeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public NullAttributeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
