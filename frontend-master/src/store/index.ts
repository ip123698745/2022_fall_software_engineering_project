import Vuex from "vuex";
import Vue from "vue";
import AuthModule from "./modules/auth";
import { createVuexStore, Module } from "vuex-simple";
import createPersistedState from "vuex-persistedstate";

// Load Vuex
Vue.use(Vuex);
// Create store
class RootModule {
  @Module()
  public auth = new AuthModule();
}

const instance = new RootModule();
createVuexStore(instance, {
  plugins: [createPersistedState()]
});
export default instance;
