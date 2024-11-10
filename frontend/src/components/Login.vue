<template>
  <div class="login">
    <div class="login-box">
      <h2 class="green">Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="username">Username</label>
          <input type="text" id="username" v-model="username" required />
        </div>
        <div class="input-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <button type="submit" class="login-button">Login</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import '../assets/login.css';

const username = ref('');
const password = ref('');
const router = useRouter();

const handleLogin = () => {
  console.log('Username:', username.value);
  console.log('Password:', password.value);

  if (username.value === 'user' && password.value === 'pass') {
    router.push('/main');
    console.log("Richtig");
  } else {
    alert('Invalid credentials');
  }
};
</script>
