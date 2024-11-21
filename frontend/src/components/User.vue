<template>
  <div class="container">
    <div class="events">
      <h1 v-if="userData.username">Willkommen, {{ userData.username }}</h1>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const userData = ref({
  username: route.query.username || '', 
  prename: route.query.prename || '',
  lastname: route.query.lastname || '',
  email: route.query.email || '',
  admin: route.query.admin === "true", 
});

const username = ref('');
const prename = ref('');
const lastname = ref('');
const email = ref('');
const admin = ref(false);


</script>

<style scoped>
.container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.events {
  text-align: center;
}

</style>