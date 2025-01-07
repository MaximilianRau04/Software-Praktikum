<script setup>
import MainPage from "./MainPage.vue";
import { RouterView } from "vue-router";
</script>

<template>
  <div class="home-container">
    <RouterView />
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  flex: 0 0 300px;
}

.main-page {
  flex: 1;
}
</style>
