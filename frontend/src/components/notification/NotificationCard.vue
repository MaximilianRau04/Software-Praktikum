<template>
    <div class="notification-card">
      <h3>{{ notification.title }}</h3>
      <p>{{ notification.description }}</p>
      <p><strong>Status:</strong> {{ notification.isRead ? 'Read' : 'Unread' }}</p>
      <p><strong>Created At:</strong> {{ notification.createdAt }}</p>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      notification: {
        type: Object,
        required: true,
      },
    },
  };
  </script>
  
  <style scoped>
  .notification-card {
    padding: 15px;
    border: 1px solid #ddd;
    margin: 10px 0;
    background-color: #f9f9f9;
  }
  
  .notification-card h3 {
    margin-top: 0;
  }
  
  .notification-card p {
    margin: 5px 0;
  }
  </style>
  