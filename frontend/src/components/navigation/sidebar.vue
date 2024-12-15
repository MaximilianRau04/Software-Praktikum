<template>
  <div class="sidebar" :class="{ 'sidebar-visible': dataOpenSideBar, 'sidebar-close': !dataOpenSideBar }">
    <div class="content">
      <div class="menu">
        <div class="menu-item" @click="navigateTo('/home')">
          <span>Home</span>
        </div>
        <div class="menu-item" @click="navigateTo('/events/planning')" v-if="currentUser.role == 'ADMIN'">
          <span>Event Planung</span>
        </div>
        <div class="menu-item" @click="navigateTo('/events/registrations')">
          <span>Meine Events</span>
        </div>
        <div class="menu-item" @click="navigateTo('/')">
          <span>Log Out</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { globalState } from '@/types/User';
import '../../assets/sidebar.css';

export default {
  props: {
    dataOpenSideBar: Boolean,
  },
  computed: {
    currentUser() {
      return globalState.user;
    },
  },
  methods: {
    navigateTo(route) {
      this.$emit('toggleSidebar');
      this.$router.push(route);
    },
  },
};
</script>

