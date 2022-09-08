package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Andreas on 9.6.2019
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamVersionSimpleResponse {
    private String teamName;
    private Long teamVersion;
}
