package eu.lindroos.taas.teams.service.service.external.steam;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by bege on 8.4.2019
 */
@FeignClient(name = "steamApi", url = "https://api.steampowered.com/ISteamUser")
@ControllerAdvice()
public interface SteamClient {
    @RequestMapping(method = RequestMethod.GET, path = "/GetPlayerSummaries/v0002/")
    ResponseEntity<SteamUserResponse> getSteamUser(@RequestParam("key") String apiKey, @RequestParam("steamIds") String steamIds);

    @RequestMapping(method = RequestMethod.GET, path = "/GetFriendList/v0001/")
    ResponseEntity<SteamFriendsResponse> getStreamFriends(@RequestParam("key") String apiKey, @RequestParam("steamid") String steamIds, @RequestParam(value = "relationship", defaultValue = "friend") String relationship);

}
