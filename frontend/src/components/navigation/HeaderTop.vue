<template>
  <div class="header-container">
    <div class="header">
      <div class="sidebar-toggle" @click="handleToggleSidebar">
        <img
          src="@/images/sidebar.png"
          alt="Sidebar Toggle"
          class="sidebar-icon"
        />
      </div>

      <div class="notification-container" @click="toggleNotificationMenu">
        <div class="notification-icon">
          <img src="@/images/bell.png" alt="Notifications" class="bell-icon" />
          <div v-if="unreadCount > 0" class="notification-badge">
            {{ unreadCount }}
          </div>
        </div>

        <div v-if="showNotifications" class="notification-menu" @click.stop>
          <!-- EVENT_REMINDERS Section -->
          <div v-if="groupedNotifications.EVENT_REMINDER?.length">
            <h3>Event Erinnerungen</h3>
            <component
              v-for="(
                notification, index
              ) in groupedNotifications.EVENT_REMINDER"
              :key="index"
              is="EventNotificationCard"
              :notification="notification"
              @mark-as-read="markAsRead"
              @closeNoti="handleNavigate"
            />
          </div>

          <!-- FORUM_POST Section -->
          <div v-if="groupedNotifications.FORUM_POST?.length">
            <h3>Forum Antworten</h3>
            <component
              v-for="(notification, index) in groupedNotifications.FORUM_POST"
              :key="index"
              is="ForumNotificationCard"
              :notification="notification"
              @mark-as-read="markAsRead"
              @closeNoti="handleNavigate"
            />
          </div>

          <!-- REWARD Section -->
          <div v-if="groupedNotifications.REWARD?.length">
            <h3>Errungenschaften</h3>
            <component
              v-for="(notification, index) in groupedNotifications.REWARD"
              :key="index"
              is="RewardNotificationCard"
              :notification="notification"
              @mark-as-read="markAsRead"
              @closeNoti="handleNavigate"
            />
          </div>

          <div v-if="!hasNotifications" class="no-notifications">
            Keine Benachrichtigungen
          </div>
        </div>
      </div>

      <div class="user-info">
        <div class="user-label" @click="navigateToProfile">
          {{ currentUser.username || "Gast" }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { globalState } from "@/types/User";
import Cookies from "js-cookie";
import "@/assets/header.css";

import { useAuth } from "@/util/auth";

import NotificationCard from "@/components/notification/NotificationCardBase.vue";
import EventNotificationCard from "../notification/EventNotificationCard.vue";
import ForumNotificationCard from "../notification/ForumNotificationCard.vue";
import RewardNotificationCard from "../notification/RewardNotificationCard.vue";

export default {
  components: {
    NotificationCard,
    EventNotificationCard,
    ForumNotificationCard,
    RewardNotificationCard,
  },
  props: {
    dataOpenSideBar: Boolean,
    toggleSidebar: Function,
    notifications: {
      type: Array,
      default: () => [],
      required: false,
    },
  },
  data() {
    return {
      value: "",
      showNotifications: false,
    };
  },
  computed: {
    currentUser() {
      return useAuth().getUserData() || this.getFallbackUser();
    },

    /**
     * Groups notifications by type.
     */
    groupedNotifications() {
      return this.notifications.reduce((groups, notification) => {
        const type = notification.type || "UNKNOWN";
        if (!groups[type]) groups[type] = [];
        groups[type].push(notification);
        return groups;
      }, {});
    },

    hasNotifications() {
      return Object.keys(this.groupedNotifications).some(
        (type) => this.groupedNotifications[type]?.length > 0,
      );
    },

    unreadCount() {
      return Array.isArray(this.notifications)
        ? this.notifications.filter((notification) => !notification.isRead)
            .length
        : 0;
    },
  },
  methods: {
    getFallbackUser() {
      const auth = useAuth();
      return auth.getUserData() || { id: null, username: 'Guest' };
    },

    handleToggleSidebar() {
      this.toggleSidebar();
    },
    handleLogin() {
      this.$router.push("/login");
    },
    logout() {
      const auth = useAuth();
      auth.clearToken();
      globalState.setUser(null);
      this.$router.push("/login");
    },

    toggleToRegistration() {
      this.$router.push("/register");
    },

    navigateToProfile() {
      if (this.currentUser.id) {
        this.$router.push(`/profile/${this.currentUser.username}`);
      } else {
        this.$router.push("/login");
      }
    },

    toggleNotificationMenu() {
      this.showNotifications = !this.showNotifications;
    },
    markAsRead(notificationId) {
      this.$emit("mark-as-read", notificationId);
    },
    handleNavigate(notification) {
      if (!notification) return;

      if (notification.type === "FORUM_POST") {
        this.$router.push({
          name: "EventPage",
          params: { eventId: notification.context.eventId },
          query: { threadId: notification.context.threadId },
        });
      } else if (notification.type === "EVENT_REMINDER") {
        this.$router.push({ name: "EventDetails", params: { eventId: notification.context.eventId } });
      } else if (notification.type === "REWARD") {
        this.$router.push(`/profile/${this.currentUser.username}`);
      }

      this.showNotifications = false; // Close menu
    },
  },

  /**
   * Check if user is already logged in
   */
  onMounted() {
    const auth = useAuth();
    if (auth.isAuthenticated.value) {
      if (userData) globalState.setUser(userData);
    }
  },
};
</script>

<style scoped>
h3 {
  margin-left: 5%;
}

.notification-container {
  position: relative;
  cursor: pointer;
  margin-left: auto;
  margin-right: 5%;
  z-index: 500;
}

.notification-icon {
  position: relative;
}

.bell-icon {
  width: 25px;
  height: 25px;
}

.notification-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: red;
  color: white;
  font-size: 12px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.notification-menu {
  position: absolute;
  top: 40px;
  right: 0;
  width: 300px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 10;
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.notification-item:last-child {
  border-bottom: none;
}

.no-notifications {
  text-align: center;
  padding: 20px;
  font-size: 14px;
  color: #666;
}
</style>
