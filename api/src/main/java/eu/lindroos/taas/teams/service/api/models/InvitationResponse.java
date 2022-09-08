package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by Andreas on 19.4.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationResponse {
    private UUID inviteId;
    private UUID userId;
    private TeamSimpleResponse team;
}
