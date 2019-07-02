package com.tamboot.redis.core;

public interface RedisLockExecutor<T> {
    T onLockSuccess();

    T onLockFail();
}
