package eu.lindroos.taas.teams.service.service.database;

import eu.lindroos.taas.teams.service.service.database.model.TeamMember;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Created by Andreas on 19.4.2019
 */
public interface TeamMemberRepository extends CrudRepository<TeamMember, UUID> {
    TeamMember findByUserIdAndTeamVersionId(UUID userId, Long teamVersionId);

    TeamMember findByUserIdAndTeamVersionTeamId(UUID userId, UUID teamId);
}
