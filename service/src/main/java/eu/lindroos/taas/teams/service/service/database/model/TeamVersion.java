package eu.lindroos.taas.teams.service.service.database.model;

import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

/**
 * Created by Andreas on 9.4.2019
 */
@Entity
@Data
@Immutable // Blocks any updates to rows
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    @Size(min = 4, max = 32)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @ToString.Exclude
    private Team team;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "teamVersion")
    List<TeamMember> teamMembers;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChangeKey changeKey;

    @Column(nullable = false)
    private String changeValue;

    public enum ChangeKey {
        TEAM_CREATED, MEMBER_REMOVED, MEMBER_ADDED
    }
}
