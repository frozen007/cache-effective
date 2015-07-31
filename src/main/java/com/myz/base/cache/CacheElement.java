package com.myz.base.cache;

/**
 * @ClassName: CacheElement
 * @author: mingyu.zhao
 * @date: 15/5/19 上午11:56
 */
public class CacheElement {
    Object value;
    long timeToLiveSeconds;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }
}
