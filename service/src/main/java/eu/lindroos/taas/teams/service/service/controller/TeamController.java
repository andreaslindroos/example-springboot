package eu.lindroos.taas.teams.service.service.controller;

import eu.lindroos.taas.teams.service.api.TeamsApi;
import eu.lindroos.taas.teams.service.api.models.*;
import eu.lindroos.taas.teams.service.service.database.model.Team;
import eu.lindroos.taas.teams.service.service.database.model.TeamMember;
import eu.lindroos.taas.teams.service.service.database.model.TeamVersion;
import eu.lindroos.taas.teams.service.service.security.SecurityHelper;
import eu.lindroos.taas.teams.service.service.service.TeamImageService;
import eu.lindroos.taas.teams.service.service.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Andreas on 13.4.2019
 */
@RestController
@RequestMapping("teams")
@Log4j2
@RequiredArgsConstructor
public class TeamController implements TeamsApi {

    private final TeamService teamService;
    private final TeamImageService teamImageService;

    @Override
    public ResponseEntity<TeamCreateResponse> createTeam(@RequestHeader(value = "Authorization", required = true) String authHeader, TeamCreateRequest teamCreateRequest) {
        UUID loggedInUser = SecurityHelper.getLoggedInId("creating team");
        if (loggedInUser == null) {
            log.warn("user not logged in while creating team");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Team t = teamService.createTeam(teamCreateRequest.getTeamName(), teamCreateRequest.getTeamMotto(), teamCreateRequest.getTeamDescription(), loggedInUser);
        return ResponseEntity.ok(TeamCreateResponse.builder()
                .teamId(t.getId())
                .teamName(t.getTeamVersion().getName())
                .members(t.getTeamVersion().getTeamMembers().stream()
                        .map(member -> MemberResponse.builder().memberId(member.getUserId()).build())
                        .collect(Collectors.toList())).build());
    }

    @Override
    public ResponseEntity<BrowsePage<TeamSimpleResponse>> getTeams(@RequestParam int page, @RequestParam int size) {
        Page<Team> teams = teamService.getTeams(page, size);
        BrowsePage<TeamSimpleResponse> response = new BrowsePage<>();
        response.setItems(teams.get().map(t -> new TeamSimpleResponse(t.getId(), t.getTeamVersion().getName())).collect(Collectors.toList()));
        response.setPage(teams.getNumber());
        response.setSize(teams.getSize());
        response.setTotalItems(teams.getTotalElements());
        response.setTotalPages(teams.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TeamDetailedResponse> getTeam(@PathVariable("teamId") UUID teamId) {
        Team team = teamService.getTeam(teamId);
        TeamDetailedResponse teamDetailedResponse = getTeamDetailedResponse(team);
        teamDetailedResponse.setTeamLogos(teamImageService.getTeamLogos(teamId));
        return ResponseEntity.ok(teamDetailedResponse);
    }

    private TeamDetailedResponse getTeamDetailedResponse(Team team) {
        return TeamDetailedResponse.builder()
                .teamId(team.getId())
                .teamMotto(team.getTeamMotto())
                .teamDescription(team.getTeamDescription())
                .teamName(team.getTeamVersion().getName())
                .teamVersion(team.getTeamVersion().getId())
                .teamMembers(team.getTeamVersion().getTeamMembers().stream().map(member -> TeamMemberResponse.builder()
                        .memberId(member.getUserId())
                        .teamRole(member.getTeamRole().toString())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    @Override
    @RequestMapping(value = "/{teamId}/members/{userId}/remove", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeTeamMember(@RequestHeader("Authorization") String authHeader, @PathVariable("teamId") UUID teamId, @PathVariable("userId") UUID userId) {
        UUID loggedInUser = SecurityHelper.getLoggedInId("kicking team member");
        if (loggedInUser == null) {
            log.warn("user not logged in while creating team");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        teamService.removeTeamMember(teamId, userId, loggedInUser);
        return ResponseEntity.ok().build();
    }

    @Override
    @RequestMapping(value = "/{teamId}/versions/", method = RequestMethod.GET)
    public ResponseEntity<BrowsePage<TeamVersionResponse>> getTeamVersions(@PathVariable("teamId") UUID teamId, @RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        Page<TeamVersion> teamVersions = teamService.getTeamVersions(teamId, page, pageSize);


        BrowsePage response = BrowsePage.builder()
                .totalItems(teamVersions.getTotalElements())
                .size(teamVersions.getSize())
                .page(teamVersions.getNumber())
                .totalPages(teamVersions.getTotalPages())
                .build();

        response.setItems(teamVersions.getContent().stream().map(version ->
                TeamVersionResponse.builder()
                        .changeKey(version.getChangeKey().name())
                        .changeValue(version.getChangeValue())
                        .versionId(version.getId())
                        .timestamp(version.getCreated())
                        .build()).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @Override
    @RequestMapping(value = "/{teamId}/versions/{versionId}", method = RequestMethod.GET)
    public ResponseEntity<TeamDetailedResponse> getTeamVersion(@PathVariable("teamId") UUID teamId, @PathVariable("versionId") Long versionId) {
        return ResponseEntity.ok(getTeamDetailedResponse(teamService.getTeamVersion(teamId, versionId)));
    }

    @Override
    @RequestMapping(value = "/versions", method = RequestMethod.POST)
    public List<TeamVersionSimpleResponse> getTeamByVersions(@RequestBody TeamVersionRequest teamVersionRequest) {
        return teamService.getTeamsByVersions(teamVersionRequest.getTeamVersions()).stream().map(version -> TeamVersionSimpleResponse.builder()
                .teamVersion(version.getId())
                .teamName(version.getName())
                .build()).collect(Collectors.toList());
    }

    @Override
    @RequestMapping(value = "teamMembers/userId/{userId}", method = RequestMethod.GET)
    public List<TeamSimpleResponse> getTeamByUserId(@PathVariable("userId") UUID userId, @RequestParam(value = "isCaptain", required = false) Boolean isCaptain) {
        List<Team> teams = teamService.getTeamsByUserId(userId);
        if (isCaptain != null) {
            // Filter out where user is not a team captain
            teams = teams.stream().filter(team ->
                    team.getTeamVersion().getTeamMembers().stream().anyMatch(member -> member.getUserId().equals(userId) && ((isCaptain && member.getTeamRole() == TeamMember.TeamRole.CAPTAIN) || (!isCaptain && member.getTeamRole() != TeamMember.TeamRole.CAPTAIN))
                    )).collect(Collectors.toList());
        }
        return teams.stream().map(team -> new TeamSimpleResponse(team.getId(), team.getTeamVersion().getName())).collect(Collectors.toList());
    }

    @Override
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public List<TeamSimpleResponse> getTeamsByIds(List<UUID> teamIds) {
        List<Team> teams = teamService.getTeamsByIds(teamIds);
        return teams.stream().map(team -> new TeamSimpleResponse(team.getId(), team.getTeamVersion().getName())).collect(Collectors.toList());
    }

    @Override
    @RequestMapping(value = "teams/{teamId}/logo", method = RequestMethod.PUT)
    public ResponseEntity uploadTeamLogo(@RequestHeader(value = "Authorization", required = true) String authHeader, @PathVariable("teamId") UUID teamId, @RequestPart(value = "file", required = true) MultipartFile file) {
        UUID loggedInUser = SecurityHelper.getLoggedInId("updating team logo");
        if (loggedInUser == null) {
            log.warn("user not logged in while uploading team logo");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        teamImageService.updateTeamLogo(loggedInUser, teamId, file);
        return ResponseEntity.ok().build();
    }

}
