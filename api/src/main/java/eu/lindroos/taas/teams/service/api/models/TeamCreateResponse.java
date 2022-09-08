package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Created by Andreas on 9.4.2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCreateResponse {
    private UUID teamId;
    private String teamName;
    private List<MemberResponse> members;
}
