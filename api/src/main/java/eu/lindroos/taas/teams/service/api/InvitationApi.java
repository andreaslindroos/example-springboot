package eu.lindroos.taas.teams.service.api;

import eu.lindroos.taas.teams.service.api.models.InvitationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

/**
 * Created by Andreas on 19.4.2019
 */
@RequestMapping("invitations")
public interface InvitationApi {
    @RequestMapping(method = RequestMethod.GET, value = "members")
    List<InvitationResponse> getMemberTeamInvitation(@RequestHeader("Authorization") String authHeader);

    @RequestMapping(method = RequestMethod.POST, value = "teams/{teamId}/invites/{inviteeId}/invite")
    ResponseEntity<InvitationResponse> inviteMember(@RequestHeader("Authorization") String authHeader, @PathVariable("teamId") UUID teamId, @PathVariable("inviteeId") UUID inviteeId);

    @RequestMapping(method = RequestMethod.GET, value = "teams/{teamId}/invites")
    ResponseEntity<List<InvitationResponse>> getTeamInvites(@RequestHeader("Authorization") String authHeader, @PathVariable("teamId") UUID teamId);

    @RequestMapping(method = RequestMethod.POST, value = "{invitationId}/accept")
    ResponseEntity<Void> acceptTeamInvite(@RequestHeader("Authorization") String authHeader, @PathVariable("invitationId") UUID invitationID);

    @RequestMapping(method = RequestMethod.POST, value = "{invitationId}/reject")
    ResponseEntity<Void> rejectTeamInvite(@RequestHeader("Authorization") String authHeader, @PathVariable("invitationId") UUID invitationID);
}
