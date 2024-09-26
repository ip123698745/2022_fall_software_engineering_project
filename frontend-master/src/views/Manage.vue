<template>
  <v-container fluid fill-height class="align-start">
    <v-row class="justify-center">
      <v-col cols="8" sm="6" md="7" lg="8">
        <v-tabs v-model="tab" background-color="teal darken-3" fixed-tabs dark>
          <v-tab v-for="item in items" :key="item">
            {{ item }}
          </v-tab>

          <v-tabs-items v-model="tab" class="tab-item">
            <v-tab-item
              ><ManageAccount
                :users="users"
                @showMessage="showMessage"
                @update="update"
            /></v-tab-item>
            <v-tab-item
              ><ManageProject
                :projects="projects"
                @showMessage="showMessage"
                @update="update"
            /></v-tab-item>
          </v-tabs-items>
        </v-tabs>
      </v-col>
    </v-row>
    <v-snackbar
      v-model="snackBar"
      :timeout="snackBarTimeout"
      :color="snackBarColor"
    >
      {{ message }}
    </v-snackbar>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import { getAllUser } from "@/apis/user";
import { getProjectByAdmin } from "@/apis/projects";
import ManageAccount from "@/components/ManageAccount.vue";
import ManageProject from "@/components/ManageProject.vue";

export default Vue.extend({
  data() {
    return {
      users: [] as any,
      projects: [] as any,
      tab: null,
      items: ["Account Manage", "Project Manage"],
      repoId: Number(this.$route.params.repoId),
      message: "",
      snackBar: false,
      snackBarTimeout: 3000,
      snackBarColor: ""
    };
  },
  methods: {
    async initialize() {
      this.projects = (await getProjectByAdmin())["data"];
      this.users = (await getAllUser())["data"];
    },
    showMessage(response: any) {
      this.snackBarColor = response.success ? "green" : "red";
      this.message = response.message;
      this.snackBar = true;
      if (response.success) {
        this.update();
      }
    },
    update() {
      this.initialize();
    }
  },
  components: {
    ManageAccount,
    ManageProject
  },
  created() {
    this.initialize();
  }
});
</script>

<style></style>
