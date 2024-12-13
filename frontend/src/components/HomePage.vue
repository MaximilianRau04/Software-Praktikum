<template>
  <div class="dashboard-container">
     <!-- Sidebar for navigation -->
    <sidebar :dataOpenSideBar="openSidebar" @changeComponent="changeComponent" />
     <!-- Main content area -->
    <div class="main-content">
      <headerTop :dataOpenSideBar="openSidebar" :toggleSidebar="toggleSidebar" />
      <!-- Dynamic content area that renders the current component -->
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import MainPage from '@/components/ViewAllExchangeDays/MainPage.vue';
import EventPlanning from '@/components/createNewEvents/EventPlanning.vue';
import GiveFeedback from '@/components/feedback/GiveFeedback.vue';
import Sidebar from '@/components/navigation/sidebar.vue';
import HeaderTop from '@/components/navigation/header.vue';
import EventRegistrations from './ViewAllExchangeDays/EventRegistrations.vue';

export default {
  components: {
    Sidebar, // Sidebar component for navigation
    HeaderTop, // Header component with toggle button
    MainPage,  // Default component for main view
    EventPlanning, // Component for creating new events
    GiveFeedback, // Component for giving Feedback
    EventRegistrations, // Component for viewing registered events
  },
  data() {
    return {
      openSidebar: true,
      currentComponent: 'MainPage',
    };
  },
  methods: {
  /**
   * Toggles the visibility of the sidebar.
   */
    toggleSidebar() {
      this.openSidebar = !this.openSidebar;
    },
    /**
     * Updates the currently displayed component.
     * @param {string} componentName - The name of the component to display.
     */
    changeComponent(componentName) {
      this.currentComponent = componentName;
    },
  },
};
</script>



<style scoped>
/* Container for the entire dashboard layout */
.dashboard-container {
  display: flex;
  height: 100vh;
}

/* Main content area grows to fill space next to the sidebar */
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
