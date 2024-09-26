<template>
  <v-container fluid fill-height class="container-background-color">
    <v-row class="d-flex justify-center">
      <!-- 左邊個人資訊 -->
      <v-col lg="2">
        <UserInfo
          :avatarUrl="user.avatarUrl"
          :name="user.name"
          @save="saveUserName($event)"
        />
      </v-col>
      <!-- 左邊個人資訊 end -->
      <!-- 右邊表格 -->
      <v-col lg="6">
        <v-row>
          <v-col lg="3" class="d-flex align-center text-h3">Project</v-col>
          <v-col lg="7" class="d-flex flex-row">
            <TableSearch
              vTextLabel="Find a Project..."
              @ChangeInput="ChangeInput($event)"
            />
          </v-col>
          <v-col lg="2" class="d-flex justify-end align-end">
            <NewItem
              vCardTitle="Add Project"
              vTextLabel="Project Name"
              @add="addproject($event)"
            />
          </v-col>
        </v-row>
        <v-divider></v-divider>
        <v-row>
          <v-col lg="12">
            <DataTable
              :searchedName="searchedName"
              :tableData="projects"
              :user="user"
              @deleteProject="removeProject"
            />
          </v-col>
        </v-row>
      </v-col>
      <!-- 右邊表格 end -->
      <v-snackbar
        v-model="snackBar"
        :timeout="snackBarTimeout"
        :color="snackBarColor"
      >
        {{ msg }}
      </v-snackbar>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import { addProject, getProjects, deleteProject } from "@/apis/projects.ts";
import UserInfo from "@/components/UserInfo.vue";
import DataTable from "@/components/DataTable.vue";
import NewItem from "@/components/NewItem.vue";
import TableSearch from "@/components/TableSearch.vue";
import { getUserInfo, editUserName } from "@/apis/user";
import bus from "@/store/modules/bus";

export default Vue.extend({
  components: {
    UserInfo,
    DataTable,
    NewItem,
    TableSearch
  },
  data() {
    return {
      dialog: false,
      searchedName: "",
      projects: [] as any,
      msg: "" as any,
      snackBar: false,
      snackBarTimeout: 3000,
      snackBarColor: "",
      user: { type: Object, id: "", name: "", avatarUrl: "" }
    };
  },
  async created() {
    this.updateProject();
    this.user = (await getUserInfo())["data"];
    bus.on("updateProject", this.updateProject);
  },
  methods: {
    async updateProject() {
      this.projects = (await getProjects())["data"];
    },

    async saveUserName(newUserName: string) {
      const result = await editUserName(newUserName);

      this.msg = result["data"].message;
      this.snackBar = true;
      this.snackBarColor = result["data"].success ? "green" : "red";

      this.user.name = "";
      this.user.name = (await getUserInfo())["data"].name;
    },
    async addproject(inputData: any) {
      const result = await addProject(inputData);

      this.msg = result["data"].message;
      this.dialog = false;
      this.snackBar = true;
      this.snackBarColor = result["data"].success ? "green" : "red";

      this.projects = (await getProjects(this.user.id))["data"];
    },
    ChangeInput(searchedName: any) {
      this.searchedName = searchedName;
    },

    async removeProject(projectId: any, userId: any) {
      const result = await deleteProject(projectId, userId);

      this.msg = result["data"].message;
      this.dialog = false;
      this.snackBar = true;
      this.snackBarColor = result["data"].success ? "green" : "red";

      this.projects = (await getProjects(this.user.id))["data"];
    }
  }
});
</script>

<style lang="scss">
tbody {
  tr:hover {
    background-color: rgba(0, 0, 0, 0) !important;
  }
}

.v-data-table {
  padding: 0 !important;
}

tbody {
  background-color: rgba(237, 237, 237, 0) !important;
  padding: 0 !important;
}

.v-text-field {
  margin: 0px !important;
}

.container-background-color {
  background-color: rgb(247, 247, 247) !important;
}
</style>
