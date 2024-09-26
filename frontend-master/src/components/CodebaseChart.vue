<template>
  <div>
    <ve-line :data="chartData" :settings="chartSettings"></ve-line>
    <ve-line
      v-if="isCompare"
      :data="compareChartData"
      :settings="chartSettings"
    ></ve-line>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { getCodebase } from "@/apis/repoInfo.ts";
export default Vue.extend({
  props: {
    repoId: Number,
    compareRepoId: Number
  },
  data() {
    return {
      chartSettings: {
        area: true
      },
      chartData: {
        columns: [
          "date",
          "numberOfRows",
          "numberOfRowsAdded",
          "numberOfRowsDeleted"
        ],
        rows: []
      },
      compareChartData: {
        columns: [
          "date",
          "numberOfRows",
          "numberOfRowsAdded",
          "numberOfRowsDeleted"
        ],
        rows: []
      }
    };
  },
  watch: {
    compareRepoId: function(newValue) {
      this.getCodebaseData(this.compareRepoId).then(res => {
        this.compareChartData.rows = res;
      });
    }
  },
  mounted() {
    this.getCodebaseData(this.repoId).then(res => {
      this.chartData.rows = res;
    });

    if (this.isCompare) {
      this.getCodebaseData(this.compareRepoId).then(res => {
        this.compareChartData.rows = res;
      });
    }
  },
  computed: {
    isCompare(): boolean {
      return this.compareRepoId != null;
    }
  },
  methods: {
    getCodebaseData(repoId: number): Promise<any> {
      return getCodebase(repoId).then(res => {
        return res.data;
      });
    }
  }
});
</script>
