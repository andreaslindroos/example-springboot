package eu.lindroos.taas.teams.service.service.database;

import eu.lindroos.taas.teams.service.service.database.model.TeamVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by Andreas on 13.4.2019
 */
public interface TeamVersionRepository extends PagingAndSortingRepository<TeamVersion, Long> {
    Page<TeamVersion> getByTeamId(UUID teamId, Pageable pageable);

    TeamVersion getByTeamIdAndId(UUID teamId, Long id);

    List<TeamVersion> getByIdIn(List<Long> teamVersions);
}
