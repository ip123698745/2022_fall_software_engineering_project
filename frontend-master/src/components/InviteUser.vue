<template>
  <v-dialog v-model="dialog" max-width="60%">
    <template v-slot:activator="{ on, attrs }">
      <v-btn color="success" v-bind="attrs" v-on="on" @click="clearInputData"
        ><v-icon>mdi-account-multiple-plus-outline</v-icon></v-btn
      >
    </template>
    <v-card>
      <v-card-title>
        <span class="headline">{{ vCardTitle }}</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-autocomplete
                v-model="inputData"
                :items="userInfo"
                color="white"
                item-text="name"
                item-value="id"
                label="User Name"
              >
                <template v-slot:selection="data">
                  <v-avatar left>
                    <v-img :src="data.item.avatarUrl"></v-img>
                  </v-avatar>
                  <v-sheet class="ml-4">
                    {{ data.item.name }}
                  </v-sheet>
                </template>
                <template v-slot:item="data">
                  <template v-if="typeof data.item !== 'object'">
                    <v-list-item-content
                      v-text="data.item"
                    ></v-list-item-content>
                  </template>
                  <template v-else>
                    <v-list-item-avatar>
                      <img :src="data.item.avatarUrl" />
                    </v-list-item-avatar>
                    <v-list-item-content>
                      <v-list-item-title
                        v-html="data.item.name"
                      ></v-list-item-title>
                    </v-list-item-content>
                  </template>
                </template>
              </v-autocomplete>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="dialog = false">
          CANCEL
        </v-btn>
        <v-btn color="blue darken-1" text @click="add"> INVITE </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["vCardTitle", "vTextLabel", "userInfo"],
  data() {
    return {
      inputData: "",
      dialog: false
    };
  },
  methods: {
    add() {
      this.$emit("invite", this.inputData);
      this.dialog = false;
    },
    clearInputData() {
      this.$emit("clickInvitation", this.inputData);
      this.inputData = "";
    }
  }
});
</script>
