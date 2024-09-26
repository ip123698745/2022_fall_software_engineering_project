<template>
  <v-data-table
    :headers="headers"
    :items="projects"
    sort-by="id"
    class="elevation-3 align-self-center"
    :search="search"
  >
    <!--  -->
    <template v-slot:top>
      <v-toolbar flat>
        <v-toolbar-title>Project Manage</v-toolbar-title>
        <v-divider class="mx-4" inset vertical></v-divider>
        <v-text-field
          v-model="search"
          append-icon="mdi-magnify"
          label="Search"
          single-line
          hide-details
        ></v-text-field>
        <v-spacer></v-spacer>
        <!-- edit dialog -->
        <v-dialog v-model="dialog" max-width="500px">
          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle }}</span>
            </v-card-title>
            <v-card-text>
              <v-container>
                <v-row>
                  <v-col cols="12" sm="6" md="6" lg="6">
                    <v-text-field
                      v-model="editedItem.name"
                      label="Name"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="close"> Cancel </v-btn>
              <v-btn color="blue darken-1" text @click="save"> Save </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <!-- edit dialog end -->
        <!-- check delete dialog -->
        <v-dialog v-model="dialogDelete" max-width="550px">
          <v-card>
            <v-card-title class="headline"
              >Are you sure you want to delete this Project?</v-card-title
            >
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="closeDelete"
                >Cancel</v-btn
              >
              <v-btn color="blue darken-1" text @click="deleteItemConfirm"
                >OK</v-btn
              >
              <v-spacer></v-spacer>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <!-- check delete dialog end -->
      </v-toolbar>
      <v-divider></v-divider>
    </template>
    <!--  -->
    <!--  -->
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon small class="mr-2" @click="editItem(item)"> mdi-pencil </v-icon>
      <v-icon small @click="deleteItem(item)"> mdi-delete </v-icon>
    </template>
    <!--  -->
  </v-data-table>
</template>

<script lang="ts">
import Vue from "vue";
import {
  getProjects,
  editProjectNameByAdmin,
  deleteProjectByAdmin
} from "@/apis/projects.ts";

export default Vue.extend({
  props: ["projects"],
  data: () => {
    return {
      search: "",
      dialog: false,
      dialogDelete: false,
      headers: [
        {
          text: "Id",
          align: "start",
          value: "id"
        },
        { text: "Name", value: "name" },
        { text: "Owner", value: "ownerName" },
        { text: "Members", value: "members" },
        { text: "Actions", value: "actions", sortable: false }
      ],
      editedIndex: -1,
      editedItem: {
        name: ""
      },
      defaultItem: {
        name: ""
      }
    };
  },
  computed: {
    formTitle(): string {
      return this.editedIndex === -1 ? "New Project" : "Edit Project";
    }
  },
  watch: {
    dialog(val) {
      val || this.close();
    },
    dialogDelete(val) {
      val || this.closeDelete();
    }
  },
  methods: {
    editItem(item: any) {
      this.editedIndex = this.projects.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialog = true;
    },

    deleteItem(item: any) {
      this.editedIndex = this.projects.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialogDelete = true;
    },

    async deleteItemConfirm() {
      const response = (
        await deleteProjectByAdmin(this.projects[this.editedIndex].id)
      )["data"];
      if (response.success) this.projects.splice(this.editedIndex, 1);
      this.$emit("update");
      this.$emit("showMessage", response);
      this.closeDelete();
    },

    close() {
      this.dialog = false;
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },

    closeDelete() {
      this.dialogDelete = false;
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },

    save() {
      if (this.editedIndex > -1) {
        const newProjectInfo = [
          {
            value: this.editedItem.name,
            path: "/name",
            op: "replace"
          }
        ];
        Object.assign(this.projects[this.editedIndex], this.editedItem);
        const project = this.projects[this.editedIndex];
        editProjectNameByAdmin(project.id, newProjectInfo)
          .then(response => {
            if (response.data.success) {
              this.$emit("showMessage", response.data);
              this.$emit("update");
            }
          })
          .catch(err => {
            this.$emit("showMessage", err);
            this.$emit("update");
          });
      } else {
        this.projects.push(this.editedItem);
      }
      this.close();
    }
  }
});
</script>

<style></style>
