package eu.lindroos.taas.teams.service.service.external.steam;

import lombok.Data;

import java.util.List;

/**
 * Created by bege on 8.4.2019
 */
@Data
public class SteamUserResponse {
    private ResponseBody response;

    @Data
    class ResponseBody {
        private List<SteamUser> players;
    }

    /**
     * Created by bege on 8.4.2019
     */
    @Data
    public static class SteamUser {
        private String steamid;
        private String avatarfull;
        private String profileurl;
        private String personaname;
        private String avatarmedium;
        private String avatar;
    }
}
