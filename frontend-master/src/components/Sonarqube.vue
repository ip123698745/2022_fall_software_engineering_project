<template>
  <div>
    <v-row class="mt-5 justify-center">
      <v-col cols="12" lg="10">
        <v-card elevation="2" outlined>
          <v-container fluid>
            <v-row>
              <v-col class="text-left"
                ><span>{{ projectName }}</span></v-col
              >
            </v-row>
            <v-row>
              <v-col>
                <span class="mr-2">Bugs</span>
                <v-icon>mdi-bug</v-icon>
                <v-row class="justify-center mt-2"
                  ><span class="mr-2">{{
                    getSingleMeasureDataValue("bugs")
                  }}</span></v-row
                >
              </v-col>
              <v-divider vertical></v-divider>
              <v-col>
                <span class="mr-2">Vulnerabilities</span>
                <v-icon>mdi-security</v-icon>
                <v-row class="justify-center mt-2"
                  ><span class="mr-2">{{
                    getSingleMeasureDataValue("vulnerabilities")
                  }}</span></v-row
                >
              </v-col>
              <v-divider vertical></v-divider>
              <v-col>
                <span>Code Smells</span>
                <v-row class="justify-center mt-2"
                  ><span class="mr-2">{{
                    getSingleMeasureDataValue("code_smells")
                  }}</span></v-row
                >
              </v-col>
              <v-divider vertical></v-divider>
              <v-col>
                <span>Coverage</span>
                <v-row class="justify-center mt-2"
                  ><span class="mr-2"
                    >{{ getSingleMeasureDataValue("coverage") }}%</span
                  ></v-row
                >
              </v-col>
              <v-divider vertical></v-divider>
              <v-col>
                <span class="mr-2">Duplications</span>
                <v-icon>mdi-content-duplicate</v-icon>
                <v-row class="justify-center mt-2"
                  ><span class="mr-2">{{
                    getSingleMeasureDataValue("duplicated_lines_density")
                  }}</span></v-row
                >
              </v-col>
            </v-row>
          </v-container>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-card class="mx-auto text-left">
        <v-container>
          <v-row dense>
            <v-col v-for="(item, key) in codeSmells" :key="key" cols="12">
              <v-card>
                <div>
                  <div>
                    <v-card-title>{{ key }}</v-card-title>

                    <v-card
                      class="mt-1"
                      color="#F2DEDE"
                      v-for="(codeSmell, i) in item"
                      :key="i"
                      :href=getUrl(codeSmell.key)
                    >
                      <v-card-text style="color: black; font-weight: bold"
                        ><v-chip
                          class="ma-2"
                          :color="colorMapper[codeSmell.severity]"
                          text-color="white"
                        >
                          {{ codeSmell.severity }} </v-chip
                        >{{ codeSmell.message }}</v-card-text
                      >
                    </v-card>
                  </div>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </v-card>
    </v-row>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { getSonarqubeInfo, getSonarqubeCodeSmell } from "@/apis/repoInfo";

interface Measure {
  metric: string;
  component: string;
  value: string;
  bestValue: boolean;
}

interface CodeSmell {
  total: number;
  issues: Issue[];
}

interface Issue {
  key: string;
  severity: string;
  component: string;
  line: number;
  message: string;
}

export default Vue.extend({
  props: {
    repoId: Number,
  },
  data() {
    return {
      measures: [] as Array<Measure>,
      projectName: String,
      codeSmells: {} as CodeSmell,
      colorMapper: {
        BLOCKER: "red",
        CRITICAL: "red",
        MAJOR: "red",
        MINOR: "green",
        INFO: "secondary",
      },
    };
  },
  created() {
    this.getSonarqubeInfo();
  },
  methods: {
    async getSonarqubeInfo() {
      const data = (await getSonarqubeInfo(this.repoId)).data;
      this.measures = data["measures"];
      this.projectName = data["projectName"];
      this.codeSmells = (await getSonarqubeCodeSmell(this.repoId)).data;
    },
    getSingleMeasureDataValue(measureName: string): string | undefined {
      const result = this.measures.find(
        (measure) => measure.metric === measureName
      );
      return result?.value;
    },
    getUrl(key: string) {
      return `http://zxjte9411.ml:9000/project/issues?id=PMS_109&open=${key}`;
    }
  },
});
</script>

<style></style>
