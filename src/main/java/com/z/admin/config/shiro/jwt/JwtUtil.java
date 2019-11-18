package com.z.admin.config.shiro.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.z.admin.config.shiro.Constant;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: JWTUtil
 * @Date: 2019-11-14 16:50
 * @Author: jy
 */
@Slf4j
public class JwtUtil {

    // 过期时间24小时
    private static final long EXPIRE_TIME = 2 * 60 * 60 * 1000;

    /**
     * 验证
     *
     * @param token    token
     * @param username username
     * @param secret   secret
     * @return 结果
     */
    public static boolean verify(String token, String username, String secret) {
        try {
            // 根据密码生成JWT效验器
            Algorithm   algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier  = JWT.require(algorithm).withClaim(Constant.ACCOUNT, username).build();
            // 效验TOKEN
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String sign(String username, String secret) {
        Date      date      = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 设置头部信息
        Map<String, Object> header = new HashMap<>(2);
        header.put("Type", "Jwt");
        header.put("alg", "HS256");

        // 附带username信息
        return JWT.create()
                .withHeader(header)
                .withClaim(Constant.ACCOUNT, username)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 获得 Token 中的信息无需 secret 解密也能获得
     */
    public static String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            log.error("解密 Token 中的公共信息出现 JWTDecodeException 异常:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Date date = new Date(System.currentTimeMillis());
        log.info("{}", date);
        date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        log.info("{}", date);
    }

}