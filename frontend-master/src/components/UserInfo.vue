<template>
  <v-card max-width="374" height="700" class="pt-5">
    <v-card-text>
      <v-avatar size="192">
        <img class="mb-2" height="250" contain :src="avatarUrl" />
      </v-avatar>
      <v-col md="12"
        ><v-row class="d-flex justify-center">
          <v-edit-dialog :return-value.sync="userName" @save="save">
            <div class="text-h5">{{ userName }}</div>
            <template v-slot:input>
              <v-text-field
                v-model="userName"
                :rules="[max25chars]"
                label="Edit Name"
              ></v-text-field>
            </template>
          </v-edit-dialog>
        </v-row>
      </v-col>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  props: {
    name: { type: String },
    avatarUrl: { type: String }
  },
  data() {
    return {
      userName: this.name,
      max25chars: function(v: any) {
        return v.length <= 25 || "Input too long!";
      }
    };
  },
  methods: {
    save() {
      this.$emit("save", this.userName);
    }
  },
  watch: {
    name(newVal) {
      this.userName = newVal;
    }
  }
});
</script>
