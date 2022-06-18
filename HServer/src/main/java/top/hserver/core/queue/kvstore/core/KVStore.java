package top.hserver.core.queue.kvstore.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import top.hserver.core.queue.kvstore.core.exception.DeleteAllFailedException;
import top.hserver.core.queue.kvstore.core.exception.DeleteFailedException;
import top.hserver.core.queue.kvstore.core.exception.FindFailedException;
import top.hserver.core.queue.kvstore.core.exception.SaveFailedException;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.server.util.SerializationUtil;

/**
 * Base class that should be extended by the concrete repository.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public class KVStore<K, V> extends RocksDBConnection implements KeyValueStore<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KVStore.class);

    private Class<K> keyType;
    private Class<V> valueType;

    /**
     * @param configuration for {@link RocksDBConnection}.
     * @param keyType       for mapper.
     * @param valueType     for mapper.
     */
    public KVStore(
            final RocksDBConfiguration configuration,
            final Class<K> keyType,
            final Class<V> valueType
    ) {
        super(configuration);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public void save(
            final K key,
            final V value
    ) throws SaveFailedException {
        try {
            final byte[] serializedKey = SerializationUtil.serialize(key);
            final byte[] serializedValue = SerializationUtil.serialize(value);
            rocksDB.put(serializedKey, serializedValue);
        } catch (final RocksDBException exception) {
            LOGGER.error("RocksDBException occurred during save operation. {}", exception.getMessage());
            throw new SaveFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public Optional<V> findByKey(final K key) throws FindFailedException {
        try {
            final byte[] serializedKey = SerializationUtil.serialize(key);
            final byte[] bytes = rocksDB.get(serializedKey);
            return Optional.ofNullable(SerializationUtil.deserialize(bytes, this.valueType));
        } catch (final RocksDBException exception) {
            LOGGER.error("RocksDBException occurred during findByKey operation. {}", exception.getMessage());
            throw new FindFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public Collection<V> findAll() {
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            final Collection<V> result = new ArrayList<>();
            iterator.seekToFirst();

            while (iterator.isValid()) {
                final V value = SerializationUtil.deserialize(iterator.value(), valueType);
                result.add(value);
                iterator.next();
            }

            return result;
        }
    }


    @Override
    public Optional<V> findLast() {
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            iterator.seekToLast();
            if (iterator.isValid()) {
                return Optional.ofNullable(SerializationUtil.deserialize(iterator.value(), valueType));
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<V> findFirst() {
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            iterator.seekToFirst();
            if (iterator.isValid()) {
                return Optional.ofNullable(SerializationUtil.deserialize(iterator.value(), valueType));
            }
        }
        return Optional.empty();
    }

    @Override
    public long size() {
        try (final RocksIterator iterator = rocksDB.newIterator()) {
            iterator.seekToFirst();
            long i = 0;
            while (iterator.isValid()) {
                i++;
                iterator.next();
            }
            return i;
        }
    }

    @Override
    public void deleteByKey(final K key) throws DeleteFailedException {
        try {
            final byte[] serializedKey = SerializationUtil.serialize(key);
            rocksDB.delete(serializedKey);
        } catch (final RocksDBException exception) {
            LOGGER.error("RocksDBException occurred during deleteByKey operation. {}", exception.getMessage());
            throw new DeleteFailedException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteAll() throws DeleteAllFailedException {
        final RocksIterator iterator = rocksDB.newIterator();

        iterator.seekToFirst();
        final byte[] firstKey = getKey(iterator);

        iterator.seekToLast();
        final byte[] lastKey = getKey(iterator);

        if (firstKey == null || lastKey == null) {
            return;
        }

        try {
            rocksDB.deleteRange(firstKey, lastKey);
            rocksDB.delete(lastKey);
        } catch (final RocksDBException exception) {
            LOGGER.error("RocksDBException occurred during deleteAll operation. {}", exception.getMessage());
            throw new DeleteAllFailedException(exception.getMessage(), exception);
        } finally {
            iterator.close();
        }
    }

    private byte[] getKey(final RocksIterator iterator) {
        if (!iterator.isValid()) {
            return null;
        }
        return iterator.key();
    }

    @SuppressWarnings("unchecked")
    private Class<K> extractKeyType() {
        return (Class<K>) extractClass(((ParameterizedType) getGenericSuperClass()).getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    private Class<V> extractValueType() {
        return (Class<V>) extractClass(((ParameterizedType) getGenericSuperClass()).getActualTypeArguments()[1]);
    }

    private Type getGenericSuperClass() {
        final Type superClass = getClass().getGenericSuperclass();

        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }

        return superClass;
    }

    private Class<?> extractClass(final Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }

        throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
    }
}
