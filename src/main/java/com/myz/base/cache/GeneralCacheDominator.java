package com.myz.base.cache;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: GeneralCacheDominator
 * @author: mingyu.zhao
 * @date: 15/5/29 下午4:28
 */
public class GeneralCacheDominator {
    private static final Logger log = LoggerFactory.getLogger(GeneralCacheDominator.class);

    public static <V> V getCachedValue(final String cacheKey, final CacheContext context, final CacheOperator<V> cacheOperator) {
        int timeToLiveLimit = context.timeToLiveLimit;
        CachedObject<V> cachedObject = cacheOperator.getCachedObject(cacheKey);
        if (cachedObject != null) {
            long timeToLiveSeconds = cachedObject.getTimeToLiveSeconds();
            V value = cachedObject.getV();
            if (timeToLiveSeconds != 0 && timeToLiveSeconds < timeToLiveLimit) {
                //如果已经在最小期限以下，则异步刷新缓存
                Thread t = new Thread(GeneralCacheDominator.class.getSimpleName() + "-refresher") {
                    public void run() {
                        boolean lockSuccess = false;
                        try {
                            lockSuccess = cacheOperator.lock(cacheKey);
                            if (lockSuccess) {
                                log.info("reloading {} by {}", cacheKey, this.getName());
                                V value2 = cacheOperator.reload();
                                cacheOperator.setToCache(cacheKey, value2, context.expiredTime);
                            } else {
                                //only for logging
                                if (log.isDebugEnabled()) {
                                    log.debug("skip reloading {} by {}", cacheKey, this.getName());
                                }
                            }
                        } finally {
                            if (lockSuccess) {
                                cacheOperator.unlock(cacheKey);
                            }
                        }
                    }
                };
                t.start();
            }
            return value;
        }

        V valueReloaded = cacheOperator.reload();
        cacheOperator.setToCache(cacheKey, valueReloaded, context.expiredTime);
        return valueReloaded;
    }

    /**
     * 获取临时缓存
     * @param <K> key
     * @param cacheMap
     * @param getter
     * @param <V> value
     * @return
     */
    public static <K, V> V getCachedMapValue(K key, Map<K, V> cacheMap, ValueGetter<V> getter) {
        V v = cacheMap.get(key);
        if (v == null) {
            v = getter.get();
            cacheMap.put(key, v);
        }
        return v;
    }

}
