import axios from "axios";
import { host } from "../config/config";
import store from "@/store";

export const getRepository = (projectId: string) => {
  return axios.get(`${host}/repo/${projectId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const addRepo: any = (
  projectId: number,
  url: string,
  isSonarqube: boolean,
  sonarqubeUrl: string,
  accountColonPassword: string,
  projectKey: string
) => {
  return axios.post(
    `${host}/repo`,
    {
      projectId: projectId,
      url: url,
      isSonarqube: isSonarqube,
      sonarqubeUrl: sonarqubeUrl,
      accountColonPassword: accountColonPassword,
      projectKey: projectKey
    },
    {
      headers: {
        Authorization: `Bearer ${store.auth.getToken}`
      }
    }
  );
};

export const deleteRepo: any = (projectId: number, repoId: number) => {
  return axios.delete(`${host}/repo/${projectId}/${repoId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};
