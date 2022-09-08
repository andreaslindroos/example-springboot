package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by Andreas on 23.4.2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamSimpleResponse {
    private UUID teamId;
    private String teamName;
}
