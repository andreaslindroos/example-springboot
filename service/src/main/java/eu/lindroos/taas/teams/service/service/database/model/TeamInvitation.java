package eu.lindroos.taas.teams.service.service.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Andreas on 14.4.2019
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamInvitation {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID memberId;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Team team;

}
