package com.huiluczp.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huiluczp.bean.UserInfo;
import com.huiluczp.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// JWT token生成工具类
@PropertySource("classpath:/application.yml")
@Component
public class JWTUtil {
    private static long ALIVE_TIME;
    private static final String SALT = "5oiR5Lmf5LiN5oeC77yM5q+V56uf5oiR5Y+q5piv5LiA5p2h54uX44CC";

    @Value("${jwt.alive_time}")
    public void setAliveTime(long aliveTime) {
        ALIVE_TIME = aliveTime;
    }

    // 生成token，考虑到活跃用户过期体验，返回普通token和refresh_token的拼接
    public static String createToken(String userId) throws JsonProcessingException, UnsupportedEncodingException {
        // 用户id
        String infoStr = userId;

        // 设置JWT头，利用用户信息和盐生成结果
        Date date = new Date(System.currentTimeMillis() + ALIVE_TIME);
        Date refreshDate = new Date(System.currentTimeMillis() + 2 * ALIVE_TIME); // refresh token过期时间为两倍
        Algorithm algorithm = Algorithm.HMAC256(SALT);
        Map<String, Object> heads= new HashMap<>();
        heads.put("typ", "JWT");
        heads.put("alg", "HS256");

        return JWT.create().withHeader(heads).withClaim("userId", infoStr).withExpiresAt(date).sign(algorithm) + ";" +
                JWT.create().withHeader(heads).withClaim("userId", infoStr).withExpiresAt(refreshDate).sign(algorithm);
    }

    // 验证单JWT token
    public static boolean verifyToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SALT);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (JWTVerificationException e){
            return false;
        }
    }

    // 返回解码id
    public static String getDecodedId(String token){
        DecodedJWT dJwt = JWT.decode(token);
        String userId = dJwt.getClaim("userId").asString();
        return userId;
    }
}
