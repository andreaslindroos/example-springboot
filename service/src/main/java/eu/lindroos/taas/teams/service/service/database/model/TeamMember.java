package eu.lindroos.taas.teams.service.service.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Andreas on 19.4.2019
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {

    @Id
    UUID id;

    @Column(nullable = false)
    UUID userId;

    @ManyToOne
    @JoinColumn(nullable = false)
    TeamVersion teamVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TeamRole teamRole;

    public enum TeamRole {
        CAPTAIN, REGULAR
    }
}
