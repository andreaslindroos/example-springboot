package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by Andreas on 24.4.2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberResponse {
    private UUID memberId;
    private String teamRole;
}
