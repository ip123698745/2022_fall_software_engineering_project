package com.service;

import com.bean.Invitation;
import com.bean.Project;
import com.bean.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface InvitationService extends BaseService{
    Optional<Invitation> getInvitation(long id);
    Invitation createInvitation(User inviter, User applicant, Project invitedProject);
    List<Invitation> getInvitations(User user);
    void saveInvitation(Invitation invitation);
    boolean isInvitationExist(Invitation invitation);
    boolean isUserInProject(User user, Project project);
    void deleteInvitation(Invitation invitation);
}
