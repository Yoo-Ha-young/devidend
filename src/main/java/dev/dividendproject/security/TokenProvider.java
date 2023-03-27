package dev.dividendproject.security;

import dev.dividendproject.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1hour
    private static final String KEY_ROLES = "roles";
    private final MemberService memberService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성(발급)
     * @param username
     * @param roles
     * @return
     * */
    public String generateToken(String username, List<String> roles){
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();

        // 토큰이 유효한 시간(만료시간지정)
        var expireDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expireDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS256, this.secretKey) // 사용할 암호화 알고리즘, 비밀키
                .compact();
    }

    public Authentication getAuthentication(String jwt){
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }


    public String getUsername(String token){
        return this.parseClaims(token).getSubject();
    }


    public boolean validateToken(String token){
        if(!StringUtils.hasText(token)) {
            return false;
        }
        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());
    }


    private Claims parseClaims(String token){
        try{
        return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e){
            // TODO
            return e.getClaims();
        }
    }
}

/*
* 암호화 알고리즘
* 암호화 vs 복호화
* 대칭키 vs 비대칭키
* */