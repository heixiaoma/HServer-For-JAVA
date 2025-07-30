package cn.hserver.core.queue.fqueue.exception;

public class FileEOFException extends Exception {

    private static final long serialVersionUID = -1L;

    public FileEOFException(String message) {
        super(message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
