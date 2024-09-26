<template> 
  <v-container> 
    <v-row class="d-flex justify-end"> 
      <!--select filter button--> 
      <v-col class="px-1" cols="3" v-if="selected.chart!='line'"> 
        <span class="font-weight-bold btn_label">Filter:</span> 
        <v-menu offset-y transition="scale-transition"> 
          <template v-slot:activator="{ on, attrs }"> 
            <v-btn color="primary" dark v-bind="attrs" v-on="on" width="150"> 
              {{ selected.filter }} 
            </v-btn> 
          </template> 
          <v-list> 
            <template v-for="item in filterActivities"> 
              <v-list-item :key="item.name" link class="pa-0"> 
                <v-list-item-content class="pa-0"> 
                  <v-btn class="pa-0" color="white" @click="changeFilterType(item)"> 
                    {{ item.name }} 
                  </v-btn> 
                </v-list-item-content> 
              </v-list-item> 
            </template> 
          </v-list> 
        </v-menu> 
      </v-col> 
      <!--select chart button--> 
      <v-col class="px-1" cols="3"> 
        <span class="font-weight-bold btn_label">Chart:</span> 
        <v-menu offset-y transition="scale-transition"> 
          <template v-slot:activator="{ on, attrs }"> 
            <v-btn color="primary" dark v-bind="attrs" v-on="on" width="150"> 
              {{ selected.chart }} 
            </v-btn> 
          </template> 
          <v-list> 
            <template v-for="item in chartActivities"> 
              <v-list-item :key="item.name" link class="pa-0"> 
                <v-list-item-content class="pa-0"> 
                  <v-btn class="pa-0" color="white" @click="changeChartType(item)"> 
                    {{ item.name }} 
                  </v-btn> 
                </v-list-item-content> 
              </v-list-item> 
            </template> 
          </v-list> 
        </v-menu> 
      </v-col> 
      <!--select info button--> 
      <v-col class="px-1" cols="3"> 
        <span class="font-weight-bold btn_label">Info:</span> 
        <v-menu offset-y transition="scale-transition"> 
          <template v-slot:activator="{ on, attrs }"> 
            <v-btn color="primary" dark v-bind="attrs" v-on="on" width="150"> 
              {{ selected.info }} 
            </v-btn> 
          </template> 
          <v-list> 
            <template v-for="item in infoActivities"> 
              <v-list-item :key="item.name" link class="pa-0"> 
                <v-list-item-content class="pa-0"> 
                  <v-btn class="pa-0" color="white" @click="changeInfoType(item)"> 
                    {{ item.name }} 
                  </v-btn> 
                </v-list-item-content> 
              </v-list-item> 
            </template> 
          </v-list> 
        </v-menu> 
      </v-col> 
    </v-row> 
    <!--select contributors--> 
    <v-row class="d-flex justify-end" v-if="selected.filter=='selected' && selected.chart!='line'"> 
      <v-autocomplete 
        v-model="selectedMembers" 
        :items="data" 
        item-text="author.login" 
        item-value="author.login" 
        placeholder="Select a contributor" 
        clearable 
        multiple 
        filled 
        dense 
        return-object 
        @change="updateOverallData" 
        @input="searchInput=null" 
        :search-input.sync="searchInput" 
      > 
        <template v-slot:selection="{ index }"> 
          <div v-if="index==0"> 
            {{ selectedMembers.length }} members selected &ensp; 
          </div> 
        </template> 
        <template v-slot:item="{ item, attrs, on }"> 
          <v-list-item v-on="on" v-bind="attrs" #default="{ active }"> 
            <v-list-item-action> 
              <v-checkbox :input-value="active"></v-checkbox> 
            </v-list-item-action> 
            <v-list-item-avatar> 
              <img :src="item.author.avatar_url"> 
            </v-list-item-avatar> 
            <v-list-item-content> 
              <v-list-item-title style="font-size:15px"> 
                {{ item.author.login }} 
              </v-list-item-title> 
            </v-list-item-content> 
            <v-list-item-icon> 
              <v-chip text-color="white" :color="chipColor"> 
                rank: {{ item[selected.rankingType] }} 
              </v-chip> 
            </v-list-item-icon> 
          </v-list-item> 
        </template> 
      </v-autocomplete> 
    </v-row> 
    <!--select date range--> 
    <v-row class="d-flex justify-end" v-if="selected.chart!='line'"> 
      <v-col cols="12" sm="6" md="6" lg="6"> 
        <v-menu 
          ref="startDateMenu" 
          v-model="startDateMenu" 
          :close-on-content-click="false" 
          :return-value.sync="startDate" 
          transition="scale-transition" 
          offset-y 
          max-width="320px" 
          min-width="auto" 
        > 
          <template v-slot:activator="{ on, attrs }"> 
            <v-text-field 
              v-model="startDate" 
              label="Start date(month)" 
              prepend-icon="mdi-calendar" 
              readonly 
              v-bind="attrs" 
              v-on="on" 
            ></v-text-field> 
          </template> 
          <v-date-picker v-model="startDate" type="month" no-title scrollable> 
            <v-spacer></v-spacer> 
            <v-btn text color="primary" @click="startDateMenu = false"> 
              Cancel 
            </v-btn> 
            <v-btn text color="primary" @click="changeDataTimeRange(); $refs.startDateMenu.save(startDate)"> 
              OK 
            </v-btn> 
          </v-date-picker> 
        </v-menu> 
      </v-col> 
      <v-col cols="12" sm="6" md="6" lg="6"> 
        <v-menu 
          ref="endDateMenu" 
          v-model="endDateMenu" 
          :close-on-content-click="false" 
          :return-value.sync="endDate" 
          transition="scale-transition" 
          offset-y 
          max-width="320px" 
          min-width="auto" 
        > 
          <template v-slot:activator="{ on, attrs }"> 
            <v-text-field 
              v-model="endDate" 
              label="End date(month)" 
              prepend-icon="mdi-calendar" 
              readonly 
              v-bind="attrs" 
              v-on="on" 
            ></v-text-field> 
          </template> 
          <v-date-picker v-model="endDate" type="month" no-title scrollable> 
            <v-spacer></v-spacer> 
            <v-btn text color="primary" @click="endDateMenu = false"> 
              Cancel 
            </v-btn> 
            <v-btn text color="primary" @click="changeDataTimeRange(); $refs.endDateMenu.save(endDate)"> 
              OK 
            </v-btn> 
          </v-date-picker> 
        </v-menu> 
      </v-col> 
    </v-row> 
    <!--divider--> 
    <v-divider class="mb-10"></v-divider> 
    <!--line chart--> 
    <v-row v-if="selected.chart=='line'"> 
      <template v-for="(item, index) in data"> 
        <v-col lg="4" class="d-flex justify-center" :key="index"> 
          <v-card :key="index" width="450" height="380" class="my-2"> 
            <v-card-title class="mb-1"> 
              <v-avatar size="60"> 
                <img alt="user" :src="item.author.avatar_url"> 
              </v-avatar> 
              <div class="justify-space-around"> 
                <v-card-subtitle class="text-left py-0"> 
                  <a 
                    class="text-subtitle-1 font-weight-regular" 
                    target="aboutblank" 
                    :href="item.author.html_url" 
                  > {{ item.author.login }} 
                  </a> 
                </v-card-subtitle> 
                <div class="d-flex justify-space-around"> 
                  <v-card-subtitle class="text-left py-0"> 
                    <a 
                      class="grey--text" 
                      target="aboutblank" 
                      :href="item.commitsHtmlUrl" 
                    > commits {{ item.total }} 
                    </a> 
                  </v-card-subtitle> 
                  <v-card-subtitle class="text-left py-0 green--text"> 
                    {{ item.totalAdditions }} ++ 
                  </v-card-subtitle> 
                  <v-card-subtitle class="text-left py-0 red--text"> 
                    {{ item.totalDeletions }} -- 
                  </v-card-subtitle> 
                </div> 
              </div> 
            </v-card-title> 
            <v-divider class="mx-4"></v-divider> 
            <ve-line 
              height="300px" 
              :legend-visible="false" 
              :extend="lineChartExtend" 
              :data="personalData[index]" 
              :settings="lineChartSettings" 
              :colors="['#fb8532']" 
            ></ve-line> 
          </v-card> 
        </v-col> 
      </template> 
    </v-row> 
    <!--pie chart--> 
    <ve-pie  
      v-if="selected.chart=='pie'"  
      height="600px" 
      :data="overallData" 
      :settings="pieChartSettings" 
    ></ve-pie> 
    <!--bar chart--> 
    <ve-histogram 
      v-if="selected.chart=='bar'" 
      :data="overallData" 
      :extend="barChartExtend" 
      :settings="barChartSettings"
    ></ve-histogram> 
  </v-container> 
</template> 
 
<script lang="ts"> 
import Vue from "vue"; 
import { getContributeInfo } from "@/apis/repoInfo"; 
 
interface AuthorData { 
  author: Author; 
  commitsHtmlUrl: string; 
  total: number; 
  totalAdditions: number; 
  totalDeletions: number; 
  weeks: Rows[]; 
  subTotal: number; 
  subTotalAdditions: number; 
  subTotalDeletions: number; 
  commitRank: number; 
  additionsRank: number; 
  deletionsRank: number; 
} 
 
interface Author { 
  avatar_url: string; 
  email: string; 
  html_url: string; 
  login: string; 
} 
 
interface Rows { 
  ws: string; 
  w: number; 
  c: number; 
  a: number; 
  d: number; 
} 
 
interface ChartData { 
  rows: any[]; 
  columns: string[]; 
} 
 
export default Vue.extend({ 
  props: { 
    repoId: Number 
  }, 
  data() { 
    return { 
      // response data 
      data: [] as AuthorData[], 
      // chart data 
      personalData: [] as ChartData[], 
      overallData: { columns: ["author", "commit"], rows: [] } as ChartData, 
      // button selected 
      selected: { 
        info: "commit", 
        chart: "line", 
        filter: "all", 
        rankingType: "commitRank" 
      }, 
      // button activities 
      infoActivities: [ 
        { mode: "c", name: "commit",    lineYAxisMax: 0, barYAxisName: "time(s)", color: "#fb8532"}, 
        { mode: "a", name: "additions", lineYAxisMax: 0, barYAxisName: "line(s)", color: "#20cc20"}, 
        { mode: "d", name: "deletions", lineYAxisMax: 0, barYAxisName: "line(s)", color: "#ff5050"} 
      ], 
      chartActivities: [ 
        { name: "line" }, 
        { name: "pie" }, 
        { name: "bar" } 
      ], 
      filterActivities: [ 
        { name: "all" }, 
        { name: "top 5" }, 
        { name: "top 10" }, 
        { name: "selected" } 
      ], 
      // settings 
      lineChartSettings: { 
        labelMap: { 
          ws: "day", 
          c: "commits", 
          a: "additions", 
          d: "deletions" 
        }, 
        dimension: ["ws"], 
        area: true 
      }, 
      pieChartSettings: { 
        radius: 160, 
        offsetY: 300, 
      },
      barChartSettings: { 
        dataOrder : { label: 'commits', order: 'asc'},
        yAxisName : ["time(s)", ""]
      }, 
      // setting extend 
      lineChartExtend: { 
        yAxis: { 
          max: 40 
        } 
      }, 
      barChartExtend: { 
        xAxis: { 
          axisLabel: { 
            margin: 12, 
            rotate: 30, 
          } 
        },
        series: { 
          itemStyle : { 
            color: "#fb8532" 
          } 
        } 
      }, 
      // filter 
      selectedMembers: [] as AuthorData[], 
      chipColor: "#fb8532", 
      // Date 
      startDate: "", 
      endDate: "", 
      startDateMenu: false, 
      endDateMenu: false, 
      // other 
      searchInput: null 
    }; 
  }, 
  methods: { 
    pushToOverallData(item: AuthorData) { 
      this.overallData.rows.push({ 
        "author" : item.author.login, 
        "commit" : item.subTotal, 
        "additions" : item.subTotalAdditions, 
        "deletions" : item.subTotalDeletions 
      }); 
    }, 
    updateOverallDataBySelected() { 
      this.overallData.rows = []; 
      this.selectedMembers.forEach(item => this.pushToOverallData(item)) 
    }, 
    updateOverallDataByRanking(top: number) { 
      this.selectedMembers = this.data.filter(item =>  
        (item as any)[this.selected.rankingType] <= top); 
      this.updateOverallDataBySelected(); 
    }, 
    updateOverallData() { 
      if (this.selected.filter == "all") { 
        this.selectedMembers = [...this.data]; 
        this.updateOverallDataBySelected(); 
      } else if (this.selected.filter == "top 5") { 
        this.updateOverallDataByRanking(5); 
      } else if (this.selected.filter == "top 10") { 
        this.updateOverallDataByRanking(10); 
      } else if (this.selected.filter == "selected") { 
        this.updateOverallDataBySelected(); 
      }
    }, 
    getRankings(arr: number[]) { 
      const sorted = [...arr].sort((a, b) => b - a); 
      return arr.map((x) => sorted.indexOf(x) + 1); 
    }, 
    changeDataRankings() { 
      const commitRanking = this.getRankings(this.data.map(item => item.subTotal)); 
      const additionsRanking = this.getRankings(this.data.map(item => item.subTotalAdditions)); 
      const deletionsRanking = this.getRankings(this.data.map(item => item.subTotalDeletions)); 
      this.data.forEach((item, index) => { 
        item.commitRank = commitRanking[index]; 
        item.additionsRank = additionsRanking[index]; 
        item.deletionsRank = deletionsRanking[index]; 
      }); 
    }, 
    changeDataTimeRange() { 
      let start = new Date(this.startDate); 
      const end = new Date(this.endDate); 
      let startIndex: number; 
      let endIndex: number; 
      if (start > end) { 
        start = end; 
        this.startDate = start.toISOString().substr(0, 7); 
        this.endDate = end.toISOString().substr(0, 7); 
      } 
      // end與start月份相同，期間會有一個月(月初到月底) 
      end.setMonth(end.getMonth() + 1); 
      const isGitLab = this.data[0].weeks[0].w == 0; 
      if (isGitLab) { // GitLab 
        startIndex = this.data[0].weeks.findIndex(element => 
          new Date(element.ws) >= start); 
        endIndex = this.data[0].weeks.findIndex(element => 
          new Date(element.ws) > end); 
      } else { // GitHub 
        startIndex = this.data[0].weeks.findIndex(element => 
          element.w * 1000 >= start.getTime()); 
        endIndex = this.data[0].weeks.findIndex(element => 
          element.w * 1000 > end.getTime()); 
      } 
      if (startIndex == -1) startIndex = 0; 
      if (endIndex == -1) endIndex = this.data[0].weeks.length; 
      this.data.forEach(item => { 
        const subWeeks = item.weeks.slice(startIndex, endIndex); 
        item.subTotal = subWeeks.map(item => item.c).reduce((a, b) => a + b, 0); 
        item.subTotalAdditions = subWeeks.map(item => item.a).reduce((a, b) => a + b, 0); 
        item.subTotalDeletions = subWeeks.map(item => item.d).reduce((a, b) => a + b, 0); 
      }); 
      this.changeDataRankings(); 
      this.updateOverallData(); 
    }, 
    changeInfoType(infoActivities: any) { 
      this.selected.info = infoActivities.name; 
      this.selected.rankingType = infoActivities.name + "Rank"; 
      this.lineChartExtend.yAxis.max = infoActivities.lineYAxisMax; 
      this.barChartSettings.yAxisName[0] = infoActivities.barYAxisName; 
      this.barChartExtend.series.itemStyle.color = infoActivities.color; 
      this.personalData.forEach(item => item.columns = ["ws", infoActivities.mode]); 
      this.overallData.columns = ["author", this.selected.info]; 
      this.barChartSettings.dataOrder.label = this.selected.info;
      this.updateOverallData(); 
      this.chipColor = infoActivities.color; 
    }, 
    changeChartType(chartActivities: any) { 
      this.selected.chart = chartActivities.name; 
    }, 
    changeFilterType(filterActivities: any) { 
      this.selected.filter = filterActivities.name; 
      this.updateOverallData(); 
    }, 
    initDate() { 
      const author = this.data[0].weeks[0]; 
      const isGitLab = author.w == 0; 
      if (isGitLab) { // GitLab 
        this.startDate = new Date(author.ws).toISOString().substr(0, 7);      
      } else { // GitHub 
        this.startDate = new Date(author.w * 1000).toISOString().substr(0, 7); 
      } 
      this.endDate = new Date().toISOString().substr(0, 7); 
    }, 
    initLineYAxisMax() { 
      this.infoActivities[0].lineYAxisMax = Math.max(...this.data.map(item => item.total)) 
      this.infoActivities[1].lineYAxisMax = Math.max(...this.data.map(item => item.totalAdditions)) 
      this.infoActivities[2].lineYAxisMax = Math.max(...this.data.map(item => item.totalDeletions)) 
    }, 
    getContributeInfo() { 
      getContributeInfo(this.repoId) 
        .then(response => { 
          this.data = response.data; 
          this.data.forEach((item, index) => { 
            if (item.author.avatar_url === null) { 
              /* eslint-disable */ 
              item.author.avatar_url = "https://i.stack.imgur.com/frlIf.png"; 
              /* eslint-enable */ 
            } 
            this.personalData.push({ 
              columns: ["ws", "c"], 
              rows: item.weeks 
            }); 
          }); 
          this.selectedMembers = [...this.data]; 
          this.initDate(); 
          this.initLineYAxisMax(); 
          this.changeDataTimeRange(); 
          this.updateOverallData(); 
        }) 
        .catch(err => { 
          alert("系統錯誤"); 
        }); 
    } 
  }, 
  mounted() { 
    this.getContributeInfo(); 
  } 
}); 
</script> 
 
<style lang="scss"> 
a { 
  text-decoration: none; 
} 
.btn_label { 
  margin-right: 5px; 
  font-size: large; 
  width: 60px; 
  display: inline-block; 
} 
</style> 
