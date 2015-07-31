package com.myz.base.cache;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.behavior.ICacheElement;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: CacheManager
 * @author: mingyu.zhao
 * @date: 15/2/3 下午4:47
 */
public class CacheManager {
    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);

    private JCS jcsCache = null;

    private static String CACHE_REGION_NAME = "default";

    private static volatile CacheManager cacheManager = null;

    private CacheManager() {
        try {
            jcsCache = JCS.getInstance(CACHE_REGION_NAME);
        } catch (CacheException e) {
            e.printStackTrace();
        }
    }

    public static CacheManager getInstance() {
        if (cacheManager == null) {
            synchronized (CacheManager.class) {
                if (cacheManager == null) {
                    cacheManager = new CacheManager();
                }
            }
        }

        return cacheManager;
    }


    /**
     * 缓存项。缓存时间为默认的1分钟
     * @param name
     * @param value
     */
    public void put(Object name, Object value) {
        try {
            jcsCache.put(name, value);
        } catch (CacheException e) {
            e.printStackTrace();
            log.info("put local cache error. key=" + name + ",value=" + value, e);
        }
    }


    /**
     * 缓存项，可设置过期时间，单位：秒
     * @param name
     * @param value
     * @param expire 单位：秒
     */
    public void put(Object name, Object value, int expire) {
        try {
            IElementAttributes attributes = jcsCache.getDefaultElementAttributes();
            attributes.setMaxLifeSeconds(expire);
            jcsCache.put(name, value, attributes);
        } catch (CacheException e) {
            e.printStackTrace();
            log.info("put local cache error. key=" + name + ",value=" + value, e);
        }
    }

    public CacheElement getElement(Object name) {
        CacheElement result = null;
        ICacheElement cacheElement = jcsCache.getCacheElement(name);
        if (cacheElement != null) {
            result = new CacheElement();
            result.setValue(cacheElement.getVal());
            result.setTimeToLiveSeconds(cacheElement.getElementAttributes().getTimeToLiveSeconds());
        }

        return result;
    }

    public Object get(Object name) {
        Object value = jcsCache.get(name);

        return value;
    }
}