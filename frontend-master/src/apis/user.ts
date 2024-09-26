import axios from "axios";
import store from "@/store";
import { host } from "../config/config";

export const getUser = () => {
  return axios.get(`${host}/user`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const getUserInfo = (): Promise<any> => {
  return axios.get(`${host}/user`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const editUserName = (newUserName: string) => {
  return axios
    .post(
      `${host}/user/edit`,
      {
        Name: newUserName
      },
      {
        headers: {
          "Content-Type": "application/json; charset=UTF-8",
          Authorization: `Bearer ${store.auth.getToken}`
        }
      }
    )
    .then(response => {
      return response;
    });
};

export const isCurrentUserProjectOwner: any = (projectId: string) => {
  return axios.get(`${host}/invitation/checkowner/${projectId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const getAllUser: any = () => {
  return axios.get(`${host}/user/admin`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const deleteUserByAccount: any = (account: string) => {
  return axios.delete(`${host}/user/?account=${account}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const editUserInfo = (account: string, newInfo: any) => {
  return axios.patch(`${host}/user/${account}`, newInfo, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};
