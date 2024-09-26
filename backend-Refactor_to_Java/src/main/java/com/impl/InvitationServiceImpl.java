package com.impl;

import com.bean.Invitation;
import com.bean.Project;
import com.bean.User;
import com.dao.InvitationRepository;
import com.service.InvitationService;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("InvitationService")
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Override
    public Optional<Invitation> getInvitation(long id) {
        return invitationRepository.findById(id);
    }

    @Override
    public Invitation createInvitation(User inviter, User applicant, Project invitedProject) {
        Invitation invitation = new Invitation();
        invitation.setInviter(inviter);
        invitation.setApplicant(applicant);
        invitation.setInvitedProject(invitedProject);
        invitation.setAgreed(false);
        return invitation;
    }

    @Override
    public List<Invitation> getInvitations(User user) {
        return invitationRepository.findByApplicantAccount(user.getAccount());
    }

    @Override
    public void saveInvitation(Invitation invitation) throws HibernateException {
        try {
            invitationRepository.save(invitation);
        } catch (HibernateException he) {
            throw he;
        }
    }

    @Override
    public boolean isInvitationExist(Invitation invitation) {
        return invitationRepository.existsById(invitation.getId());
    }

    @Override
    public boolean isUserInProject(User user, Project project) {
        return project.getUsers().contains(user);
    }

    @Override
    public void deleteInvitation(Invitation invitation) throws HibernateException {
        try {
            invitationRepository.delete(invitation);
        } catch (HibernateException he) {
            throw he;
        }
    }
}
