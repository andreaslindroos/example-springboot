package eu.lindroos.taas.teams.service.service.database;

import eu.lindroos.taas.teams.service.service.database.model.TeamInvitation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Andreas on 14.4.2019
 */
public interface TeamInvitationRepository extends CrudRepository<TeamInvitation, UUID> {
    TeamInvitation getByTeamIdAndMemberId(UUID teamId, UUID memberId);

    List<TeamInvitation> getByMemberId(UUID memberId);

    Optional<List<TeamInvitation>> findByTeamId(UUID teamId);
}
