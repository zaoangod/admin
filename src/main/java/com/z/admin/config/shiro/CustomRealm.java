package com.z.admin.config.shiro;

import com.z.admin.config.shiro.jwt.JwtToken;
import com.z.admin.config.shiro.jwt.JwtUtil;
import com.z.admin.model.SysUser;
import com.z.admin.service.ISysMenuService;
import com.z.admin.service.ISysRoleService;
import com.z.admin.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @Description: 自定义安全数据Realm
 * @Date: 2019-11-13 22:38
 * @Author: jy
 */
@Slf4j
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysUserService userService;

    //告诉shiro如何根据获取到的用户信息中的密码和盐值来校验密码
    /*{
        //设置用于匹配密码的CredentialsMatcher
        HashedCredentialsMatcher hashMatcher = new HashedCredentialsMatcher();
        hashMatcher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
        hashMatcher.setStoredCredentialsHexEncoded(false);
        hashMatcher.setHashIterations(1024);
        this.setCredentialsMatcher(hashMatcher);
    }*/

    /**
     * 必须重写此方法, 不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("=>> 配置当前用户权限");
        String  username = (String) principals.getPrimaryPrincipal();
        SysUser sysUser  = new SysUser().setLoginName(username);
        sysUser = userService.selectUser(sysUser).stream().findFirst().orElse(null);
        if (null == sysUser) {
            return null;
        }
        //
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (sysUser.getLoginName().equals("admin")) {
            authorizationInfo.addRole("admin");
            authorizationInfo.addStringPermission("*:*:*");
        } else {
            long        userId          = sysUser.getUserId();
            Set<String> roleKeyList     = roleService.selectRoleKeyByUserId(userId);
            Set<String> permissionsList = menuService.selectPermissionsByUserId(userId);
            // 角色加入AuthorizationInfo认证对象
            authorizationInfo.setRoles(roleKeyList);
            // 权限加入AuthorizationInfo认证对象
            authorizationInfo.setStringPermissions(permissionsList);
        }
        return authorizationInfo;
    }

    /**
     * 身份认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) {
        log.debug("=>> Shiro 身份认证");
        String token = (String) auth.getCredentials();
        log.info("=>> token: {}", token);
        if (token == null) {
            log.info("=>> Token 未获取到, 身份认证失败");
            throw new AuthenticationException("token为空");
        }
        // 解密获得 username, 用于和数据库进行对比
        String username = JwtUtil.getClaim(token, Constant.ACCOUNT);
        if (username == null) {
            log.debug("=>> Token 未获取到 UserName");
            throw new AuthenticationException("token 非法无效");
        }
        log.info("=>> Token 中获取到 UserName: {}", username);
        // 查询用户信息
        SysUser user = userService.selectUser(new SysUser().setLoginName(username)).stream().findFirst().orElse(null);
        if (user == null) {
            log.debug("=>> 根据 UserName 未查询到用户信息");
            throw new AuthenticationException("用户不存在");
        }
        log.info("=>> 根据 UserName 查询到用户信息");
        //验证 token
        if (!JwtUtil.verify(token, username, user.getPassword())) {
            log.debug("=>> 用户名或者密码错误");
            throw new AuthenticationException("用户名或者密码错误");
        }
        log.info("=>> 身份认证通过");
        return new SimpleAuthenticationInfo(user, token, getName());
    }

    /**
     * JWTToken 刷新生命周期(实现: 用户在线操作不掉线功能)
     * 1、登录成功后将用户的JWT生成的Token作为k, v存储到 cache 缓存里面(这时候k, v值一样), 缓存有效期设置为 Jwt 有效时间的2倍
     * 2、当该用户再次请求时, 通过 JWTFilter 层层校验之后会进入到 doGetAuthenticationInfo 进行身份验证
     * 3、当该用户这次请求 jwt 生成的 token 值已经超时, 但该 token 对应 cache 中的 k 还是存在, 则表示该用户一直在操作只是 JWT 的 token 失效了, 程序会给 token 对应的 k 映射的v值重新生成 JWTToken 并覆盖 v 值, 该缓存生命周期重新计算
     * 4、当该用户这次请求 jwt 在生成的 token 值已经超时, 并在 cache 中不存在对应的 k, 则表示该用户账户空闲超时, 返回用户信息已失效, 请重新登录
     * 注意: 前端请求 Header 中设置 Authorization 保持不变, 校验有效性以缓存中的 token 为准
     * 用户过期时间 = Jwt 有效时间 * 2
     */

}
