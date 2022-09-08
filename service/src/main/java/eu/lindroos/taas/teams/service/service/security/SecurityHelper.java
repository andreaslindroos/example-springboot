package eu.lindroos.taas.teams.service.service.security;

import eu.lindroos.taas.teams.service.service.controller.exception.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Created by Andreas on 14.4.2019
 */
public class SecurityHelper {
    public static UUID getLoggedInId(String failMessage) throws UnauthorizedException {
        if (StringUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            throw new UnauthorizedException("Not logged in (" + failMessage + ")");
        }
        String user = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (user.equals("anonymousUser")) {
            throw new UnauthorizedException("Not logged in (" + failMessage + ")");
        }
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
}
