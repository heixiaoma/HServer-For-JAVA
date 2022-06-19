package net.hserver.core.queue.fqueue.exception;

public class FileFormatException extends Exception {

    private static final long serialVersionUID = -1L;

    public FileFormatException() {
        super();
    }

    public FileFormatException(String message) {
        super(message);
    }

    public FileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileFormatException(Throwable cause) {
        super(cause);
    }
}
