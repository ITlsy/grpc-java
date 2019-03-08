package com.lsy.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsy on 2019/3/6.
 */
public class JwtUtil {
    private static final long EXPIRE_TIME=15*60*1000;
    private static final String TOKEN_SERECT="jwt123";


    /**
     * 生成签名，15分钟过期
     * @param **username**
     * @param **password**
     * @return
     */
    public static String createJwt(String userName,String pwd){
        try{
            //过期时间
            Date date=new Date(System.currentTimeMillis()+EXPIRE_TIME);
            //私钥和加密算法
            Algorithm algorithm=Algorithm.HMAC256(TOKEN_SERECT);
            //设置头部信息
            Map<String,Object> header=new HashMap<String, Object>();
            header.put("Type","jwt");
            header.put("alg","HS256");
            //返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("loginName",userName)
                    .withClaim("pwd",pwd)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检验token是否正确
     * @param **token**
     * @return
     */
    public static boolean verify(String token){
        try {
            Algorithm algorithm=Algorithm.HMAC256(TOKEN_SERECT);
            JWTVerifier jwtVerifier=JWT.require(algorithm).build();
            DecodedJWT jwt=jwtVerifier.verify(token);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
