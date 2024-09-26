<template>
  <div>
    <v-tabs>
      <v-tab>Open</v-tab>
      <v-tab>Close</v-tab>
      <!-- open -->
      <v-tab-item>
        <v-data-table
          :loading="loading"
          loading-text="Loading... Please wait"
          dense
          :headers="headers"
          :items="items.openIssues"
          class="elevation-1"
        >
          <template v-slot:[`item.title`]="{ item }">
            <a :href="item.html_url" target="aboutblank">{{ item.title }}</a>
          </template>
        </v-data-table>
      </v-tab-item>
      <!-- closed -->
      <v-tab-item>
        <v-card-text
          >Average Issue Processing Time：{{
            items.averageDealWithIssueTime
          }}</v-card-text
        >
        <v-data-table
          :loading="loading"
          loading-text="Loading... Please wait"
          dense
          :headers="headers"
          :items="items.closeIssues"
          class="elevation-1"
        >
          <template v-slot:[`item.title`]="{ item }">
            <a :href="item.html_url" target="aboutblank">{{ item.title }}</a>
          </template>
          <template v-slot:[`item.user.login`]="{ item }">
            <a :href="item.user.html_url" target="aboutblank">{{
              item.user.login
            }}</a>
          </template>
        </v-data-table>
      </v-tab-item>
    </v-tabs>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { getIssueInfo } from "@/apis/repoInfo";

export default Vue.extend({
  props: {
    repoId: Number
  },
  data() {
    return {
      loading: true,
      headers: [
        { text: "No.", align: "start", value: "number", width: "4%" },
        { text: "Title", value: "title", width: "65%" },
        { text: "IssuesProposer", value: "user.login" },
        { text: "CreatedTime", value: "created_at" },
        { text: "ClosedTime", value: "closed_at" }
      ],
      items: [] as any
    };
  },
  async created() {
    this.items = (await getIssueInfo(this.repoId))["data"];
    this.loading = false;
  },
  methods: {}
});
</script>
