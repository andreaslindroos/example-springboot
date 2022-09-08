package eu.lindroos.taas.teams.service.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Andreas on 13.4.2019
 */
@Component
public class JwtTokenAuthFilter extends OncePerRequestFilter {

    public static String AUTH_TOKEN = "aksdnkenrkcjnasdfasfwefqwfqwrfaewrfarfarfeaweerkjnakjncjkansdk";
    private final PublicKey publicKey;


    public JwtTokenAuthFilter() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Load public key (used for JWT tokens)
        byte[] keyBytes = Files.readAllBytes(Paths.get("public_jwt_key.der"));
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        publicKey = kf.generatePublic(pubSpec);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = httpServletRequest.getHeader("Authorization");

            if (StringUtils.isEmpty(jwtToken)) {
                logger.debug("No jwt token provided");
            } else if (!jwtToken.startsWith("Bearer")) {
                logger.warn("Invalid token prefix: " + jwtToken);
            } else {
                // Try authenticating

                // This will throw exception if auth fails:
                Claims claims = Jwts.parser()
                        .setSigningKey(AUTH_TOKEN)
                        .parseClaimsJws(jwtToken.substring(6))
                        .getBody();

                // Parse user info + set request as authenticated
                String memberId = claims.getSubject();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(memberId, null);
                logger.info("Authenticated " + memberId);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Throwable t) {
            logger.error("Exception while handling jwttoken", t);
            // Something bad happened, better clear any auth, just to be safe:
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
