package eu.lindroos.taas.teams.service.service.controller;

import eu.lindroos.taas.teams.service.api.models.TeamSimpleResponse;
import eu.lindroos.taas.teams.service.service.database.model.Team;

/**
 * Created by Andreas on 27.4.2019
 */
public class MapperUtil {
    public static TeamSimpleResponse getTeamSimpleResponse(Team team) {
        return new TeamSimpleResponse(team.getId(), team.getTeamVersion().getName());
    }
}
