package com.myz.base.cache;

/**
 * @ClassName: CacheContext
 * @author: mingyu.zhao
 * @date: 15/6/5 下午12:03
 */
public class CacheContext {
    String cacheKey; //缓存key
    int expiredTime; //过期时间(秒)

    /**
     * 生存时间(秒)极限，当剩余生存时间小于等于这个时会启动异步线程刷新缓存
     * 如果为0则不会异步刷新缓存
     */
    int timeToLiveLimit;

    public CacheContext(String cacheKey, int expiredTime) {
        this(cacheKey, expiredTime, 0);
    }

    public CacheContext(String cacheKey, int expiredTime, int timeToLiveLimit) {
        this.cacheKey = cacheKey;
        this.expiredTime = expiredTime;
        this.timeToLiveLimit = timeToLiveLimit;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public int getExpiredTime() {
        return expiredTime;
    }

    public int getTimeToLiveLimit() {
        return timeToLiveLimit;
    }
}