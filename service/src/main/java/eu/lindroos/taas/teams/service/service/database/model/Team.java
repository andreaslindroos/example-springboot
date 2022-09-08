package eu.lindroos.taas.teams.service.service.database.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by Andreas on 9.4.2019
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Team {
    @Id
    private UUID id;

    @Column(nullable = false)
    private Instant created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @ToString.Exclude
    private TeamVersion teamVersion;

    @Column(length = 2048)
    private String teamDescription;

    @Column(length = 64)
    private String teamMotto;

    // Optimistic locking with one annotation
    @Version
    private Integer version;
}
