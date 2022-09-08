package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Andreas on 9.4.2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCreateRequest {
    private String teamName;
    private String teamMotto;
    private String teamDescription;
}
