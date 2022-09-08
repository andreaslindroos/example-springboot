package eu.lindroos.taas.teams.service.service.controller;

import eu.lindroos.taas.teams.service.api.InvitationApi;
import eu.lindroos.taas.teams.service.api.models.InvitationResponse;
import eu.lindroos.taas.teams.service.service.database.model.TeamInvitation;
import eu.lindroos.taas.teams.service.service.security.SecurityHelper;
import eu.lindroos.taas.teams.service.service.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Andreas on 14.4.2019
 */
@RestController
@RequestMapping("invitations")
@RequiredArgsConstructor
public class InvitationController implements InvitationApi {

    private final TeamService teamService;

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "members")
    public List<InvitationResponse> getMemberTeamInvitation(@RequestHeader("Authorization") String authHeader) {
        UUID memberId = SecurityHelper.getLoggedInId("Failed to get logged in user when handling team invitations");
        return teamService.getMemberTeamInvitations(memberId).stream().map(invite -> new InvitationResponse(invite.getId(), invite.getMemberId(), MapperUtil.getTeamSimpleResponse(invite.getTeam()))).collect(Collectors.toList());
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "teams/{teamId}/invites/{inviteeId}/invite")
    public ResponseEntity<InvitationResponse> inviteMember(@RequestHeader("Authorization") String authHeader, @PathVariable("teamId") UUID teamId, @PathVariable("inviteeId") UUID inviteeId) {
        UUID memberId = SecurityHelper.getLoggedInId("Failed to get login details on invitation request");
        TeamInvitation invitation = teamService.inviteTeamMember(teamId, inviteeId, memberId);
        return ResponseEntity.ok(
                new InvitationResponse(invitation.getId(), invitation.getMemberId(), MapperUtil.getTeamSimpleResponse(invitation.getTeam())));
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "teams/{teamId}/invites")
    public ResponseEntity<List<InvitationResponse>> getTeamInvites(@RequestHeader("Authorization") String authHeader, @PathVariable("teamId") UUID teamId) {
        UUID memberId = SecurityHelper.getLoggedInId("Failed to get login details on team invitations request");
        List<TeamInvitation> invites = teamService.getTeamInvites(teamId, memberId);
        return ResponseEntity.ok(invites.stream().map(invitation ->
                new InvitationResponse(invitation.getId(), invitation.getMemberId(), MapperUtil.getTeamSimpleResponse(invitation.getTeam()))
        ).collect(Collectors.toList()));
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "{invitationId}/accept")
    public ResponseEntity<Void> acceptTeamInvite(@RequestHeader("Authorization") String authHeader, @PathVariable("invitationId") UUID invitationID) {
        UUID memberId = SecurityHelper.getLoggedInId("Failed to get login details on invitation response");
        teamService.acceptInvitation(invitationID, memberId);
        return ResponseEntity.ok().build();
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "{invitationId}/reject")
    public ResponseEntity<Void> rejectTeamInvite(@RequestHeader("Authorization") String authHeader, @PathVariable("invitationId") UUID invitationID) {
        UUID memberId = SecurityHelper.getLoggedInId("Failed to get login details on invitation response");
        teamService.rejectInvite(invitationID, memberId);
        return ResponseEntity.ok().build();
    }
}
