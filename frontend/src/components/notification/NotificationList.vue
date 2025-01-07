<template>
  <div class="notification-list">
    <div v-for="(notification, index) in notifications" :key="index">
      <NotificationCard :notification="notification" />
    </div>
  </div>
</template>
  
<script>
  import NotificationCard from './NotificationCard.vue'; // Import the notification card component
  
  export default {
    data() {
      return {
        notifications: [] // Store incoming notifications
      };
    },
    mounted() {
      const userId = 2; // Replace with the actual user ID
      const eventSource = new EventSource(`http://localhost:8080/sse/notifications/${userId}`);
  
      eventSource.onmessage = (event) => {
        const notification = JSON.parse(event.data);
        this.notifications.push(notification); // Add the new notification to the list
      };
  
      eventSource.onerror = (event) => {
        console.error('Error with SSE connection:', event);
      };
    },
    components: {
      NotificationCard // Register the NotificationCard component
    }
  };
</script>
  
<style scoped>
  .notification-list {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .notification-list > div {
    margin-bottom: 20px;
  }
</style>