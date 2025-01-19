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

      <div class="search-container">
        <input
          type="text"
          v-model="value"
          class="search-input"
          placeholder="Suche..."
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
            <h3>Event Reminders</h3>
            <NotificationCard
              v-for="(
                notification, index
              ) in groupedNotifications.EVENT_REMINDER"
              :key="index"
              :notification="notification"
              @mark-as-read="markAsRead"
            />
          </div>
          <!-- Add sections for other notification types as needed -->
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
import { ref, onMounted } from "vue";
import { globalState } from "@/types/User";
import Cookies from "js-cookie";
import "@/assets/header.css";
import NotificationCard from "@/components/notification/NotificationCard.vue";
import config from "@/config";

export default {
  components: {
    NotificationCard,
  },
  props: {
    dataOpenSideBar: Boolean,
    toggleSidebar: Function,
    notifications: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      value: "",
      showNotifications: false,
    };
  },
  computed: {
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
    currentUser() {
      return globalState.user;
    },
    unreadCount() {
      return this.notifications.filter((notification) => !notification.isRead)
        .length;
    },
  },
  methods: {
    handleToggleSidebar() {
      this.toggleSidebar();
    },
    handleLogin() {
      this.$router.push("/login");
    },
    logout() {
      Cookies.remove("userId");
      Cookies.remove("username");
      Cookies.remove("firstname");
      Cookies.remove("lastname");
      Cookies.remove("role");
      globalState.setUser(null);
      this.$router.push("/login");
    },
    toggleToRegistration() {
      this.$router.push("/register");
    },
    navigateToProfile() {
      if (this.currentUser.id) {
        this.$router.push(`/user/${this.currentUser.username}`);
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
  },

  /**
   * Check if user is already logged in
   */
  onMounted() {
    const userId = Cookies.get("userId");
    if (userId) {
      const userData = {
        id: userId,
        username: Cookies.get("username"),
        firstname: Cookies.get("firstname"),
        lastname: Cookies.get("lastname"),
        role: Cookies.get("role"),
      };
      globalState.setUser(userData);
    }
  },
};
</script>

<style scoped>
.notification-container {
  position: relative;
  margin-right: 20px;
  cursor: pointer;
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
