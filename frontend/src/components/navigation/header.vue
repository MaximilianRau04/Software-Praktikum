<template>
  <div class="header-container">
    <div class="header">
      <div class="sidebar-toggle" @click="handleToggleSidebar">
        <img src="@/images/sidebar.png" alt="Sidebar Toggle" class="sidebar-icon" />
      </div>

      <div class="search-container">
        <input type="text" v-model="value" class="search-input" placeholder="Search..." />
      </div>

      <div class="user-info">
        <div class="user-label">{{ currentUser.username || 'Guest' }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { globalState } from '@/types/User';

export default {
  props: {
    dataOpenSideBar: Boolean,
    toggleSidebar: Function,
  },
  data() {
    return {
      value: '',
    };
  },
  computed: {
    currentUser() {
      return globalState.user;
    },
  },
  methods: {
    handleToggleSidebar() {
      this.toggleSidebar();
    },
  },
};
</script>

<style scoped>
.header-container {
  width: 100%;
  background-color: #f3f4f6;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 50px;
}

.sidebar-toggle {
  padding: 8px;
  cursor: pointer;
}

.sidebar-icon {
  width: 30px;
  height: 30px;
  transition: transform 0.3s ease;
}

.sidebar-toggle:hover .sidebar-icon {
  transform: scale(1.1);
}

.search-container {
  padding: 8px 16px;
}

.search-input {
  height: 40px;
  width: 400px;
  padding: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 30px;
  margin-right: 2%;
  border: 2px solid #01172F;
  border-radius: 8px;
  background-color: #ffffff;
}


.user-label {
  font-size: 20px;
}
</style>
