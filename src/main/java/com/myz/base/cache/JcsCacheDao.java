package com.myz.base.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: JcsCacheDao
 * @author: mingyu.zhao
 * @date: 15/5/19 下午12:02
 */
public abstract class JcsCacheDao<T> {
    private final Logger log = LoggerFactory.getLogger(JcsCacheDao.class);

    protected String cachedKey; //jcs缓存key
    protected int expiredTime; //过期时间(秒)
    protected int timeToLiveLimit; //生存时间(秒)极限，当剩余生存时间小于等于这个时需要重新初始化缓存

    private Object lck = new Object();
    private volatile boolean reloading = false;

    protected CacheManager cacheManager = CacheManager.getInstance();

    public T get() {
        CacheElement element = cacheManager.getElement(cachedKey);
        if (element != null) {
            long timeToLiveSeconds = element.getTimeToLiveSeconds();
            Object o = element.getValue();
            if (timeToLiveSeconds > timeToLiveLimit || reloading) {
                //如果在最小生存极限中，或者正在刷新缓存
                if ("null".equals(o)) {
                    return null;
                }
                return (T) o;
            } else {
                //异步刷新缓存
                synchronized (lck) {
                    element = cacheManager.getElement(cachedKey);
                    if (element != null) {
                        if (element.getTimeToLiveSeconds() > timeToLiveLimit) {
                            return (T) element.getValue();
                        }
                    }

                    reloading = true;
                    Thread t = new Thread(this.getClass().getSimpleName() + "-refresher") {
                        public void run() {
                            try {
                                log.info("reloading {} by {}", cachedKey, this.getName());
                                refreshCache();
                            } finally {
                                reloading = false;
                            }
                        }
                    };
                    t.start();
                }

                return (T) o;
            }
        }

        T value = refreshCache();
        return value;
    }

    protected T refreshCache() {
        if (log.isDebugEnabled()) {
            log.debug("reloading cache for {}", cachedKey);
        }
        Object cachedObject = "null";
        T value = reload();
        if (value != null) {
            cachedObject = value;
        }
        cacheManager.put(cachedKey, cachedObject, expiredTime);

        return value;
    }

    public abstract T reload();
}
