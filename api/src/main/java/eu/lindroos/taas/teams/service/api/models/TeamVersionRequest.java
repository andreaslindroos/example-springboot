package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Andreas on 9.6.2019
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamVersionRequest {
    private List<Long> teamVersions;
}
