package eu.lindroos.taas.teams.service.api;

import eu.lindroos.taas.teams.service.api.models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Created by Andreas on 8.4.2019
 */
@RequestMapping("teams")
public interface TeamsApi {
    @RequestMapping(method = RequestMethod.PUT)
    ResponseEntity<TeamCreateResponse> createTeam(@RequestHeader("Authorization") String authHeader, @RequestBody TeamCreateRequest teamCreateRequest);

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<BrowsePage<TeamSimpleResponse>> getTeams(@RequestParam int page, @RequestParam int size);

    @RequestMapping(value = "/{teamId}", method = RequestMethod.GET)
    ResponseEntity<TeamDetailedResponse> getTeam(@PathVariable("teamId") UUID teamId);

    @RequestMapping(value = "/{teamId}/members/{userId}/remove", method = RequestMethod.DELETE)
    ResponseEntity<Void> removeTeamMember(@RequestHeader("Authorization") String authHeader, @PathVariable("teamId") UUID teamId, @PathVariable("userId") UUID userId);

    @RequestMapping(value = "/{teamId}/versions/", method = RequestMethod.GET)
    ResponseEntity<BrowsePage<TeamVersionResponse>> getTeamVersions(@PathVariable("teamId") UUID teamId, @RequestParam("page") int page, @RequestParam("pageSize") int pageSize);

    @RequestMapping(value = "/{teamId}/versions/{versionId}", method = RequestMethod.GET)
    ResponseEntity<TeamDetailedResponse> getTeamVersion(@PathVariable("teamId") UUID teamId, @PathVariable("versionId") Long versionId);

    @RequestMapping(value = "/versions", method = RequestMethod.POST)
    List<TeamVersionSimpleResponse> getTeamByVersions(@RequestBody TeamVersionRequest teamVersionRequest);

    @RequestMapping(value = "teamMembers/userId/{userId}", method = RequestMethod.GET)
    List<TeamSimpleResponse> getTeamByUserId(@PathVariable("userId") UUID userId, @RequestParam("isCaptain") Boolean isCaptain);

    @RequestMapping(value = "search", method = RequestMethod.POST)
    List<TeamSimpleResponse> getTeamsByIds(@RequestBody List<UUID> teamIds);

    @RequestMapping(value = "teams/{teamId}/logo", method = RequestMethod.PUT, consumes = "multipart/form-data")
    ResponseEntity uploadTeamLogo(@RequestHeader(value = "Authorization", required = true) String authHeader, @PathVariable("teamId") UUID teamId, @RequestPart(value = "file", required = true) MultipartFile file);
}
