<template>
  <v-container>
    <v-row align="center">
      <v-switch v-model="isCompare" label="Compare" @change="changeSwitch" />
      <v-select
        class="mx-4"
        :items="projects"
        item-text="name"
        v-show="isCompare"
        v-model="selectedProject"
        item-value="id"
        @change="changeProject"
        label="Compared Project"
      />
      <v-select
        class="mx-4"
        v-show="isCompare"
        v-model="selectedRepo"
        :items="repos"
        item-text="name"
        item-value="id"
        :disabled="selectedProject == null"
        label="Compared Repo"
      />
    </v-row>
    <v-row align="center">
      <v-divider />
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import { getProjects } from "../apis/projects";
import { getRepository } from "../apis/repository";
export default Vue.extend({
  data() {
    return {
      isCompare: false,
      projects: [],
      repos: [],
      selectedProject: null,
      selectedRepo: null
    };
  },
  watch: {
    selectedRepo: function(newValue) {
      this.$emit("change", { isCompare: this.isCompare, repoId: newValue });
    }
  },
  created() {
    this.getprojects();
  },
  methods: {
    async getprojects() {
      this.projects = (await getProjects())["data"];
    },
    changeSwitch() {
      if (!this.isCompare) {
        this.$emit("change", { isCompare: this.isCompare, repoId: null });
      } else {
        this.$emit("change", {
          isCompare: this.isCompare,
          repoId: this.selectedRepo
        });
      }
    },
    changeProject(projectId: any) {
      getRepository(projectId).then(res => {
        this.repos = res.data;
      });
    }
  }
});
</script>
