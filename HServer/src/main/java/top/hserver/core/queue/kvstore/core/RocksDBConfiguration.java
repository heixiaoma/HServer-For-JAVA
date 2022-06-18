package top.hserver.core.queue.kvstore.core;

import java.io.File;

/**
 * Configuration class for RocksDB.
 * Path is path to files where data will be stored.
 * Name is the name of repository.
 */
public final class RocksDBConfiguration {

    protected final String path;
    protected final String name;
    protected int threadCount = 5;

    public RocksDBConfiguration(
            final String path,
            final String name
    ) {
        this.path = path;
        this.name = name;
    }

    public RocksDBConfiguration(
            final String path,
            final String name,
            final int threadCount
    ) {
        this.path = path;
        this.name = name;
        this.threadCount = threadCount;
    }

    public String path() {
        return path;
    }

    public String name() {
        return name;
    }

    public int threadCount() {
        return threadCount;
    }

    public String url() {
        return path + File.separator + name;
    }
}
