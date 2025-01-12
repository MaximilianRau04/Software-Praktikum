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
          <span>Home</span>
        </div>
        <div
          class="menu-item"
          @click="navigateTo('/events/planning')"
          v-if="getCookie('role') === 'ADMIN'"
        >
          <span>Event Planung</span>
        </div>
        <div
          class="menu-item"
          @click="navigateTo('/events/registrations')"
          v-if="isUserLoggedIn"
        >
          <span>Meine Events</span>
        </div>
        <div
          class="menu-item"
          @click="isUserLoggedIn ? logout() : navigateTo('/login')"
        >
          <span v-if="isUserLoggedIn">Log Out</span>
          <span v-else>Log In</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import "../../assets/sidebar.css";

export default {
  props: {
    dataOpenSideBar: Boolean,
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
        }
      );
      this.$router.push("/login");
    },
    getCookie(name) {
      return Cookies.get(name);
    },
  },
};
</script>
