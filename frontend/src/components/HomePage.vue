<template>
  <div class="dashboard-container">
    <sidebar :dataOpenSideBar="openSidebar" @changeComponent="changeComponent" />
    <div class="main-content">
      <headerTop :dataOpenSideBar="openSidebar" :toggleSidebar="toggleSidebar" />
      <div class="content-area">
        <component :is="currentComponent" />
      </div>
    </div>
  </div>
</template>

<script>
import MainPage from '@/components/ViewAllExchangeDays/MainPage.vue';
import Create from '@/components/CreateNewEvents/Create.vue';
import Sidebar from '@/components/Navigation/sidebar.vue';
import HeaderTop from '@/components/Navigation/header.vue';

export default {
  components: {
    Sidebar,
    HeaderTop,
    MainPage,
    Create,
  },
  data() {
    return {
      openSidebar: true,
      currentComponent: 'MainPage',
    };
  },
  methods: {
    toggleSidebar() {
      this.openSidebar = !this.openSidebar;
    },
    changeComponent(componentName) {
      this.currentComponent = componentName;
    },
  },
};
</script>



<style scoped>
.dashboard-container {
  display: flex;
  height: 100vh;
}

.main-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  max-height: 100vh;
}

.content-area {
  height: calc(100vh - 50px);
}
</style>
