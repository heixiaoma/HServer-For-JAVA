package top.hserver.core.queue.kvstore.core.exception;

/**
 * {@link FindFailedException} is thrown when there is a problem with retrieving an entity from a repository.
 */
public final class FindFailedException extends RocksIOException {

    public FindFailedException(final String message) {
        super(message);
    }

    public FindFailedException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
