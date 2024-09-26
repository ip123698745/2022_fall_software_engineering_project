<template>
  <v-text-field
    id="table-search-card"
    class="align-self-end"
    @input="ChangeInput"
    v-model="searchedName"
    prepend-inner-icon="mdi-magnify"
    :label="vTextLabel"
    hide-details
    clearable
    outlined
    dense
    solo
  ></v-text-field>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({
  props: ["vTextLabel"],
  data() {
    return {
      searchedName: ""
    };
  },
  methods: {
    ChangeInput() {
      this.$emit("ChangeInput", this.searchedName);
    }
  }
});
</script>
