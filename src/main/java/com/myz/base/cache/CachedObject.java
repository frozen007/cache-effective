package com.myz.base.cache;

/**
 * @ClassName: CachedObject
 * @author: mingyu.zhao
 * @date: 15/6/5 下午12:02
 */
public class CachedObject<V> {
    long timeToLiveSeconds;
    long expiredTimeAt;
    V v;

    public CachedObject(V v) {
        this(0, v);
    }

    public CachedObject(long timeToLiveSeconds, V v) {
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.v = v;
    }

    public CachedObject(long timeToLiveSeconds, long expiredTimeAt, V v) {
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.expiredTimeAt = expiredTimeAt;
        this.v = v;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public long getExpiredTimeAt() {
        return expiredTimeAt;
    }

    public void setExpiredTimeAt(long expiredTimeAt) {
        this.expiredTimeAt = expiredTimeAt;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }
}