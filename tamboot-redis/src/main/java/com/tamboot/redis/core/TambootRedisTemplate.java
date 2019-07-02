package com.tamboot.redis.core;

import com.tamboot.common.tools.text.TextUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class TambootRedisTemplate<T> {
    protected RedisTemplate redisTemplate;

    public TambootRedisTemplate(RedisTemplate redisTemplate) {
        Assert.notNull(redisTemplate, "redisTemplate must not be null");
        this.redisTemplate = redisTemplate;
    }

    /**
     * Resolve namespace value.
     * @param namespace
     * @return namespace value, must not be null
     */
    protected abstract String resolveNamespaceValue(T namespace);

    private String createKeyWithNamespace(T namespace, String key) {
        Assert.notNull(namespace, "namespace must not be null");
        Assert.notNull(key, "key must not be null");
        String namespaceValue = resolveNamespaceValue(namespace);
        return RedisKeyGenerator.generate(namespaceValue, key);
    }

    private boolean isSuccess(Boolean success) {
        return success != null && success;
    }

    public void set(T namespace, String key, Object value) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        redisTemplate.opsForValue().set(keyWithNamespace, value);
    }

    public void set(T namespace, String key, Object value, Duration timeout) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        redisTemplate.opsForValue().set(keyWithNamespace, value, timeout);
    }

    public Object get(T namespace, String key) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForValue().get(keyWithNamespace);
    }

    public String getString(T namespace, String key) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        Object value = redisTemplate.opsForValue().get(keyWithNamespace);
        if (value == null) {
            return null;
        }

        if (value instanceof  String) {
            return (String) value;
        }
        return value.toString();
    }

    public boolean setIfAbsent(T namespace, String key, String value) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(keyWithNamespace, value);
        return success != null && success;
    }

    public boolean setIfAbsent(T namespace, String key, String value, Duration timeout) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(keyWithNamespace, value, timeout);
        return isSuccess(success);
    }

    public boolean delete(T namespace, String key) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        Boolean success = redisTemplate.delete(keyWithNamespace);
        return isSuccess(success);
    }

    public boolean expired(T namespace, String key, Duration timeout) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        Boolean success = redisTemplate.expire(keyWithNamespace, timeout.getSeconds(), TimeUnit.SECONDS);
        return isSuccess(success);
    }

    public Long increment(T namespace, String key, long delta) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForValue().increment(keyWithNamespace, delta);
    }

    public Long decrement(T namespace, String key, long delta) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForValue().decrement(keyWithNamespace, delta);
    }

    public Boolean zsetAdd(T namespace, String key, String value, double score) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        Boolean success = redisTemplate.opsForZSet().add(keyWithNamespace, value, score);
        return isSuccess(success);
    }

    public Long zsetAdd(T namespace, String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().add(keyWithNamespace, tuples);
    }

    public Long zsetRemove(T namespace, String key, Object ...values) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().remove(keyWithNamespace, values);
    }

    public Double zsetIncrementScore(T namespace, String key, String value, double delta) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().incrementScore(keyWithNamespace, value, delta);
    }

    public Long zsetRemoveRangeByScore(T namespace, String key, double minScore, double maxScore) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().removeRangeByScore(keyWithNamespace, minScore, maxScore);
    }

    public Set<String> zsetRange(T namespace, String key, long start, long end) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().range(keyWithNamespace, start, end);
    }

    public Set<String> zsetReverseRange(T namespace, String key, long start, long end) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().reverseRange(keyWithNamespace, start, end);
    }

    public Set<String> zsetRangeByScore(T namespace, String key, double minScore, double maxScore) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().rangeByScore(keyWithNamespace, minScore, maxScore);
    }

    public Double zsetScore(T namespace, String key, Object value) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().score(keyWithNamespace, value);
    }

    public Long zsetZCard(T namespace, String key) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForZSet().zCard(keyWithNamespace);
    }

    public Long setAdd(T namespace, String key, String... values) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        return redisTemplate.opsForSet().add(keyWithNamespace, values);
    }

    public Long setAdd(T namespace, String key, Duration timeout, String... values) {
        redisTemplate.multi();
        Long result = setAdd(namespace, key, values);
        expired(namespace, key, timeout);
        redisTemplate.exec();
        return result;
    }

    public boolean setIsMember(T namespace, String key, String value) {
        String keyWithNamespace = createKeyWithNamespace(namespace, key);
        Boolean success = redisTemplate.opsForSet().isMember(keyWithNamespace, value);
        return isSuccess(success);
    }

    /**
     *  Try to acquire lock according to {@code namespace} and {@code key}, and then release lock after {@code timeout} duration.
     * @param namespace required
     * @param key required
     * @param timeout required
     * @return true if success to acquire lock, otherwise false
     */
    public boolean lock(T namespace, String key, Duration timeout) {
        if (namespace == null || key == null || timeout == null) {
            return false;
        }

        Boolean lockSuccess = setIfAbsent(namespace, key, "1", timeout);
        return lockSuccess != null && lockSuccess;
    }

    /**
     * Try to release lock according to {@code namespace} and {@code key}.
     * @param namespace required
     * @param key required
     * @return true if success to release lock, otherwise false
     */
    public boolean releaseLock(T namespace, String key) {
        Boolean releaseSuccess = delete(namespace, key);
        return releaseSuccess != null && releaseSuccess;
    }

    /**
     * Try to acquire lock according to {@code duration} and {@code concurrent}.
     * Success to acquire lock if already locked count is less than {@code concurrent} in specified duration {@code duration}
     * @param namespace required
     * @param key required
     * @param duration required
     * @param concurrent required, larger than 0
     * @return true if lock success, otherwise false
     */
    public boolean lockInDuration(T namespace, String key, Duration duration, long concurrent) {
        if (namespace == null || key == null || duration == null || concurrent <= 0) {
            return false;
        }

        long currentMillis = System.currentTimeMillis();
        long timePoint = currentMillis / duration.getSeconds() / 1000;
        String timeKey = RedisKeyGenerator.generate(key, String.valueOf(timePoint));

        String countText = getString(namespace, timeKey);
        long currentCount = 0;
        if (TextUtil.isNotEmpty(countText)) {
            try {
                currentCount = Long.parseLong(countText);
            } catch (NumberFormatException e) {
            }
        }
        if (currentCount >= concurrent) {
            return false;
        }

        redisTemplate.multi();
        setIfAbsent(namespace, timeKey, "0", duration);
        Long increasedCount = increment(namespace, timeKey, 1l);
        redisTemplate.exec();
        if (increasedCount == null || increasedCount > concurrent) {
            return false;
        }
        return true;
    }

    /**
     * Execute specified codes when success to lock or fail to lock.
     * @param namespace required
     * @param key required
     * @param timeout required
     * @param executor required
     * @param <R>
     * @return execute result
     */
    public <R> R executeInLock(T namespace, String key, Duration timeout, RedisLockExecutor<R> executor) {
        if (namespace == null || key == null || timeout == null || executor == null) {
            return null;
        }

        try {
            Boolean lockSuccess = setIfAbsent(namespace, key, "1", timeout);
            if (lockSuccess != null && lockSuccess) {
                return executor.onLockSuccess();
            } else {
                return executor.onLockFail();
            }
        } finally {
            delete(namespace, key);
        }
    }
}
