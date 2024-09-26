package com.controller;

import com.JwtHelper;
import com.bean.Invitation;
import com.bean.Project;
import com.bean.User;
import com.dto.RequestInvitationDto;
import com.dto.RequestReplyInvitationDto;
import com.dto.ResponseDto;
import com.dto.ResponseUserInfoDto;
import com.service.InvitationService;
import com.service.ProjectService;
import com.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
public class InvitationController {
    @Autowired
    @Qualifier("UserService")
    private UserService userService;

    @Autowired
    @Qualifier("ProjectService")
    private ProjectService projectService;

    @Autowired
    @Qualifier("InvitationService")
    private InvitationService invitationService;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping("/invitation")
    public ResponseEntity<List<Invitation>> getInvitation(@RequestHeader("Authorization") String authToken) {
        JSONObject jsonObject = jwtHelper.validateToken(authToken);
        try {
            User user = userService.getUserByAccount(jsonObject.getString("sub"));
            return ResponseEntity.ok().body(
                    invitationService.getInvitations(user)
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @GetMapping("/invitation/checkowner/{projectId}")
    public ResponseEntity<ResponseDto> isOwner(@RequestHeader("Authorization") String authToken, @PathVariable long projectId) {
        JSONObject jsonObject = jwtHelper.validateToken(authToken);
        try {
            if (projectService.isProjectOwner(projectId, jsonObject.getString("sub"))) {
                return ResponseEntity.ok().body(new ResponseDto(true, "Is project owner"));
            }
            return ResponseEntity.ok().body(new ResponseDto(false, "Not project owner"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @PostMapping("/invitation/users")
    public ResponseEntity<List<ResponseUserInfoDto>> invite(@RequestHeader("Authorization") String authToken, @RequestBody RequestInvitationDto requestInvitationDto) {
        JSONObject jsonObject = jwtHelper.validateToken(authToken);
        try {
            String account = jsonObject.getString("sub");
            if (projectService.isProjectOwner(requestInvitationDto.ProjectId, account)) {
                List<User> users = userService.getAllUsers();

                List<ResponseUserInfoDto> dtos = users.stream()
                        .filter(user -> !user.getAccount().equals(account))
                        .map(user -> new ResponseUserInfoDto(user.getAccount(), user.getName(), user.getAvatarUrl()))
                        .collect(toList());
                return ResponseEntity.ok().body(dtos);
            }
            return ResponseEntity.ok().body(
                    Collections.emptyList()
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @PostMapping("/invitation/sendinvitation")
    public ResponseEntity<ResponseDto> sendInvitation(@RequestHeader("Authorization") String authToken, @RequestBody RequestInvitationDto requestInvitationDto) {
        JSONObject jsonObject = jwtHelper.validateToken(authToken);
        try {
            String account = jsonObject.getString("sub");
            if (userService.checkUserExist(requestInvitationDto.ApplicantId)) {
                User inviter = userService.getUserByAccount(account);
                User applicant = userService.getUserByAccount(requestInvitationDto.ApplicantId);
                Project project = projectService.getProjectById(requestInvitationDto.ProjectId);
                if (projectService.isProjectOwner(requestInvitationDto.ProjectId, account) && !invitationService.isUserInProject(applicant, project)) {
                    Invitation invitation = invitationService.createInvitation(inviter, applicant, project);
                    if (!invitationService.isInvitationExist(invitation)) {
                        invitationService.saveInvitation(invitation);
                        // TODO: Notify applicant
                        //   C# Code: await _notifyHub.Clients.Groups(invitation.Applicant.Account).ReceiveNotification();
                        return ResponseEntity.ok().body(
                                new ResponseDto(true, "Send invitation")
                        );
                    } else {
                        return ResponseEntity.ok().body(
                                new ResponseDto(false, "Send invitation, don't send again")
                        );
                    }
                } else {
                    return ResponseEntity.ok().body(
                            new ResponseDto(false, "User: " + applicant.getName() + " has been project contributor")
                    );
                }
            }
            return ResponseEntity.ok().body(
                    new ResponseDto(false, "Applicant not found")
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @PostMapping("/invitation/reply")
    public ResponseEntity<ResponseDto> replyInvitation(@RequestHeader("Authorization") String authToken, @RequestBody RequestReplyInvitationDto requestReplyInvitationDto) {
        JSONObject jsonObject = jwtHelper.validateToken(authToken);
        try {
            Optional<Invitation> invitation = invitationService.getInvitation(requestReplyInvitationDto.InvitationId);
            if (invitation.isPresent()) {
                User applicant = userService.getUserByAccount(jsonObject.getString("sub"));
                invitationService.deleteInvitation(invitation.get());
                if (requestReplyInvitationDto.IsAgreed) {
                    Project project = projectService.getProjectById(invitation.get().getInvitedProject().getId());
                    project.getUsers().add(applicant);
                    projectService.saveProject(project);
                    return ResponseEntity.ok().body(
                            new ResponseDto(true, "Agreed invitation")
                    );
                }
                return ResponseEntity.ok().body(
                        new ResponseDto(true, "Reject invitation")
                );
            }
            return ResponseEntity.ok().body(
                    new ResponseDto(false, "Invitation not found")
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }
}
