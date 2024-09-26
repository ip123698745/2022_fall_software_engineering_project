<template>
  <v-data-iterator
    :items="tableData"
    :search="searchedName"
    :user="user"
    fixed-header
    hide-default-header
    style="background-color: rgba(237, 237, 237, 0)"
  >
    <template v-slot:default="props">
      <v-dialog v-model="dialogDelete" max-width="30%">
        <v-card>
          <v-card-title class="headline"
            >Are you sure you want to delete?</v-card-title
          >
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="closeDelete"
              >Cancel</v-btn
            >
            <v-btn color="blue darken-1" text @click="removeProject">OK</v-btn>
            <v-spacer></v-spacer>
          </v-card-actions>
        </v-card>
      </v-dialog>
      <v-row>
        <v-col
          v-for="item in props.items"
          :key="item.name"
          cols="12"
          sm="12"
          md="6"
          lg="4"
        >
          <v-card>
            <v-row>
              <v-col style="text-align: right" class="mr-3">
                <v-icon
                  @click="showDeleteDialog(item.id, user.id)"
                  :disabled="!isDeleteProjectEnable(user.id, item.ownerId)"
                  >mdi-close-thick</v-icon
                ></v-col
              >
            </v-row>
            <v-row>
              <v-col>
                <div @click="goToRepositoryDetail(item.id, item.name)">
                  <a class="subheading font-weight-bold text-h4">
                    {{ item.name }}
                  </a>
                  <div style="text-align: center" class="mt-8 ml-2">
                    Owner: {{ item.ownerName }}
                  </div>
                </div></v-col
              >
            </v-row>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-data-iterator>
</template>

<script lang="ts">
import Vue from "vue";
import { getProjects, deleteProject } from "@/apis/projects.ts";

export default Vue.extend({
  props: ["searchedName", "tableData", "user"],
  data() {
    return {
      headers: [
        {
          text: "ProjectName",
          value: "name"
        },
        {
          text: "Actions",
          value: "actions"
        }
      ],
      dialogDelete: false,
      deleteUserId: "",
      deleteProjectId: ""
    };
  },
  methods: {
    goToRepositoryDetail(id: string, projectName: string) {
      this.$router.push({ name: "Repository", params: { projectId: id } });
    },

    isDeleteProjectEnable(userId: string, owner: string) {
      return owner === userId;
    },

    closeDelete() {
      this.deleteProjectId = "";
      this.deleteUserId = "";
      this.dialogDelete = false;
    },
    showDeleteDialog(projectId: string, userId: string) {
      this.dialogDelete = true;
      this.deleteUserId = userId;
      this.deleteProjectId = projectId;
    },
    removeProject() {
      this.$emit("deleteProject", this.deleteProjectId, this.deleteUserId);
      this.dialogDelete = false;
    }
  }
});
</script>
