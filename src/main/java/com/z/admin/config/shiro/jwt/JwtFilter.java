package com.z.admin.config.shiro.jwt;

import com.alibaba.fastjson.JSON;
import com.z.admin.config.shiro.Constant;
import com.z.admin.util.EhCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: jwt filter
 * @Date: 2019-11-14 15:01
 * @Author: jy
 */
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest  httpServletRequest  = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 这里我们详细说明下为什么最终返回的都是true, 即允许访问
     * 例如我们提供一个地址 GET /article
     * 登入用户和游客看到的内容是不同的
     * 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
     * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入
     * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
     * 但是这样做有一个缺点，就是不能够对GET,POST等请求进行分别过滤鉴权(因为我们重写了官方的方法), 但实际上对应用影响不大
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        // 获取当前请求URI
        String requestURI = httpServletRequest.getRequestURI();
        // 获取当前请求类型
        String httpMethod = httpServletRequest.getMethod();
        log.info("请求类型: [{}], 请求地址: [{}]", httpMethod, requestURI);
        // 查看当前 Header 中是否携带 Authorization 属性 Token, 有的话就进行登录认证授权
        boolean loginAttempt = isLoginAttempt(request, response);
        log.info("->> {}", loginAttempt);
        if (loginAttempt) {
            return this.executeLogin(request, response);
        }
        // 返回 false 会执行 onAccessDenied 方法
        return false;
    }

    /**
     * onAccessDenied: 表示当访问拒绝时是否已经处理了;
     * 如果返回 true 表示需要继续处理;
     * 如果返回 false 表示该拦截器实例已经处理了, 将直接返回即可
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // log.info("当 isAccessAllowed 返回 false 的时候，才会执行 method onAccessDenied ");
        HttpServletRequest  request  = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.sendRedirect(request.getContextPath() + "/common/401");
        // 返回 false 表示已经处理，例如页面跳转啥的，表示不在走以下的拦截器了（如果还有配置的话）
        return false;
    }

    /**
     * 判断用户是否想要登入
     * 检测 header 里面是否包含 Authorization 字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        String  token = this.getAuthzHeader(request);
        boolean tag   = token != null && !token.equals("");
        log.debug("=>> 判断请求 Header 中是否有 Authorization 参数: {}", tag);
        return tag;
    }

    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        log.debug("=>> Execute Login...");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String             authorization      = httpServletRequest.getHeader("Authorization");
        log.info("=>> authorization: {}", authorization);
        JwtToken token = new JwtToken(authorization);
        try {
            // 提交给realm进行登入，如果错误他会抛出异常并被捕获
            getSubject(request, response).login(token);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 此处为 AccessToken 刷新, 进行判断 RefreshToken 是否过期, 未过期就返回新的 AccessToken 且继续正常访问
     */
    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        // 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
        String token = this.getAuthzHeader(request);
        // 获取当前Token的帐号信息
        String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
        // 判断Redis中RefreshToken是否存在
        log.info("=>> refreshToken > {}", EhCacheUtil.getCacheManager().getName());

        /*if (JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
            // Redis中RefreshToken还存在，获取RefreshToken的时间戳
            String currentTimeMillisRedis = JedisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account).toString();
            // 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
            if (JwtUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
                // 获取当前最新时间戳
                String currentTimeMillis = String.valueOf(System.currentTimeMillis());
                // 读取配置文件，获取refreshTokenExpireTime属性
                PropertiesUtil.readProperties("config.properties");
                String refreshTokenExpireTime = PropertiesUtil.getProperty("refreshTokenExpireTime");
                // 设置RefreshToken中的时间戳为当前最新时间戳，且刷新过期时间重新为30分钟过期(配置文件可配置refreshTokenExpireTime属性)
                JedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account, currentTimeMillis, Integer.parseInt(refreshTokenExpireTime));
                // 刷新AccessToken，设置时间戳为当前最新时间戳
                token = JwtUtil.sign(account, currentTimeMillis);
                // 将新刷新的AccessToken再次进行Shiro的登录
                JwtToken jwtToken = new JwtToken(token);
                // 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
                this.getSubject(request, response).login(jwtToken);
                // 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
                HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
                httpServletResponse.setHeader("Authorization", token);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                return true;
            }
        }
        return false;
        */
        return true;
    }
    /*@Override
    protected boolean sendChallenge(ServletRequest request, ServletResponse response) {
        log.debug("Authentication required: sending 401 Authentication challenge response");
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        try {
            AjaxResult  result      = new AjaxResult(HttpStatus.UNAUTHORIZED.value(), "请先进行身份认证后重试");
            PrintWriter printWriter = httpResponse.getWriter();
            printWriter.append(JSON.toJSONString(result));
        } catch (IOException e) {
            log.error("sendChallenge error, can not resolve httpServletResponse");
        }
        return false;
    }*/
}