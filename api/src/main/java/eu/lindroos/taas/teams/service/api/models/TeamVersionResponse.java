package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Created by Andreas on 3.5.2019
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamVersionResponse {
    private Long versionId;
    private String changeKey;
    private String changeValue;
    private Instant timestamp;
}
