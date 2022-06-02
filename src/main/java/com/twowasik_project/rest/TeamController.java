package com.twowasik_project.rest;

import com.twowasik_project.dto.CreateTeamDto;
import com.twowasik_project.dto.ShowTeamDto;
import com.twowasik_project.exceptions.InvalidTokenExceptions;
import com.twowasik_project.jwt.JWTProvider;
import com.twowasik_project.model.Team;
import com.twowasik_project.model.User;
import com.twowasik_project.repository.TeamRepository;
import com.twowasik_project.repository.UserRepository;
import com.twowasik_project.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000/", maxAge = 3600)
@RestController
@RequestMapping(value = "/teams/")
public class TeamController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private ShowTeamDto showTeamDto;

    @PostMapping("create")
    public ResponseEntity createTeam(HttpServletRequest request, @RequestBody CreateTeamDto CreateTeamDto) {

        if (!jwtProvider.validateAccessToken(request.getHeader("Authorization"))) {
            throw new InvalidTokenExceptions();
        }

        User admin = userRepository.findByUsername(jwtProvider.getAccessClaims(request.getHeader("Authorization")).getSubject());
        String members = admin.getEmail() + " " + CreateTeamDto.getTeam_participants();

        teamService.saveTeam(new Team(CreateTeamDto.getName(), members, new ArrayList<>(), admin));
        return ResponseEntity.ok(true);
    }

    @GetMapping("showTeams")
    public ResponseEntity showTeams(HttpServletRequest request) {

        if (!jwtProvider.validateAccessToken(request.getHeader("Authorization"))) {
            throw new InvalidTokenExceptions();
        }

        List<Integer> id = new ArrayList<>();
        List<String> name = new ArrayList<>();

        for(Team team: teamRepository.getAll()) {
            id.add(team.getId());
            name.add(team.getName());
        }

        showTeamDto.setId(id);
        showTeamDto.setName(name);
        return ResponseEntity.ok(showTeamDto);
    }
}