<template>
  <v-menu
    :close-on-content-click="false"
    offset-y
    transition="scale-transition"
  >
    <!-- template start -->
    <template v-slot:activator="{ on, attrs }">
      <v-btn icon v-bind="attrs" v-on="on">
        <v-badge dot :value="hasMessage">
          <v-icon large> mdi-bell-circle </v-icon>
        </v-badge>
      </v-btn>
    </template>
    <!-- template end -->
    <!-- list start -->
    <v-list two-line width="500" max-height="500" class="overflow-y-auto">
      <v-sheet class="" width="500" v-if="!hasMessage">
        <div>No invitation message!</div>
      </v-sheet>
      <template v-for="(item, index) in items">
        <v-list-item :key="item.invitedProject.id">
          <v-list-item-content>
            <v-list-item-title
              class="text-left mb-2"
              v-text="item.inviter.name"
            ></v-list-item-title>
            <v-list-item-subtitle
              class="text-left"
              v-text="subtitleText + item.invitedProject.name"
            ></v-list-item-subtitle>
          </v-list-item-content>

          <v-list-item-action>
            <div class="d-flex">
              <v-btn @click="accept(item)" color="green" icon
                ><v-icon>mdi-check</v-icon></v-btn
              >
              <v-btn @click="reject(item)" color="red" icon
                ><v-icon>mdi-close</v-icon></v-btn
              >
            </div>
          </v-list-item-action>
        </v-list-item>
        <v-divider
          v-if="index < items.length - 1"
          :key="item.invitedProject.name"
        ></v-divider>
      </template>
    </v-list>
    <!-- list end -->
  </v-menu>
</template>

<script lang="ts">
import Vue from "vue";
import { getNotification, ReplyToInvitation } from "@/apis/notification.ts";
import * as signalR from "@aspnet/signalr";
import { host } from "../config/config";
import store from "@/store";
import bus from "@/store/modules/bus";

export default Vue.extend({
  data() {
    return {
      hubConnection: new signalR.HubConnectionBuilder()
        .withUrl(`${host}/hub/notify`)
        .build(),
      hasMessage: false,
      subtitleText: "invite you to join：",
      items: [] as any,
      userAccount: store.auth.getUserAccount
    };
  },

  created() {
    this.connectHub();
    this.lisentNotification();
    this.getNotificationData();
  },

  beforeDestroy() {
    this.unsubscribeNotification();
  },

  methods: {
    getNotificationData() {
      getNotification().then(res => {
        this.items = res.data;
        if (typeof this.items !== "undefined" && this.items.length > 0) {
          this.hasMessage = true;
        } else {
          this.hasMessage = false;
        }
      });
    },

    accept(item: any) {
      ReplyToInvitation(item.id, true).then(() => {
        this.getNotificationData();
        bus.emit("updateProject");
      });
    },

    reject(item: any) {
      ReplyToInvitation(item.id, false).then(() => {
        this.getNotificationData();
      });
    },

    connectHub() {
      this.hubConnection.start().then(() => {
        this.subscribeNotification();
      });
    },

    subscribeNotification() {
      this.hubConnection.send("SubscribeNotification", this.userAccount);
    },

    unsubscribeNotification() {
      this.hubConnection.send("UnsubscribeNotification", this.userAccount);
    },

    lisentNotification() {
      this.hubConnection.on("ReceiveNotification", () => {
        this.getNotificationData();
      });
    }
  }
});
</script>

<style></style>
