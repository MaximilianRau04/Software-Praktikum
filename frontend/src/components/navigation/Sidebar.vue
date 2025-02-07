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

        <HasRole role="ROLE_ADMIN">
          <div class="menu-item" @click="navigateTo('/events/planning')">
            <div class="menu-icon planning-icon"></div>
            <span class="menu-text">Admin Panel</span>
          </div>
        </HasRole>

        <HasRole role="ROLE_USER">
          <div class="menu-item" @click="navigateTo('/events/registrations')">
            <div class="menu-icon events-icon"></div>
            <span class="menu-text">Meine Events</span>
          </div>
        </HasRole>

        <HasRole role="ROLE_USER">
          <div class="menu-item" @click="navigateTo('/leaderboard')">
            <div class="menu-icon leaderboard-icon"></div>
            <span class="menu-text">Rangliste</span>
          </div>
        </HasRole>

        <div class="separator-side"></div>

        <div
          class="menu-item"
          @click="isAuthenticated ? logout() : navigateTo('/login')"
        >
          <div v-if="isAuthenticated" class="menu-icon logout-icon"></div>
          <div v-else class="menu-icon login-icon"></div>
          <span class="menu-text" v-if="isAuthenticated">Log Out</span>
          <span class="menu-text" v-else>Log In</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import "@/assets/sidebar.css";
import HasRole from "../HasRole.vue";
import { useAuth } from "@/util/auth";

export default {
  components: {
    HasRole,
  },

  props: {
    dataOpenSideBar: Boolean,
    toggleSidebar: Function,
  },
  computed: {
    isAuthenticated() {
      const auth = useAuth();
      return auth.isAuthenticated;
    },
  },
  methods: {
    navigateTo(route) {
      this.$emit("toggleSidebar");
      this.$router.push(route);
    },

    logout() {
      const auth = useAuth();
      auth.clearToken();
      this.$router.push("/login");
    },
  },
};
</script>
