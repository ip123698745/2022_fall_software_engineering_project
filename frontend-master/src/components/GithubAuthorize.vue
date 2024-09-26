<template>
  <v-container fill-height fluid class="align-center justify-center">
    <v-progress-circular
      :size="50"
      color="primary"
      indeterminate
    ></v-progress-circular>
  </v-container>
</template>

<script lang="ts">
import router from "@/router";
import store from "@/store";
import { useStore } from "vuex-simple";
import AuthModule from "@/store/modules/auth";
import Vue from "vue";
import { mapActions, Store } from "vuex";
import { authenticateGithubToken } from "../apis/authorize";

export default Vue.extend({
  mounted: () => {
    const query = router.currentRoute.query;
    const code = query.code.toString();
    store.auth.login(code);
  }
});
</script>
