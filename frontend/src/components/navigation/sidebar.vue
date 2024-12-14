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
        <div class="menu-item" @click="navigateTo('/feedback')" v-if="currentUser.role == 'ADMIN'">
          <span>Mein Feedback</span>
        </div>
        <div class="menu-item log-out" @click="navigateTo('/')">
          <span>Log Out</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { globalState } from '@/types/User';
export default {
  props: {
    dataOpenSideBar: Boolean,
  },
  methods: {
    navigateTo(route) {
      this.$router.push(route);
    },
    
  },
  computed: {
    currentUser() {
      return globalState.user;
    },
  },
};
</script>

<style scoped>
.sidebar {
  height: 100vh;
  background-color: #01172F;
  overflow: hidden;
  transition: 300ms;
}

.sidebar-visible {
  width: 250px !important;
}

.sidebar-close {
  width: 50px !important;
}

.content {
  padding-top: 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: calc(100vh - 3rem);
  background-color: #01172F;
}

.menu {
  text-align: left;
  padding: 0 0.5rem;
  white-space: nowrap;
}

.menu-item {
  margin-left: 0.5rem;
  padding: 0.7rem 0;
  border-radius: 0.375rem;
  cursor: pointer;
  color: #009EE2;
  font-weight: bold;
  font-size: larger;
  transition: color 0.2s ease-in-out;
}

.menu-item:hover {
  color: #fff;
}

.log-out{
  position: absolute;
  bottom: 1%;
  left: 10px;

}

</style>
