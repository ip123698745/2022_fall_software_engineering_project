<template>
  <v-container fill-height fluid class="paper align-center justify-center">
    <div>
      <h1 class="title">Welcome to PMS</h1>
      <v-btn class="login-btn pa-2" @click="handleLogin">
        <v-icon>mdi-login-variant</v-icon>
        Login via Github
      </v-btn>
    </div>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import { githubOAuthUrl } from "../config/config";
import router from "@/router";
import store from "@/store";
import App from "@/App.vue";

export default Vue.extend({
  methods: {
    handleLogin: (e: any) => {
      window.location.assign(githubOAuthUrl);
    }
  }
});
</script>

<style lang="scss">
.paper {
  background-color: rgb(92, 92, 92) !important;
}
.login-btn {
  background-color: rgb(46, 46, 46);
  margin-top: 2em;
}
.title {
  color: rgb(255, 255, 255);
}
</style>
