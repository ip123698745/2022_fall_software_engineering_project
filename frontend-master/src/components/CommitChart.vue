<template>
  <div>
    <ve-histogram
      :data="weekTotalData"
      :events="chartEvents"
      :settings="chartSetting"
    ></ve-histogram>
    <p v-if="!isCompare">selected-week：{{ selectedWeek }}</p>
    <ve-line v-if="!isCompare" :data="commitData"></ve-line>
    <ve-histogram
      v-if="isCompare"
      :data="compareWeekTotalData"
      :settings="chartSetting"
    ></ve-histogram>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { getCommitInfo } from "../apis//repoInfo";
import { checkAuth } from "@/apis/authorize";
import router from "@/router";

const initialData = () => {
  return {
    weekTotalData: {},
    compareWeekTotalData: {},
    dayOfWeekData: [],
    commitData: {},
    selectedWeek: "",
    chartSetting: {
      selectedMode: "single"
    }
  };
};

export default Vue.extend({
  props: {
    repoId: Number,
    compareRepoId: Number
  },
  watch: {
    compareRepoId: function(newValue) {
      this.getCompareCommitInfoData();
    }
  },
  data: function() {
    return initialData();
  },
  mounted() {
    this.getCommitInfoData(this.repoId).then(res => {
      this.weekTotalData = res.weekTotalData;
      this.dayOfWeekData = res.dayOfWeekData;
      this.selectedWeek =
        res.weekTotalData.rows[res.weekTotalData.rows.length - 1].week;
      this.setCommitData();
    });
    this.getCompareCommitInfoData();
  },
  computed: {
    chartEvents() {
      return {
        click: (e: any) => {
          this.clickChartEvent(e);
        }
      };
    },
    isCompare(): boolean {
      return this.compareRepoId != null;
    }
  },
  methods: {
    getCommitInfoData(repoId: number): Promise<any> {
      return getCommitInfo(repoId)
        .then(res => {
          const data = res.data;
          return {
            weekTotalData: {
              columns: ["week", "total"],
              rows: data.weekTotalData
            },
            dayOfWeekData: data.dayOfWeekData
          };
        })
        .catch(err => {
          router.push("/notfound");
        });
    },
    getCompareCommitInfoData() {
      if (this.isCompare) {
        this.getCommitInfoData(this.compareRepoId).then(res => {
          this.compareWeekTotalData = res.weekTotalData;
        });
      }
    },
    setCommitData() {
      this.commitData = {
        columns: ["day", "commit"],
        rows: this.getCommitRowsData()
      };
    },
    clickChartEvent(e: any) {
      this.selectedWeek = e.name;
      this.setCommitData();
    },
    getCommitRowsData() {
      let rows: { day: string; commit: number }[] = [];
      this.dayOfWeekData.forEach(
        (data: {
          week: string;
          detailDatas: { day: string; commit: number }[];
        }) => {
          if (data.week === this.selectedWeek) {
            rows = data.detailDatas;
          }
        }
      );
      return rows;
    }
  }
});
</script>
