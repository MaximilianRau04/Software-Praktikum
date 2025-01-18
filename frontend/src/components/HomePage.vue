<template>
  <div class="dashboard-container">
    <!-- Sidebar for navigation -->
    <sidebar
      :dataOpenSideBar="openSidebar"
      :toggleSidebar="toggleSidebar"
      @changeComponent="changeComponent"
    />
    <!-- Main content area -->
    <div class="main-content">
      <HeaderTop
        :dataOpenSideBar="openSidebar"
        :toggleSidebar="toggleSidebar"
        :notifications="notifications"
        @mark-as-read="handleMarkAsRead"
      />
      <!-- Dynamic content area that renders the current component -->
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import MainPage from "@/components/viewExchangeDays/home/MainPage.vue";
import EventPlanning from "@/components/createNewEvents/EventPlanning.vue";
import GiveFeedback from "@/components/feedback/GiveFeedback.vue";
import Sidebar from "@/components/navigation/Sidebar.vue";
import HeaderTop from "@/components/navigation/header.vue";
import EventRegistrations from "@/components/viewExchangeDays/EventRegistrations.vue";
import Leaderboard from "@/components/leaderboard/Leaderboard.vue";
import Forum from "./forum/Forum.vue";
import Cookies from "js-cookie";
import config from "../config";

export default {
  components: {
    Sidebar, // Sidebar component for navigation
    HeaderTop, // Header component with toggle button
    MainPage, // Default component for main view
    EventPlanning, // Component for creating new events
    GiveFeedback, // Component for giving Feedback
    EventRegistrations, // Component for viewing registered events
    Forum, // Component for viewing forum
    Leaderboard, // Component for displaying the points of users in a ranking
  },
  setup() {
    const openSidebar = ref(false);

    const toggleSidebar = () => {
      openSidebar.value = !openSidebar.value;
    };

    return { openSidebar, toggleSidebar };
  },
  data() {
    return {
      currentComponent: "MainPage",
      notifications: [],
    };
  },
  methods: {
    /**
     * Updates the currently displayed component.
     * @param {string} componentName - The name of the component to display.
     */
    changeComponent(componentName) {
      this.currentComponent = componentName;
      this.toggleSidebar();
    },
    fetchNotifications() {
      const userId = Cookies.get("userId");
      if (!userId) return;

      const eventSource = new EventSource(
        `${config.sseBaseUrl}/notifications/${userId}`,
      );

      eventSource.addEventListener("notification", (event) => {
        const notification = JSON.parse(event.data);
        this.notifications.push(notification);
      });

      eventSource.onerror = (event) => {
        console.error("Error with SSE connection", event);
      };
    },
    handleMarkAsRead(notificationId) {
      console.log("pressing mark as read");
      this.notifications = this.notifications.filter(
        (notification) => notification.id !== notificationId,
      );
    },
  },
  mounted() {
    this.fetchNotifications();
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
  max-height: 100vh;
}

.content-area {
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.header-top {
  height: 60px;
  background: #f5f5f5;
  border-bottom: 1px solid #ddd;
  height: calc(100vh - 50px);
  overflow: auto;
}
</style>
