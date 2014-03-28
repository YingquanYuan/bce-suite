package bce.server.exception;

/**
 * 空对象异常，obj == null
 *
 * @author robins
 *
 */
public class NullObjectException extends RuntimeException {

    private static final long serialVersionUID = 5608625504181231776L;

    public NullObjectException() {
        // TODO Auto-generated constructor stub
    }

    public NullObjectException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public NullObjectException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public NullObjectException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
