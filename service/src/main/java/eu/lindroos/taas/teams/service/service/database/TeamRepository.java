package eu.lindroos.taas.teams.service.service.database;

import eu.lindroos.taas.teams.service.service.database.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by Andreas on 13.4.2019
 */
public interface TeamRepository extends PagingAndSortingRepository<Team, UUID> {
    Team getById(UUID id);

    // Pageable result, which automatically splits the results into pages
    Page<Team> findAll(Pageable pageable);

    List<Team> getTeamsByTeamVersionTeamMembersUserId(UUID userId);

    List<Team> getTeamByIdIn(List<UUID> teamIds);

    // Regular SQL search, that automatically parses to the Team model. We can join stuff and define custom POJOs for results as well or simply return a Map.
    @Query(value = "select * from team where id = :id", nativeQuery = true)
    Team findCustomTeamSearchSQL(UUID id);

    // JQL search: SQL like searches, that allows using Java models (note "Team" refers to the java model)
    @Query(value = "from Team where id = :id", nativeQuery = false)
    Team findCustomTeamSearchJQL(UUID id);


}
