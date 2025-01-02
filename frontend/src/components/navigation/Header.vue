<template>
  <div class="header-container">
    <div class="header">
      <div class="sidebar-toggle" @click="handleToggleSidebar">
        <img src="@/images/sidebar.png" alt="Sidebar Toggle" class="sidebar-icon" />
      </div>

      <div class="search-container">
        <input type="text" v-model="value" class="search-input" placeholder="Suche..." />
      </div>

      <div class="user-info">
        <div class="user-label" @click="navigateToProfile">{{ currentUser.username || 'Gast' }}</div>

      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { globalState } from '@/types/User';
import Cookies from 'js-cookie';
import '../../assets/header.css';

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
    handleLogin() {
      this.$router.push('/login');
    },
    logout() {
      Cookies.remove('userId');
      Cookies.remove('username');
      Cookies.remove('firstname');
      Cookies.remove('lastname');
      Cookies.remove('role');
      globalState.setUser(null);
      this.$router.push('/login');
    },
    toggleToRegistration() {
      this.$router.push('/register');
    },
    navigateToProfile() {
      if (this.currentUser.id) {
        this.$router.push(`/user/${this.currentUser.username}`);
      } else {
        this.$router.push('/login');
      }
    },
  },
  
  /**
   * Check if user is already logged in
   */
  onMounted() {
    const userId = Cookies.get('userId');
    if (userId) {
      const userData = {
        id: userId,
        username: Cookies.get('username'),
        firstname: Cookies.get('firstname'),
        lastname: Cookies.get('lastname'),
        role: Cookies.get('role'),
      };
      globalState.setUser(userData);
    }
  },
};
</script>

