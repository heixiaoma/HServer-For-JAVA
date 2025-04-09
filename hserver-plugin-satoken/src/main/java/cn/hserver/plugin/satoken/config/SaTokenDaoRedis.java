package cn.hserver.plugin.satoken.config;

import cn.dev33.satoken.dao.auto.SaTokenDaoByObjectFollowString;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hserver.core.ioc.annotation.Autowired;
import org.redisson.api.*;
import org.redisson.api.options.KeysScanOptions;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaTokenDaoRedis implements SaTokenDaoByObjectFollowString {

    @Autowired
    private RedissonClient redissonClient;

    public String get(String key) {
        RBucket<String> rBucket = this.redissonClient.getBucket(key);
        return rBucket.get();
    }

    public void set(String key, String value, long timeout) {
        if (timeout != 0L && timeout > -2L) {
            if (timeout == -1L) {
                RBucket<String> bucket = this.redissonClient.getBucket(key);
                bucket.set(value);
            } else {
                RBatch batch = this.redissonClient.createBatch();
                RBucketAsync<String> bucket = batch.getBucket(key);
                bucket.setAsync(value);
                bucket.expireAsync(Duration.ofSeconds(timeout));
                batch.execute();
            }
        }
    }

    public void update(String key, String value) {
        long expire = this.getTimeout(key);
        if (expire != -2L) {
            this.set(key, value, expire);
        }
    }

    public void delete(String key) {
        this.redissonClient.getBucket(key).delete();
    }

    public long getTimeout(String key) {
        RBucket<String> rBucket = this.redissonClient.getBucket(key);
        long timeout = rBucket.remainTimeToLive();
        return timeout < 0L ? timeout : timeout / 1000L;
    }

    public void updateTimeout(String key, long timeout) {
        if (timeout == -1L) {
            long expire = this.getTimeout(key);
            if (expire != -1L) {
                this.set(key, this.get(key), timeout);
            }
        } else {
            RBucket<String> rBucket = this.redissonClient.getBucket(key);
            rBucket.expire(Duration.ofSeconds(timeout));
        }
    }

    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Stream<String> stream =  this.redissonClient.getKeys().getKeysStream(KeysScanOptions.defaults().pattern(prefix + "*" + keyword + "*"));
        List<String> list = stream.collect(Collectors.toList());
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}