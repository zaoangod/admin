package com.z.admin.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * @Description:
 * @Date: 2019-11-15 11:15
 * @Author: jy
 */
public class EhCacheUtil {
    /**
     * 获取EhCacheManager管理对象
     */
    public static CacheManager getCacheManager() {
        return (CacheManager) SpringContextUtil.getBean("ehCacheManager");
         //SpringContextUtil.getBean(CacheManager.class);
    }

    /**
     * 获取EhCache缓存对象
     */
    public static Cache getCache(String name) {
        return getCacheManager().getCache(name);
    }
}