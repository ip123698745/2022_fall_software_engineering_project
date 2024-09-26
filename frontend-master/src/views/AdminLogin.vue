<template>
  <v-container fill-height fluid class="paper align-center justify-center">
    <v-form
      ref="form"
      @submit.prevent="handleLogin"
      v-model="valid"
      lazy-validation
      class="form"
    >
      <v-text-field
        v-model="account"
        label="Account"
        :rules="accountRules"
        dark
      ></v-text-field>
      <v-text-field
        v-model="password"
        label="Password"
        :rules="passwordRules"
        type="password"
        dark
      ></v-text-field>
      <v-btn class="login-btn" type="submit" :disabled="isRoading">Login</v-btn>
    </v-form>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import router from "@/router";
import store from "@/store";
import App from "@/App.vue";

export default Vue.extend({
  data() {
    return {
      account: "",
      password: "",
      valid: false,
      isRoading: false,
      accountRules: [(v: any) => !!v || "Account is required"],
      passwordRules: [(v: any) => !!v || "Password is required"]
    };
  },
  methods: {
    handleLogin(e: any) {
      if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
        this.isRoading = true;
        store.auth
          .loginByAdmin({
            account: this.account,
            password: this.password
          })
          .then(res => {
            this.isRoading = false;
          });
      }
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
.textField {
  color: white;
}
.form {
  width: 50%;
  max-width: 500px;
}
</style>
