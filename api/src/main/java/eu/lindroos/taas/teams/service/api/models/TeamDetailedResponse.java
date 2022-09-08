package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Created by Andreas on 24.4.2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDetailedResponse {
    private UUID teamId;
    private String teamName;
    private String teamDescription;
    private String teamMotto;
    private Long teamVersion;
    private List<TeamMemberResponse> teamMembers;
    private List<TeamLogo> teamLogos;

}
