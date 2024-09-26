<template>
  <v-dialog v-model="dialog" max-width="60%">
    <template v-slot:activator="{ on, attrs }">
      <v-btn color="success" v-bind="attrs" v-on="on" @click="clearInputData"
        ><v-icon>mdi-plus-box-multiple-outline</v-icon></v-btn
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
              <v-text-field
                :label="vTextLabel"
                v-model="url"
                required
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row v-if="isShow()">
            <v-col>
              <v-checkbox
                flat
                label="Sonarqube"
                v-model="isSonarqube"
                color="primary"
              ></v-checkbox>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <v-text-field
                label="Sonarqube URL"
                v-model="sonarqubeUrl"
                v-show="isSonarqube"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="6">
              <v-text-field
                label="Account"
                v-model="account"
                v-show="isSonarqube"
              ></v-text-field>
            </v-col>
            <v-col cols="6">
              <v-text-field
                label="Password"
                v-model="pw"
                v-show="isSonarqube"
                type="password"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <v-text-field
                label="ProjectKey"
                v-model="projectKey"
                v-show="isSonarqube"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="dialog = false">
          Cancel
        </v-btn>
        <v-btn color="blue darken-1" text @click="add"> Add </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["vCardTitle", "vTextLabel"],
  data() {
    return {
      isSonarqube: false,
      url: "",
      sonarqubeUrl: "",
      account: "",
      pw: "",
      projectKey: "",
      dialog: false,
      userAccounts: [] as any
    };
  },
  watch: {
    isSonarqube: function() {
      this.sonarqubeUrl = "";
      this.account = "";
      this.pw = "";
      this.projectKey = "";
    }
  },
  methods: {
    add() {
      const accountColonPassword = btoa(this.account + ":" + this.pw);
      this.$emit(
        "add",
        this.url,
        this.isSonarqube,
        this.sonarqubeUrl,
        accountColonPassword,
        this.projectKey
      );
      this.dialog = false;
    },
    clearInputData() {
      this.url = "";
      this.sonarqubeUrl = "";
      this.account = "";
      this.pw = "";
      this.projectKey = "";
      this.isSonarqube = false;
    },
    isShow() {
      return !this.vCardTitle.includes("Project");
    }
  }
});
</script>
