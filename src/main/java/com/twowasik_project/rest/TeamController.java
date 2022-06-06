package com.twowasik_project.rest;

import com.twowasik_project.dto.CreateTeamDto;
import com.twowasik_project.dto.ShowTeamDto;
import com.twowasik_project.dto.TeamIdDto;
import com.twowasik_project.exceptions.InvalidTokenExceptions;
import com.twowasik_project.jwt.JWTProvider;
import com.twowasik_project.model.Team;
import com.twowasik_project.model.User;
import com.twowasik_project.service.TeamService;
import com.twowasik_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:3000/", maxAge = 3600)
@RestController
@RequestMapping(value = "/teams/")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private TeamIdDto teamIdDto;

    @Autowired
    private ShowTeamDto showTeamDto;

    @Autowired
    private User admin;

    @PostMapping("create")
    public ResponseEntity createTeam(HttpServletRequest request, @RequestBody CreateTeamDto createTeamDto) {

        if (!jwtProvider.validateAccessToken(request.getHeader("Authorization"))) {
            throw new InvalidTokenExceptions();
        }

        admin = userService.findByUsername(jwtProvider.getAccessClaims(request.getHeader("Authorization")).getSubject());
        String participantsId = userService.getUsersId(createTeamDto.getTeam_participants(), Integer.toString(admin.getId()));

        if (participantsId.equals("")) {
            System.out.println(participantsId);
            return ResponseEntity.ok(false);
        }

        teamIdDto.setId(teamService.saveTeam(new Team(createTeamDto.getName(), participantsId, Integer.toString(admin.getId()))));

        userService.addTeam(Integer.toString(teamIdDto.getId()), participantsId);

        return ResponseEntity.ok(teamIdDto);
    }

    @GetMapping("showTeams")
    public ResponseEntity showTeams(HttpServletRequest request) {

        if (!jwtProvider.validateAccessToken(request.getHeader("Authorization"))) {
            throw new InvalidTokenExceptions();
        }

        showTeamDto.setId(userService.getTeams(jwtProvider.getAccessClaims(request.getHeader("Authorization")).getSubject()));
        showTeamDto.setName(teamService.showTeams(showTeamDto.getId()));

        return ResponseEntity.ok(showTeamDto);
    }
}