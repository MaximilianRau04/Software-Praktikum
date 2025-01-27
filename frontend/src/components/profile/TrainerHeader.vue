<template>
    <div class="trainer-header">
      <div class="trainer-info">
        <!-- Trainer Name and Username -->
        <h1 class="trainer-name">{{ user.firstname }} {{ user.lastname }}</h1>
        <p class="trainer-username">@{{ user.username }}</p>
      </div>
  
      <!-- Bio/Description -->
      <div class="trainer-bio">
        <p>{{ bio }}</p>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "TrainerHeader",
    props: {
      user: {
        type: Object,
        required: true,
      },
      bio: {
        type: String,
        required: true,
      },
    },
  };
  </script>
  
  <style scoped>
  .trainer-header {
    text-align: center;
    padding: 1.5rem;
    border-bottom: 1px solid #ccc;
    margin-bottom: 1.5rem;
  }
  
  .trainer-info {
    margin-bottom: 1rem;
  }
  
  .trainer-name {
    font-size: 2rem;
    font-weight: bold;
    margin-bottom: 0.2rem;
  }
  
  .trainer-username {
    font-size: 1.2rem;
    color: #777;
    font-style: italic;
  }
  
  .trainer-bio {
    font-size: 1rem;
    color: #555;
    max-width: 400px;
    margin: 0 auto;
  }
  </style>