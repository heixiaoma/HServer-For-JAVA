package top.hserver.core.queue.kvstore.core.exception;

/**
 * {@link DeleteAllFailedException} is thrown when there is a problem with deleting all entities from a repository.
 */
public final class DeleteAllFailedException extends RocksIOException {

    public DeleteAllFailedException(final String message) {
        super(message);
    }

    public DeleteAllFailedException(
            final String message,
            final Throwable throwable
    ) {
        super(message, throwable);
    }
}
