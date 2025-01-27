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
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 1rem;
  background: linear-gradient(135deg, #657280, #1bb6f8);
  color: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease-in-out;
}

.trainer-header:hover {
  transform: translateY(-5px);
}

.trainer-info {
  margin-bottom: 0;
}

.trainer-name {
  font-size: 2.5rem;
  font-weight: bold;
  letter-spacing: 0.8px;
}

.trainer-username {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.8);
  font-style: italic;
}

.trainer-bio {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.9);
  max-width: 500px;
  line-height: 1.6;
}
</style>