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
        :notifications="notificationsForHeader"
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
import EventPlanning from "@/components/adminPanel/AdminPanel.vue";
import GiveFeedback from "@/components/feedback/GiveFeedback.vue";
import Sidebar from "@/components/navigation/Sidebar.vue";
import HeaderTop from "@/components/navigation/HeaderTop.vue";
import EventRegistrations from "@/components/viewEvents/EventRegistrations.vue";
import Leaderboard from "@/components/leaderboard/Leaderboard.vue";
import Forum from "./forum/Forum.vue";
import Cookies from "js-cookie";
import config from "../config";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

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
  computed: {
    notificationsForHeader() {
      return this.notifications || [];
    },
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

    async fetchUnreadNotifications() {
      const userId = Cookies.get("userId");
      if (!userId) return;
      try {
        const response = await fetch(
          `${config.apiBaseUrl}/notifications/unread/${userId}`,
        );
        const unreadNotifications = await response.json();

        if (response.ok) {
          this.notifications = unreadNotifications;
        }

        this.fetchNotifications();
      } catch (err) {
        showToast(
          "Error",
          "Benachrichtigungen konnten nicht geladen werden",
          "error",
        );
        this.notifications = [];
      }
    },
    async fetchNotifications() {
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
        showToast("Error", "Fehler mit der SSE Verbindung", "error");
      };
    },
    async handleMarkAsRead(notificationId) {
      try {
        const response = await fetch(
          `${config.apiBaseUrl}/notifications/${notificationId}/read`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
            },
          },
        );

        if (!response.ok) {
          showToast(
            new Toast(
              "Error",
              `Fehler beim Setzen auf gelesen`,
              "error",
              faXmark,
              10,
            ),
          );
        }
      } catch (error) {
        showToast(
          new Toast(
            "Error",
            `Fehler Fetchen der exchange days`,
            "error",
            faXmark,
            10,
          ),
        );
      }

      this.notifications = this.notifications.filter(
        (notification) => notification.id !== notificationId,
      );
    },
  },
  mounted() {
    this.fetchUnreadNotifications();
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
  overflow-y: auto;
  overflow-x: hidden;
}

.header-top {
  height: 60px;
  background: #f5f5f5;
  border-bottom: 1px solid #ddd;
  height: calc(100vh - 50px);
  overflow: auto;
}
</style>
