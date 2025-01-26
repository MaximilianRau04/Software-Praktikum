<template>
  <div class="trainer-header">
    <h1 class="trainer-name">{{ user.firstname }} {{ user.lastname }}</h1>
    <p class="trainer-bio">{{ bio }}</p>
    <p class="trainer-username">@{{ user.username }}</p>
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
  padding: 1rem;
  border-bottom: 1px solid #ccc;
  margin-bottom: 1rem;
}

.trainer-name {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.trainer-bio {
  font-size: 1rem;
  color: #555;
  margin-bottom: 0.5rem;
}

.trainer-username {
  font-size: 1rem;
  color: #888;
  font-style: italic;
}
</style>
