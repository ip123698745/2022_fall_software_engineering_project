package com.impl;

import com.Application;
import com.bean.Invitation;
import com.bean.Project;
import com.bean.User;
import com.dao.InvitationRepository;
import com.service.InvitationService;
import org.hibernate.HibernateException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class InvitationServiceImplTest {
    @Autowired
    InvitationService invitationService;

    @MockBean
    InvitationRepository mockInvitationRepository;

    @Test
    public void testGetInvitation(){
        when(mockInvitationRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        invitationService.getInvitation(1L);
        Assert.assertEquals(1, Mockito.mockingDetails(mockInvitationRepository).getInvocations().size());
    }

    @Test
    public void testCreateInvitation() {
        User inviter = new User();
        inviter.setAccount("testa");
        User applicant = new User();
        applicant.setAccount("testb");
        Project invitedProject = new Project();
        invitedProject.setId(1);

        Invitation result = invitationService.createInvitation(inviter, applicant, invitedProject);
        Assert.assertEquals("testa", result.getInviter().getAccount());
        Assert.assertEquals("testb", result.getApplicant().getAccount());
        Assert.assertEquals(1, result.getInvitedProject().getId());
    }

    @Test
    public void testGetInvitations(){
        User user = new User();
        user.setAccount("test");
        when(mockInvitationRepository.findByApplicantAccount(any(String.class))).thenReturn(null);

        invitationService.getInvitations(user);
        Assert.assertEquals(1, Mockito.mockingDetails(mockInvitationRepository).getInvocations().size());
    }

    @Test
    public void testSaveInvitation(){
        Invitation invitation = new Invitation();
        when(mockInvitationRepository.save(any(Invitation.class))).thenReturn(null);

        invitationService.saveInvitation(invitation);
        Assert.assertEquals(1, Mockito.mockingDetails(mockInvitationRepository).getInvocations().size());
    }

    @Test
    public void testSaveInvitationWithException(){
        Invitation invitation = new Invitation();
        when(mockInvitationRepository.save(any(Invitation.class))).thenThrow(new HibernateException("test"));

        try {
            invitationService.saveInvitation(invitation);
            Assert.fail();
        } catch (HibernateException he) {
            Assert.assertEquals("test", he.getMessage());
        }
    }

    @Test
    public void testIsInvitationExist() {
        Invitation invitation = new Invitation();
        when(mockInvitationRepository.existsById(anyLong())).thenReturn(true);

        Assert.assertTrue(invitationService.isInvitationExist(invitation));
        Assert.assertEquals(1, Mockito.mockingDetails(mockInvitationRepository).getInvocations().size());
    }

    @Test
    public void testIsUserInProjectTrue() {
        Project mockProject = mock(Project.class);
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setAccount("test");
        userList.add(user);
        when(mockProject.getUsers()).thenReturn(userList);

        Assert.assertTrue(invitationService.isUserInProject(user, mockProject));
    }

    @Test
    public void testIsUserInProjectFalse() {
        Project mockProject = mock(Project.class);
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setAccount("test");
        when(mockProject.getUsers()).thenReturn(userList);

        Assert.assertFalse(invitationService.isUserInProject(user, mockProject));
    }

    @Test
    public void testDeleteInvitation() {
        Invitation invitation = new Invitation();
        doNothing().when(mockInvitationRepository).delete(any(Invitation.class));

        invitationService.deleteInvitation(invitation);
        Assert.assertEquals(1, Mockito.mockingDetails(mockInvitationRepository).getInvocations().size());
    }

    @Test
    public void testDeleteInvitationWithException() {
        Invitation invitation = new Invitation();
        doThrow(new HibernateException("test")).when(mockInvitationRepository).delete(any(Invitation.class));

        try {
            invitationService.deleteInvitation(invitation);
            Assert.fail();
        } catch (HibernateException he) {
            Assert.assertEquals("test", he.getMessage());
        }
    }
}
