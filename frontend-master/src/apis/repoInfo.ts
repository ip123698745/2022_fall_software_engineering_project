import axios from "axios";
import { host } from "@/config/config";

import store from "@/store";

export const getCommitInfo = (repoId: number | null) => {
  return axios.get(`${host}/repoInfo/commit/${repoId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const getCodebase = (repoId: number | null) => {
  return axios.get(`${host}/repoInfo/codebase/${repoId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const getContributeInfo = (repoId: number | null) => {
  return axios.get(`${host}/repoInfo/contribute/${repoId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const getIssueInfo = (repoId: number | null) => {
  return axios.get(`${host}/repoInfo/issue/${repoId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const getSonarqubeInfo = (repoId: number | null) => {
  return axios.get(`${host}/repoInfo/sonarqube/${repoId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const IsHaveSonarqube = (repoId: number | null) => {
  return axios.get(`${host}/repoInfo/ishavesonarqube/${repoId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const getSonarqubeCodeSmell = (repoId: number | null) => {
  return axios.get(`${host}/repoInfo/sonarqube/codesmell/${repoId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};
