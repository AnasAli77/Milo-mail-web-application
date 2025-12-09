package com.app.milobackend.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    private final String secretKey = System.getenv("JWT_SECRET");

    //    public JWTService() throws NoSuchAlgorithmException {
//        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//        SecretKey sk = keyGen.generateKey();
//        secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//        System.out.println("secretKey: " + secretKey);
//    }

    public String generateToken(String name){
        Map<String,Object> claims = new HashMap<>();


    return Jwts.builder()
            .claims()
            .add(claims)
            .subject(name)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 60*60*1000))
            .and()
            .signWith(getSecretKey())
            .compact();
    }

    private Key getSecretKey(){
        byte[] keybytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keybytes);
    }
}
