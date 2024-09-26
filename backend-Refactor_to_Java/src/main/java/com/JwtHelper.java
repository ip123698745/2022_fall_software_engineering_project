package com;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Configuration
public class JwtHelper {

    public String generateToken(String account, String token, String authority) throws NoSuchAlgorithmException {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] stringKey = ("48521562@2f65r6@555&&^#").getBytes();
        byte[] encodedKey= Base64.getUrlEncoder().encode(stringKey);
        Key signingKey = new SecretKeySpec(encodedKey,0,encodedKey.length, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder().setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setSubject(account)
                .setIssuer("JwtAuth")
                .setExpiration(Date.from(now.toInstant().plus(10, ChronoUnit.MINUTES)))
                .signWith(signingKey, signatureAlgorithm);
        return builder.compact();
    }

    // Header 的 Authorization 的格式
    // Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4OTAyYjc2NS1kYmY1LTRkNmEtYTBlNy1jYTU5MjhlOWY3NGYiLCJpYXQiOjE2NzE2OTI0MDUsInN1YiI6ImdpdGh1Yl9hMDkzNTIxMDU3MDYwMiIsImlzcyI6Ikp3dEF1dGgiLCJleHAiOjE2NzE2OTMwMDV9.S6UndWB5ThxL2f6-nbtZ8ltia7KFrPCkF09BPv29-0k
    public JSONObject validateToken(String token) {
        String temp = token.split(" ")[1];

        String[] chunks = temp.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));

        return new JSONObject(payload);
    }
}
