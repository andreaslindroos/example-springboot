package eu.lindroos.taas.teams.service.service.external.steam;

import lombok.Data;

import java.util.List;

/**
 * Created by bege on 8.4.2019
 */
@Data
public class SteamFriendsResponse {
    private ResponseBody friendslist;

    @Data
    class ResponseBody {
        private List<SteamFriend> friends;
    }

    @Data
    public static class SteamFriend {
        private String steamid;
        private String relationship;
    }
}
