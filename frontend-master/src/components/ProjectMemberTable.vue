<template>
  <v-dialog v-model="dialog" max-width="30%">
    <v-dialog v-model="dialogDelete" max-width="30%">
      <v-card>
        <v-card-title class="headline"
          >Are you sure you want to delete?</v-card-title
        >
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="closeDialogDelete"
            >Cancel</v-btn
          >
          <v-btn color="blue darken-1" text @click="deleteProjectMember()"
            >Delete</v-btn
          >
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <template v-slot:activator="{ on, attrs }">
      <v-btn color="success" v-bind="attrs" v-on="on"
        ><v-icon>mdi-account-multiple</v-icon></v-btn
      >
    </template>
    <v-card>
      <v-card-title>
        <span class="headline">Project Member</span>
      </v-card-title>
      <v-data-table :headers="headers" :items="tableData" class="elevation-1">
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>Owner: {{ projectOwnerName }}</v-toolbar-title>
          </v-toolbar>
        </template>
        <template
          v-if="isProjectOwner(currentUserId)"
          v-slot:[`item.actions`]="{ item }"
        >
          <v-icon
            small
            :disabled="!isProjectOwner(currentUserId)"
            @click="showRemoveProjectMemberDialog(item.id)"
          >
            mdi-delete
          </v-icon>
        </template>
      </v-data-table>
    </v-card>
    <v-snackbar
      v-model="snackBar"
      :timeout="snackBarTimeout"
      :color="snackBarColor"
    >
      {{ msg }}
    </v-snackbar>
  </v-dialog>
</template>

<script lang="ts">
import Vue from "vue";
import { deleteProjectMember, getProjectMember } from "@/apis/projects";
export default Vue.extend({
  props: [
    "projectOwnerId",
    "projectOwnerName",
    "projectId",
    "currentUserId",
    "tableData"
  ],
  data() {
    return {
      dialog: false,
      dialogDelete: false,
      deletedUserId: "",
      snackBar: false,
      snackBarTimeout: 3000,
      snackBarColor: "",
      msg: "",
      headers: [
        {
          text: "Name",
          value: "name"
        },
        {
          text: "Actions",
          value: "actions"
        }
      ]
    };
  },
  methods: {
    async deleteProjectMember() {
      const reponse = (
        await deleteProjectMember(this.projectId, this.deletedUserId)
      )["data"];
      this.closeDialogDelete();
      this.msg = reponse.message;
      this.snackBar = true;
      this.snackBarColor = reponse.success ? "green" : "red";

      this.$emit("deleteProjectMember");
    },

    showRemoveProjectMemberDialog(userId: string) {
      this.dialogDelete = true;
      this.deletedUserId = userId;
    },

    closeDialogDelete() {
      this.dialogDelete = false;
      this.deletedUserId = "";
    },

    isProjectOwner(userAccount: string) {
      return userAccount == this.projectOwnerId;
    }
  }
});
</script>
