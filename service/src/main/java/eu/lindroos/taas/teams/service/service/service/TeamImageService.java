package eu.lindroos.taas.teams.service.service.service;

import eu.lindroos.ladder.images.api.Context;
import eu.lindroos.taas.teams.service.api.models.TeamLogo;
import eu.lindroos.taas.teams.service.service.database.model.TeamMember;
import eu.lindroos.taas.teams.service.service.external.ImageApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Andreas on 21.8.2019
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class TeamImageService {
    private final TeamService teamService;
    private final ImageApi imageApi;

    public void updateTeamLogo(UUID userId, UUID teamId, MultipartFile file) {
        TeamMember teamMember = teamService.getTeamMemberByTeam(userId, teamId);

        if (teamMember == null || teamMember.getTeamRole() != TeamMember.TeamRole.CAPTAIN) {
            log.warn("User " + userId + " tried updating image for team " + teamId + ". Insufficient rights!");
            throw new InsufficientAuthenticationException("Insufficient rights!");
        }

        imageApi.uploadImage(Context.TEAMS, teamId.toString(), file);
    }

    public List<TeamLogo> getTeamLogos(UUID teamId) {
        return imageApi.getAvailableImages(Context.TEAMS, teamId.toString()).getBody().stream().map(image ->
                        TeamLogo.builder()
                                .fileName(image.getFileName())
                                .size(image.getSize())
                                .build())
                .collect(Collectors.toList());
    }
}
