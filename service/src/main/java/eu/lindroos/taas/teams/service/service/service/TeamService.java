package eu.lindroos.taas.teams.service.service.service;

import eu.lindroos.taas.teams.service.service.controller.exception.UnauthorizedException;
import eu.lindroos.taas.teams.service.service.database.TeamInvitationRepository;
import eu.lindroos.taas.teams.service.service.database.TeamMemberRepository;
import eu.lindroos.taas.teams.service.service.database.TeamRepository;
import eu.lindroos.taas.teams.service.service.database.TeamVersionRepository;
import eu.lindroos.taas.teams.service.service.database.model.Team;
import eu.lindroos.taas.teams.service.service.database.model.TeamInvitation;
import eu.lindroos.taas.teams.service.service.database.model.TeamMember;
import eu.lindroos.taas.teams.service.service.database.model.TeamVersion;
import eu.lindroos.taas.teams.service.service.security.exception.InvalidRightsException;
import eu.lindroos.taas.teams.service.service.security.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Andreas on 13.4.2019
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamVersionRepository teamVersionRepository;
    private final TeamInvitationRepository teamInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public Team createTeam(String teamName, String teamMotto, String teamDescription, UUID teamCreator) {

        Team team = teamRepository.save(Team.builder()
                .created(Instant.now())
                .id(UUID.randomUUID())
                .teamDescription(teamDescription)
                .teamMotto(teamMotto)
                .build());

        TeamVersion tVersion = teamVersionRepository.save(TeamVersion.builder()
                .created(Instant.now())
                .name(teamName)
                .team(team)
                .changeKey(TeamVersion.ChangeKey.TEAM_CREATED)
                .changeValue(teamName)
                .build());
        team.setTeamVersion(tVersion);
        team = teamRepository.save(team);

        addTeamMember(team.getId(), teamCreator, TeamMember.TeamRole.CAPTAIN);

        log.info("Created team " + team.getId());
        return teamRepository.getById(team.getId());
    }

    public TeamMember addTeamMember(UUID teamId, UUID memberId, TeamMember.TeamRole teamRole) {
        Team team = teamRepository.findById(teamId).get();
        TeamVersion teamVersion = team.getTeamVersion();
        List<TeamMember> oldTeamMembers = teamVersion.getTeamMembers();
        if (oldTeamMembers == null) {
            oldTeamMembers = new ArrayList<>();
        }

        teamVersion = teamVersionRepository.save(TeamVersion.builder()
                .teamMembers(new ArrayList<>())
                .name(teamVersion.getName())
                .team(team)
                .created(Instant.now())
                .changeKey(TeamVersion.ChangeKey.MEMBER_ADDED)
                .changeValue(memberId.toString())
                .build());

        team.setTeamVersion(teamVersion);
        team = teamRepository.save(team);

        List<TeamMember> newTeamMembers = new ArrayList<>();
        for (TeamMember member : oldTeamMembers) {
            newTeamMembers.add(new TeamMember(UUID.randomUUID(), member.getUserId(), teamVersion, member.getTeamRole()));
        }

        UUID newMemberId = UUID.randomUUID();
        newTeamMembers.add(new TeamMember(newMemberId, memberId, teamVersion, teamRole));


        for (TeamMember member : teamMemberRepository.saveAll(newTeamMembers)) {
            if (member.getId().equals(newMemberId)) {
                return member;
            }
        }

        throw new RuntimeException("Failed to add team member " + memberId + " to team " + team.getId());

    }

    public TeamMember getTeamMemberByVersion(UUID userId, Long teamVersionId) {
        return teamMemberRepository.findByUserIdAndTeamVersionId(userId, teamVersionId);
    }

    public TeamMember getTeamMemberByTeam(UUID userId, UUID teamId) {
        List<TeamMember> members = teamRepository.findById(teamId).get().getTeamVersion().getTeamMembers().stream().filter(member -> member.getUserId().equals(userId)).collect(Collectors.toList());
        return members.size() == 1 ? members.get(0) : null;
    }

    @Transactional
    public TeamInvitation inviteTeamMember(UUID teamId, UUID invitee, UUID requesterId) throws InvalidRightsException {
        Team team = teamRepository.getById(teamId);

        TeamMember requester = getTeamMemberByVersion(requesterId, team.getTeamVersion().getId());
        if (requester == null || requester.getTeamRole() != TeamMember.TeamRole.CAPTAIN) {
            throw new InvalidRightsException("Team invite sending failed, member " + requesterId + " does not have enough rights to invite people");
        }

        for (TeamMember teamMember : team.getTeamVersion().getTeamMembers()) {
            if (teamMember.getUserId().equals(invitee)) {
                log.info("Member " + invitee + " is already part of team " + teamId);
                return null;
            }
        }

        if (teamInvitationRepository.getByTeamIdAndMemberId(teamId, invitee) != null) {
            log.info("Member " + invitee + " is already invited " + teamId);
            return null;
        }

        return teamInvitationRepository.save(TeamInvitation.builder()
                .id(UUID.randomUUID())
                .memberId(invitee)
                .team(team)
                .build());
    }

    public List<TeamInvitation> getMemberTeamInvitations(UUID memberId) {
        return teamInvitationRepository.getByMemberId(memberId);
    }

    @Transactional
    public TeamMember acceptInvitation(UUID invitationId, UUID memberId) {

        TeamInvitation invitation = teamInvitationRepository.findById(invitationId).orElse(null);

        if (invitation == null) {
            throw new NotFoundException("Could not find invitation ID " + invitationId);
        }

        if (!invitation.getMemberId().equals(memberId)) {
            throw new InvalidRightsException("Member " + memberId + " cannot accept invitation " + invitation.getId());
        }

        TeamMember teamMember = addTeamMember(invitation.getTeam().getId(), invitation.getMemberId(), TeamMember.TeamRole.REGULAR);

        teamInvitationRepository.delete(invitation);

        log.info("Team member " + memberId + " accepted invite to " + invitation.getTeam().getId());

        return teamMember;
    }

    public void rejectInvite(UUID invitationId, UUID userId) {
        TeamInvitation invite = teamInvitationRepository.findById(invitationId).orElse(null);
        if (invite == null) {
            log.info("No invite " + invitationId + " found");
        } else if (!invite.getMemberId().equals(userId) &&
                !invite.getTeam().getTeamVersion().getTeamMembers().stream().anyMatch(member -> member.getUserId().equals(userId) && member.getTeamRole() == TeamMember.TeamRole.CAPTAIN)) {
            throw new UnauthorizedException("Invitation reject: User " + userId + " does not own invitation " + invitationId);
        } else {
            log.info("Rejecting/deleing invite " + invitationId);
            teamInvitationRepository.delete(invite);
        }
    }

    public Page<Team> getTeams(int page, int size) {
        return teamRepository.findAll(PageRequest.of(page, size));
    }

    public Team getTeam(UUID teamId) {
        return teamRepository.findById(teamId).orElse(null);
    }

    public List<TeamInvitation> getTeamInvites(UUID teamId, UUID userId) {
        TeamMember member = getTeamMemberByTeam(userId, teamId);
        if (member == null || !member.getTeamRole().equals(TeamMember.TeamRole.CAPTAIN)) {
            throw new UnauthorizedException("Failed to verify user " + userId + " being CAPTAIN in " + teamId);
        }
        return teamInvitationRepository.findByTeamId(teamId).orElse(new ArrayList<>());
    }

    @Transactional
    public void removeTeamMember(UUID teamId, UUID userId, UUID loggedInUser) {
        Team team = teamRepository.findById(teamId).orElse(null);
        if (loggedInUser == null) {
            throw new UnauthorizedException("Failed to verify user being logged in");
        } else if (!userId.equals(loggedInUser) &&
                !team.getTeamVersion().getTeamMembers().stream().anyMatch(member -> member.getUserId().equals(loggedInUser) && member.getTeamRole() == TeamMember.TeamRole.CAPTAIN)) {
            throw new UnauthorizedException("Failed to verify user " + userId + " being CAPTAIN in " + teamId + " or being himself");
        }
        removeTeamMember(teamId, userId);
        log.info("Removed userId " + userId + " from team " + teamId);
    }

    private void removeTeamMember(UUID teamId, UUID userId) {
        Team team = teamRepository.findById(teamId).get();
        TeamVersion teamVersion = team.getTeamVersion();
        List<TeamMember> oldTeamMembers = teamVersion.getTeamMembers().stream().filter(member -> !member.getUserId().equals(userId)).collect(Collectors.toList());

        if (oldTeamMembers.size() == team.getTeamVersion().getTeamMembers().size()) {
            log.warn("Team " + teamId + " did not have member " + userId);
            return;
        }

        List<TeamMember> newTeamMembers = new ArrayList<>();
        for (TeamMember member : oldTeamMembers) {
            newTeamMembers.add(new TeamMember(UUID.randomUUID(), member.getUserId(), null, member.getTeamRole()));
        }

        if (newTeamMembers.stream().noneMatch(member -> member.getTeamRole() == TeamMember.TeamRole.CAPTAIN)) {
            throw new RuntimeException("Cannot remove user id " + userId + " from team " + teamId + ", since there would be no captain left");
        }

        final TeamVersion newTeamVersion = teamVersionRepository.save(TeamVersion.builder()
                .teamMembers(new ArrayList<>())
                .name(teamVersion.getName())
                .team(team)
                .created(Instant.now())
                .changeKey(TeamVersion.ChangeKey.MEMBER_REMOVED)
                .changeValue(userId.toString())
                .build());

        team.setTeamVersion(newTeamVersion);
        teamRepository.save(team);

        newTeamMembers.forEach(member -> member.setTeamVersion(newTeamVersion));

        teamMemberRepository.saveAll(newTeamMembers);
    }

    public Page<TeamVersion> getTeamVersions(UUID teamId, int page, int pageSize) {
        return teamVersionRepository.getByTeamId(teamId, PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("id"))));
    }

    public Team getTeamVersion(UUID teamId, Long versionId) {
        TeamVersion version = teamVersionRepository.getByTeamIdAndId(teamId, versionId);
        version.getTeam().setTeamVersion(version);
        return version.getTeam();
    }

    public List<Team> getTeamsByUserId(UUID userId) {
        return teamRepository.getTeamsByTeamVersionTeamMembersUserId(userId);
    }

    public List<TeamVersion> getTeamsByVersions(List<Long> versions) {
        return teamVersionRepository.getByIdIn(versions);
    }

    public List<Team> getTeamsByIds(List<UUID> teamIds) {
        return teamRepository.getTeamByIdIn(teamIds);
    }
}
