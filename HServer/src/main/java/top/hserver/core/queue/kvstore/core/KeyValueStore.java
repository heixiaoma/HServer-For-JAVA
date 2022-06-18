package top.hserver.core.queue.kvstore.core;

import java.util.Collection;
import java.util.Optional;

import top.hserver.core.queue.kvstore.core.exception.DeleteAllFailedException;
import top.hserver.core.queue.kvstore.core.exception.DeleteFailedException;
import top.hserver.core.queue.kvstore.core.exception.FindFailedException;
import top.hserver.core.queue.kvstore.core.exception.SaveFailedException;

/**
 * Interface that defines operations against Key-Value Store.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public interface KeyValueStore<K, V> {

    /**
     * Inserts key-value pair into RocksDB.
     *
     * @param key   of value.
     * @param value that should be persisted.
     * @throws SaveFailedException when it's not possible to persist entity.
     */
    void save(K key, V value) throws SaveFailedException;

    /**
     * Try to find value for a given key.
     *
     * @param key of entity that should be retrieved.
     * @return Optional of entity.
     * @throws FindFailedException when it's not possible to retrieve a wanted entity.
     */
    Optional<V> findByKey(K key) throws FindFailedException;

    /**
     * Try to find all entities from repository.
     *
     * @return Collection of entities.
     */
    Collection<V> findAll();

    Optional<V> findFirst();

    Optional<V> findLast();

    /**
     * Delete entity for a given key.
     *
     * @param key of entity that should be deleted.
     * @throws DeleteFailedException when it's not possible to delete a wanted entity.
     */
    void deleteByKey(K key) throws DeleteFailedException;

    /**
     * Deletes all entities from RocksDB.
     *
     * @throws DeleteAllFailedException when it's not possible to delete all entities.
     */
    void deleteAll() throws DeleteAllFailedException;


    long size();
}
