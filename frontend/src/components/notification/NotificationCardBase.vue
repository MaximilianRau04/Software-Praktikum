<template>
  <div class="notification-card">
    <div class="notification-details">
      <div class="notification-message">
        <slot name="message"></slot>
      </div>
      <div class="notification-meta">
        <span>gesendet {{ formattedCreatedAt }}</span>
      </div>
    </div>
    <button class="mark-as-read-btn" @click="markAsRead">
      <img src="@/images/close.png" alt="Mark as Read" />
    </button>
  </div>
</template>


<script>
import { formatDistanceToNow, parseISO } from "date-fns";
import { de } from "date-fns/locale";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import config from "@/config";

export default {
  props: {
    notification: {
      type: Object,
      required: true,
      default: () => ({}),
    },
  },
  setup(props, { emit }) {
    const router = useRouter();

    const formattedCreatedAt = computed(() => {
      return formatDistanceToNow(parseISO(props.notification.createdAt), {
        addSuffix: true,
        locale: de,
      });
    });

    const markAsRead = async () => {
      try {
        const response = await fetch(
          `${config.apiBaseUrl}/notifications/${props.notification.id}/read`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
            },
          },
        );

        if (!response.ok) {
          throw new Error("Failed to mark notification as read");
        }
        emit("mark-as-read", props.notification.id);
      } catch (error) {
        console.error("Error marking notification as read:", error);
      }
    };

    return {
      formattedCreatedAt,
      markAsRead,
    };
  },
};
</script>

<style scoped>
.notification-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  margin: 12px 0;
  background-color: #ffffff;
  border-radius: 16px;
  border: 1px solid #e1e1e1;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
}

.notification-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.notification-details {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.notification-message {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.notification-meta {
  font-size: 0.875rem;
  color: #9e9e9e;
  margin-top: 8px;
}

.mark-as-read-btn {
  border: none;
  background: none;
  cursor: pointer;
  padding: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.mark-as-read-btn:hover {
  background-color: #f3f3f3;
  transform: scale(1.15);
}

.mark-as-read-btn img {
  width: 16px;
  height: 16px;
}
</style>
