<template>
  <div class="notification-card">
    <div class="notification-details">
      <p>Your workshop "{{ notification.title }}" will take place {{ timeUntilEvent }}.</p>
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
import { formatDistanceToNow, parseISO } from 'date-fns';
import config from "../../config";

export default {
  props: {
    notification: {
      type: Object,
      required: true,
    },
  },
  computed: {
    formattedCreatedAt() {
      return formatDistanceToNow(parseISO(this.notification.createdAt), { addSuffix: true });
    },
    timeUntilEvent() {
      return formatDistanceToNow(parseISO(this.notification.context.eventDateTime), { addSuffix: true });
    },
  },
  methods: {
    async markAsRead() {
      try {
        const response = await fetch(`${config.apiBaseUrl}/notifications/${this.notification.id}/read`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Failed to mark notification as read');
        }
        this.$emit('mark-as-read', this.notification.id);
      } catch (error) {
        console.error("Error marking notification as read:", error);
      }
    },
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