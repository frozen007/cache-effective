package com.myz.base.cache;

/**
 * @ClassName: CacheOperator
 * @author: mingyu.zhao
 * @date: 15/6/5 下午12:04
 */
public abstract class CacheOperator<V> {
    public boolean lock(String key) {
        return true;
    }

    public void unlock(String key) {

    }

    public abstract CachedObject<V> getCachedObject(String key);

    public abstract void setToCache(String key, V v, long expiredTime);

    public abstract V reload();

}
