import axios from "axios";
import { host } from "../config/config";
import store from "@/store";

export const getNotification = () => {
  return axios.get(`${host}/invitation`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const invite = (projectId: number) => {
  return axios.post(
    `${host}/invitation/users`,
    { ApplicantId: 0, ProjectId: projectId },
    {
      headers: {
        Authorization: `Bearer ${store.auth.getToken}`,
        "Content-Type": "application/json; charset=UTF-8"
      }
    }
  );
};

export const sendInvitation = (ApplicantId: number, projectId: number) => {
  return axios.post(
    `${host}/invitation/sendinvitation`,
    { ApplicantId: ApplicantId, ProjectId: projectId },
    {
      headers: {
        Authorization: `Bearer ${store.auth.getToken}`,
        "Content-Type": "application/json; charset=UTF-8"
      }
    }
  );
};

export const ReplyToInvitation = (invitation: number, isAgree: boolean) => {
  return axios.post(
    `${host}/invitation/reply`,
    { InvitationId: invitation, IsAgreed: isAgree },
    {
      headers: {
        Authorization: `Bearer ${store.auth.getToken}`,
        "Content-Type": "application/json; charset=UTF-8"
      }
    }
  );
};
