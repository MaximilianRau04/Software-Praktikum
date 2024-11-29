<!--
This component handles user authentication by providing both registration and login forms within the same interface. 
The user can toggle between the "Register" and "Login" views using a dynamic button. 

Key Features:
- Registration: Collects username, first name, last name, email, and admin status.
- Login: Accepts only a username for authentication.
- Dynamic form rendering based on the `isRegistered` state.
- Redirects to the `/home` route upon form submission.
-->
<template>
  <div class="login">
    <div class="create-box">
       <!-- Dynamic header based on registration state -->
      <h2 class="login-header" v-if="!isRegistered">Registrieren</h2>
      <h2 class="login-header" v-else>Login</h2>
      <!-- Form for registration/login -->
      <form @submit.prevent="handleLogin">
        <!-- Username field -->
        <div class="input-group">
          <label for="username">Benutzername</label>
          <input type="text" id="username" v-model="username" required />
        </div>
         <!-- Fields displayed only for registration -->
        <div class="input-group" v-if="!isRegistered">
          <label for="firstname">Vorname</label>
          <input type="text" id="prename" v-model="prename" required />
        </div>
        <div class="input-group" v-if="!isRegistered">
          <label for="lastname">Nachname</label>
          <input type="text" id="lastname" v-model="lastname" required />
        </div>
        <div class="input-group" v-if="!isRegistered">
          <label for="email">E-Mail</label>
          <input type="email" id="email" v-model="email" required />
        </div>
        <!-- Admin checkbox for registration -->
        <div class="input-group">
        <label for="admin" id="admin" v-if="!isRegistered">
        Admin?
        <input type="checkbox" v-model="admin" />
        </label>
        <!-- Submit button -->
        </div>
         <!-- Toggle button to switch views -->
        <button type="submit" class="login-button" v-if="!isRegistered">Registrieren</button>
        <button type="submit" class="login-button" v-else>Anmelden</button>
        <button class="login-button" v-if="!isRegistered" @click="changeToLogin">Bereits registriert?</button>

        <button class="login-button" v-else @click="changeToLogin">Registrieren?</button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import '../assets/login.css';

const router = useRouter();

// State variables
const isRegistered = ref(false);
const username = ref('');
const prename = ref('');
const lastname = ref('');
const email = ref('');
const admin = ref(false);

/**
 * Handles the registration/login process.
 * Redirects to the home page after collecting user data.
 */
const handleLogin = () => {
  const userData = {
    username: username.value,
    prename: prename.value,
    lastname: lastname.value,
    email: email.value,
    admin: admin.value.toString(),
  };
  router.push({
  path: '/home', // Redirect to home page
});
}

/**
 * Toggles between "Register" and "Login" views.
 */
const changeToLogin = () => {
  isRegistered.value = !isRegistered.value;
};
</script>

