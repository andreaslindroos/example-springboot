package eu.lindroos.taas.teams.service.service.controller;

import eu.lindroos.taas.teams.service.service.security.JwtTokenAuthFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
public class TokenCreator {

    @GetMapping("getToken")
    @ApiOperation("Generates a bearer for debugging (not prod). User UUID can be found from team info endpoints.")
    public String getToken(@RequestParam String userId) {

        return "Bearer " + Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(Instant.now().plus(2, ChronoUnit.DAYS).toEpochMilli()))
                .signWith(SignatureAlgorithm.HS256, JwtTokenAuthFilter.AUTH_TOKEN)
                .compact();
    }
}
