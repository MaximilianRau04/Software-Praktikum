<template>
  <div
    class="sidebar"
    :class="{
      'sidebar-visible': dataOpenSideBar,
      'sidebar-close': !dataOpenSideBar,
    }"
  >
    <div class="content">
      <div class="menu">
        <div class="menu-item" @click="navigateTo('/home')">
          <div class="menu-icon home-icon"></div>
          <span class="menu-text">Home</span>
        </div>
        <div
          class="menu-item"
          @click="navigateTo('/events/planning')"
          v-if="getCookie('role') === 'ADMIN'"
        >
          <div class="menu-icon planning-icon"></div>
          <span class="menu-text">Admin Panel</span>
        </div>
        <div
          class="menu-item"
          @click="navigateTo('/events/registrations')"
          v-if="isUserLoggedIn"
        >
          <div class="menu-icon events-icon"></div>
          <span class="menu-text">Meine Events</span>
        </div>
        <div
          class="menu-item"
          @click="navigateTo('/leaderboard')"
          v-if="isUserLoggedIn"
        >
          <div class="menu-icon leaderboard-icon"></div>
          <span class="menu-text">Rangliste</span>
        </div>

        <div class="separator-side"></div>

        <div
          class="menu-item"
          @click="isUserLoggedIn ? logout() : navigateTo('/login')"
        >
          <div v-if="isUserLoggedIn" class="menu-icon logout-icon"></div>
          <div v-else class="menu-icon login-icon"></div>
          <span class="menu-text" v-if="isUserLoggedIn">Log Out</span>
          <span class="menu-text" v-else>Log In</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import "@/assets/sidebar.css";

export default {
  props: {
    dataOpenSideBar: Boolean,
    toggleSidebar: Function,
  },
  computed: {
    isUserLoggedIn() {
      return !!Cookies.get("userId");
    },
  },
  methods: {
    navigateTo(route) {
      this.$emit("toggleSidebar");
      this.$router.push(route);
    },
    logout() {
      ["userId", "username", "firstname", "lastname", "role"].forEach(
        (cookie) => {
          Cookies.remove(cookie);
        },
      );
      this.$router.push("/login");
    },
    getCookie(name) {
      return Cookies.get(name);
    },
  },
};
</script>
