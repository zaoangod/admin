package com.z.admin.config.shiro;

import com.z.admin.config.jwt.JwtFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;

/**
 * @Description: ShiroConfig
 * @Date: 2019-11-13 22:17
 * @Author: jy
 */
@Slf4j
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置 securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 在 Shiro过滤器链上加入 JWTFilter
        LinkedHashMap<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filters);

        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 所有请求都要经过 jwt过滤器
        filterChainDefinitionMap.put("/**", "jwt");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
        //        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //
        //        // 拦截器
        //        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //        //登录页面链接
        //        //shiroFilterFactoryBean.setLoginUrl("/login");
        //        //未授权页面链接
        //        //shiroFilterFactoryBean.setUnauthorizedUrl("/notRole");
        //        //shiroFilterFactoryBean.setUnauthorizedUrl("/common/403");
        //
        //        // 未授权界面返回JSON
        //        shiroFilterFactoryBean.setUnauthorizedUrl("/common/403");
        //        shiroFilterFactoryBean.setLoginUrl("/common/403");
        //
        //        //authc: 所有url都必须认证通过才可以访问
        //        //anon:  所有url都都可以匿名访问
        //        filterChainDefinitionMap.put("/**/login", "anon");
        //        filterChainDefinitionMap.put("/**/static/**", "anon");
        //
        //        // 添加自己的过滤器并且取名为jwt
        //        Map<String, Filter> filterMap = new HashMap<>(1);
        //        filterMap.put("jwt", new JwtFilter());
        //        shiroFilterFactoryBean.setFilters(filterMap);
        //        // 过滤链定义, 从上向下顺序执行，一般将 /** 放在最为下边
        //        filterChainDefinitionMap.put("/common/403", "anon");
        //        filterChainDefinitionMap.put("/**", "jwt");
        //        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        //        return shiroFilterFactoryBean;
    }

    /**
     * 安全管理器
     */
    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(CustomRealm customRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm);
        //自定义缓存实现, 使用 EhCache
        securityManager.setCacheManager(this.ehCacheManager());
        //关闭 shiro 自带的 session
        DefaultSubjectDAO              subjectDAO                     = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    /**
     * 添加注解支持
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启注解验证
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 添加注解支持
     * 解决 spring aop 和注解配置一起使用的 bug
     * 在 @Controller 注解的类的方法中加入 @RequiresRole 等 shiro 注解, 会导致该方法无法映射请求, 导致返回404
     */
    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 缓存管理器, 使用Ehcache实现
     */
    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache/ehcache-shiro.xml");
        return cacheManager;
    }

    /**
     * 自定义Realm
     */
    @Bean
    public CustomRealm customRealm(EhCacheManager ehCacheManager) {
        CustomRealm realm = new CustomRealm();
        realm.setCacheManager(ehCacheManager);
        return realm;
    }
}