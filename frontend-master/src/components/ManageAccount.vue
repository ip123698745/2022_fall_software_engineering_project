<template>
  <v-data-table
    :headers="headers"
    :items="users"
    sort-by="account"
    class="elevation-3 align-self-center"
    :search="search"
    :custom-filter="filter"
  >
    <template v-slot:top>
      <v-toolbar flat>
        <v-toolbar-title>Account Manage</v-toolbar-title>
        <v-divider class="mx-4" inset vertical></v-divider>
        <v-text-field
          v-model="search"
          append-icon="mdi-magnify"
          label="Search"
          single-line
          hide-details
        ></v-text-field>
        <v-spacer></v-spacer>
        <!-- edit dialog  -->
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
                  <v-col cols="12" sm="6" md="6" lg="6">
                    <v-select
                      v-model="editedItem.authority"
                      :items="authorities"
                      label="Authority"
                      return-object
                      single-line
                    ></v-select>
                  </v-col>
                  <v-col cols="12" sm="12" md="12" lg="12">
                    <v-text-field
                      v-model="editedItem.avatarUrl"
                      label="AvatarUrl"
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
        <v-dialog v-model="dialogDelete" max-width="550px">
          <v-card>
            <v-card-title class="headline" v-model="deleteUserName"
              >Are you sure you want to delete
              {{ deleteUserName }} ?</v-card-title
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
      </v-toolbar>
      <v-divider></v-divider>
    </template>
    <!-- actions -->
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon small class="mr-2" @click="editUser(item)"> mdi-pencil </v-icon>
      <v-icon small @click="deleteUser(item)"> mdi-delete </v-icon>
    </template>
    <!-- actions end -->
  </v-data-table>
</template>

<script lang="ts">
import Vue from "vue";
import { getAllUser, deleteUserByAccount, editUserInfo } from "@/apis/user";

export default Vue.extend({
  props: ["users"],
  data() {
    return {
      search: "",
      dialog: false,
      dialogDelete: false,
      authorities: ["Admin", "User"],
      headers: [
        {
          text: "Account (Id)",
          align: "start",
          value: "account"
        },
        { text: "Name", value: "name" },
        { text: "AvatarUrl", value: "avatarUrl" },
        { text: "Authority", value: "authority" },
        { text: "Actions", value: "actions", sortable: false }
      ],
      editedIndex: -1,
      deleteUserName: "",
      editedItem: {
        name: "",
        avatarUrl: "",
        authority: ""
      },
      defaultItem: {
        name: "",
        avatarUrl: "",
        authority: ""
      }
    };
  },

  computed: {
    formTitle(): string {
      return this.editedIndex === -1 ? "New User" : "Edit User";
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
    filter(value: any, search: string, item: any) {
      return item.name.includes(search) || item.account.includes(search);
    },
    // TODO
    editUser(item: any) {
      this.editedIndex = this.users.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialog = true;
    },

    deleteUser(item: any) {
      this.editedIndex = this.users.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.deleteUserName = this.users[this.editedIndex].name;
      this.dialogDelete = true;
    },

    async deleteItemConfirm() {
      const response = (
        await deleteUserByAccount(this.users[this.editedIndex].account)
      )["data"];
      if (response.success) this.users.splice(this.editedIndex, 1);
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
      this.deleteUserName = "";
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },
    findDifferent(user: any, editedItem: any): any {
      const newInfo = [] as any;
      if (user.name !== editedItem.name) {
        newInfo.push({
          value: editedItem.name,
          path: "/name",
          op: "replace"
        });
      }
      if (user.avatarUrl !== editedItem.avatarUrl) {
        newInfo.push({
          value: editedItem.avatarUrl,
          path: "/avatarUrl",
          op: "replace"
        });
      }
      if (user.authority !== editedItem.authority) {
        newInfo.push({
          value: editedItem.authority,
          path: "/authority",
          op: "replace"
        });
      }
      return newInfo;
    },
    save() {
      if (this.editedIndex > -1) {
        const user = this.users[this.editedIndex];
        const newInfo = this.findDifferent(user, this.editedItem);
        Object.assign(this.users[this.editedIndex], this.editedItem);
        editUserInfo(user.account, newInfo)
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
        this.users.push(this.editedItem);
      }
      this.close();
    }
  }
});
</script>

<style></style>
