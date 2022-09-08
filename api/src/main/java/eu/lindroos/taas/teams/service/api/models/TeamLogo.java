package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Andreas on 21.8.2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamLogo {
    private String size;
    private String fileName;
}
