package cn.hserver.plugin.satoken.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hserver.core.ioc.annotation.Autowired;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.*;

public class SaTokenDaoRedis implements SaTokenDao {

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            bucket.set(value);
        } else {
            bucket.set(value, Duration.ofSeconds(timeout));
        }
    }

    /**
     * 修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            return bucket.remainTimeToLive();
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        //判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.expire(Duration.ofSeconds(timeout));
    }

    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            bucket.set(object);
        } else {
            bucket.set(object, Duration.ofSeconds(timeout));
        }
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        redissonClient.getBucket(key).delete();
    }

    @Override
    public long getObjectTimeout(String key) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            return bucket.remainTimeToLive();
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        //判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.expire(Duration.ofSeconds(timeout));
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(prefix + "*" + keyword + "*");
        ArrayList<String> list = new ArrayList<>();
        keysByPattern.forEach(list::add);
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}