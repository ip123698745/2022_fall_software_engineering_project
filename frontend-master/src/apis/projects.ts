import axios from "axios";
import { host } from "@/config/config";
import store from "@/store";

export const getProjects: any = () => {
  return axios
    .get(`${host}/project/`, {
      headers: {
        Authorization: `Bearer ${store.auth.getToken}`
      }
    })
    .then(function(response) {
      return response;
    })
    .catch(err => {
      return false;
    });
};

export const getProject = (projectId: number | null) => {
  return axios
    .get(`${host}/project/${projectId}`, {
      headers: {
        "Content-Type": "application/json; charset=UTF-8",
        Authorization: `Bearer ${store.auth.getToken}`
      }
    })
    .then(response => {
      return response;
    });
};

export const getProjectMember = (projectId: number) => {
  return axios.get(`${host}/project/member/${projectId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const deleteProject = (projectId: number, userId: string) => {
  return axios.delete(`${host}/project/${projectId}/${userId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const deleteProjectMember = (projectId: number, userId: string) => {
  return axios.delete(`${host}/project/member/${projectId}/${userId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const addProject = (projectName: string | null) => {
  return axios
    .post(
      `${host}/project/add`,
      {
        ProjectName: projectName
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

export const editProject = (
  projectId: number,
  projectName: string | (string | null)[]
) => {
  return axios
    .post(
      `${host}/project/edit`,
      {
        ProjectId: projectId,
        ProjectName: projectName
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

export const editProjectNameByAdmin = (projectId: number, newInfo: any) => {
  return axios.patch(`${host}/project/${projectId}`, newInfo, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const deleteProjectByAdmin = (projectId: number) => {
  return axios.delete(`${host}/project/${projectId}`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};

export const getProjectByAdmin: any = () => {
  return axios.get(`${host}/project/admin/`, {
    headers: {
      Authorization: `Bearer ${store.auth.getToken}`
    }
  });
};
