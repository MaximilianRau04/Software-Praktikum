<template>
  <div class="login">
    <div class="create-box">
      <h2 class="login-header" v-if="!isRegistered">Registrieren</h2>
      <h2 class="login-header" v-else>Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="username">Benutzername</label>
          <input type="text" id="username" v-model="username" required />
        </div>

        <div class="input-group" v-if="!isRegistered">
          <label for="firstname">Vorname</label>
          <input type="text" id="firstname" v-model="firstname" required />
        </div>

        <div class="input-group" v-if="!isRegistered">
          <label for="lastname">Nachname</label>
          <input type="text" id="lastname" v-model="lastname" required />
        </div>

        <div class="input-group" v-if="!isRegistered">
          <label for="role">Rolle</label>
          <select v-model="role" required>
            <option value="USER">Benutzer</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>

        <button type="submit" class="login-button" v-if="!isRegistered">
          Registrieren
        </button>
        <button type="submit" class="login-button" v-else>Anmelden</button>

        <button
          class="login-button"
          v-if="!isRegistered"
          @click="changeToLogin"
        >
          Bereits registriert?
        </button>
        <button class="login-button" v-else @click="changeToLogin">
          Registrieren?
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { globalState } from "@/types/User";
import type { User } from "@/types/User";
import config from "@/config.js";
import "../assets/login.css";
import Cookies from "js-cookie";

const router = useRouter();

const isRegistered = ref(false);
const username = ref("");
const firstname = ref("");
const lastname = ref("");
const role = ref("USER");

const apiUrl = "http://193.196.54.172:8000/api/users";

/**
 * Handles the login or registration of a user.
 */
const handleLogin = async () => {
  try {
    if (isRegistered.value) {
      const response = await fetch(
        `${config.apiBaseUrl}/users/search?username=${username.value}`,
      );
      if (!response.ok) throw new Error("Benutzer nicht gefunden");

      const userData: User = await response.json();

      if (userData && userData.id) {
        globalState.setUser(userData);

        // Set user data in cookies
        Cookies.set("userId", userData.id, { expires: 7 });
        Cookies.set("username", userData.username, { expires: 7 });
        Cookies.set("firstname", userData.firstname || "", { expires: 7 });
        Cookies.set("lastname", userData.lastname || "", { expires: 7 });
        Cookies.set("role", userData.role || "USER", { expires: 7 });

        router.push("/home");
      } else {
        alert("Anmeldung fehlgeschlagen. Benutzer nicht gefunden.");
      }
    } else {
      const userData: User = {
        username: username.value,
        firstname: firstname.value,
        lastname: lastname.value,
        role: role.value,
      };

      const response = await fetch(`${config.apiBaseUrl}/users`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userData),
      });

      const data: User = await response.json();

      if (response.ok && data.id) {
        alert("Registrierung erfolgreich");
        isRegistered.value = true;
      } else {
        alert("Registrierung fehlgeschlagen. Bereits registriert?");
      }
    }
  } catch (error) {
    console.error("Error:", error);
    alert(
      isRegistered.value
        ? "Anmeldung fehlgeschlagen"
        : "Fehler bei der Registrierung",
    );
  }
};

/**
 * Changes the view between login and registration.
 */
const changeToLogin = () => {
  isRegistered.value = !isRegistered.value;
};
</script>
