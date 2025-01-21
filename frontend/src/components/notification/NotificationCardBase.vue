<template>
  <div class="notification-card">
    <div class="notification-details">
      <slot name="message"></slot>
      <div class="notification-meta">
        <span>Time sent: {{ formattedCreatedAt }}</span>
      </div>
      <button class="mark-as-read-btn" @click="markAsRead">
        <img src="@/images/close.png" alt="Mark as Read" />
      </button>
    </div>
  </div>
</template>

<script>
import { formatDistanceToNow, parseISO } from "date-fns";
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
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  border: 1px solid #ddd;
  margin: 10px 0;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.notification-content {
  flex-grow: 1;
}

.notification-card p {
  margin: 5px 0;
}

.notification-title {
  color: #007bff;
  cursor: pointer;
  text-decoration: underline;
}
.notification-title:hover {
  color: #0056b3;
}

.created-at {
  font-size: 0.9em;
  color: #777;
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
  background-color: #f0f0f0;
}

.mark-as-read-btn img {
  width: 16px;
  height: 16px;
}
</style>
