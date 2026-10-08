package com.user.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.user.common.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtconfig;

    // 生成 token
    public String createToken(Integer userId, String account) {
        long expireTime = jwtconfig.getExpire() * 1000;

        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("account", account)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                .sign(Algorithm.HMAC256(jwtconfig.getSecret()));
    }

    // 验证 token
    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtconfig.getSecret())).build();
        return verifier.verify(token);
    }

    // 从token获取用户ID
    public Integer getUserId(String token) {
        return verifyToken(token).getClaim("userId").asInt();
    }

    // 获取账号
    public String getAccount(String token) {
        return verifyToken(token).getClaim("account").asString();
    }
}