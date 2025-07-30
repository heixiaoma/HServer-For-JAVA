package cn.hserver.core.queue.fqueue.exception;

public class FileFormatException extends Exception {

    private static final long serialVersionUID = -1L;

    public FileFormatException(String message) {
        super(message);
    }

    public FileFormatException(Throwable cause) {
        super(cause);
    }
}
