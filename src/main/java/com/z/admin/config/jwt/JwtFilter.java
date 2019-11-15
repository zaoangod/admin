package com.z.admin.config.jwt;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.z.admin.config.authentication.Constant;
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
        // 查看当前 Header 中是否携带 Authorization 属性 Token, 有的话就进行登录认证授权
        // 没有携带 Token
        if (isLoginAttempt(request, response)) {
            log.info("请求类型: [{}], 请求地址: [{}]", httpMethod, requestURI);
            try {
                // 进行 Shiro 的登录 UserRealm
                this.executeLogin(request, response);
            } catch (Exception e) {
                String    msg       = e.getMessage();
                Throwable throwable = e.getCause();
                if (throwable instanceof SignatureVerificationException) {
                    // 该异常为 JWT 的 AccessToken 认证失败(Token或者密钥不正确)
                    msg = "Token 或者密钥不正确(" + throwable.getMessage() + ")";
                } else if (throwable instanceof TokenExpiredException) {
                    // 该异常为 JWT 的 AccessToken 已过期, 判断 RefreshToken 未过期就进行 AccessToken 刷新
                    if (this.refreshToken(request, response)) {
                        return true;
                    } else {
                        msg = "Token 已过期(" + throwable.getMessage() + ")";
                    }
                } else {
                    // 应用异常不为空
                    if (throwable != null) {
                        // 获取应用异常 msg
                        msg = throwable.getMessage();
                    }
                }
                log.info("=>> Shiro 登录异常信息: {}", msg);
                // Token认证失败直接返回Response信息
                this.response401(response, msg);
                return false;
            }
        } else {
            // 没有携带 Token
            log.info("请求类型: [{}], 请求地址: [{}], Authorization(Token) 为空", httpMethod, requestURI);
            // mustLoginFlag = true 开启任何请求必须登录才可访问
            Boolean mustLoginFlag = false;
            if (mustLoginFlag) {
                this.response401(response, "请先登录");
                return false;
            }
        }
        return true;
    }

    /**
     * 判断用户是否想要登入
     * 检测 header 里面是否包含 Authorization 字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        // 拿到当前Header中Authorization的AccessToken(Shiro 中 getAuthzHeader 方法已经实现)
        String token = this.getAuthzHeader(request);
        log.debug("=>> isLoginAttempt > Token: {}", token);
        return (token != null);
    }

    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String             authorization      = httpServletRequest.getHeader("Authorization");
        log.info("=>> authorization: {}", authorization);
        //
        JwtToken token = new JwtToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
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

    /**
     * 无需转发，直接返回Response信息
     */
    private void response401(ServletResponse response, String msg) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            Map<String, Object> param = new HashMap<>();
            param.put("code", "401");
            param.put("msg", "无访问权, " + msg);
            String data = JSON.toJSONString(param);
            out.append(data);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("直接返回 Response 信息出现 IOException 异常: {}", e.getMessage());
        }
    }

}