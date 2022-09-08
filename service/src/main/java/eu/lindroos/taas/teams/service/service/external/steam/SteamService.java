package eu.lindroos.taas.teams.service.service.external.steam;

import com.google.common.collect.Lists;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by bege on 8.4.2019
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class SteamService {

    private final static String API_KEY = "Some steam api key";

    private final SteamClient steamClient;

    public List<SteamUserResponse.SteamUser> fetchUserInfo(List<String> steamIds) {
        List<SteamUserResponse.SteamUser> users = new ArrayList<>();

        Lists.partition(steamIds, 100).forEach(steamIdsPart -> {
            log.info("Fetching steam user info for " + steamIds.toString());
            try {
                users.addAll(steamClient.getSteamUser(API_KEY, String.join(",", steamIdsPart)).getBody().getResponse().getPlayers());
                log.debug(users);
            } catch (FeignException ex) {
                log.error("FeignException: " + ex.contentUTF8(), ex);
                throw ex;
            }
        });

        return users;

    }

    public List<SteamUserResponse.SteamUser> getFriendsList(UUID userId) {
        String steamId = "steam ID";
        log.info("Fetching friends for " + steamId);
        List<SteamFriendsResponse.SteamFriend> friendSteamId;
        try {
            // Fetch friends steam ID        
            friendSteamId = steamClient.getStreamFriends(API_KEY, steamId, "friend").getBody().getFriendslist().getFriends();
        } catch (FeignException ex) {
            log.error("FeignException: " + ex.contentUTF8(), ex);
            throw ex;
        }

        // Fetch user info for each friend
        List<SteamUserResponse.SteamUser> friends = fetchUserInfo(friendSteamId.stream().map(SteamFriendsResponse.SteamFriend::getSteamid).collect(Collectors.toList()));
        log.info("Found " + friends.size() + " friends of " + steamId);
        return friends;
    }
}
