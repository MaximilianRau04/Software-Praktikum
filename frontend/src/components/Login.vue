<template>
  <div class="login-container">
    <div class="auth-box" :class="{ 'register-mode': !isRegistered }">
      <!-- Login Section -->
      <div class="login-section" v-if="isRegistered">
        <h2 class="auth-title">Willkommen zurück!</h2>
        <form @submit.prevent="handleLogin">
          <div class="input-group">
            <input type="text" v-model="username" placeholder="Nutzername" required class="auth-input" />
          </div>

          <div class="input-group password-group">
            <input :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="Passwort" required
              class="auth-input" />
            <button type="button" class="password-toggle" @click="showPassword = !showPassword">
              <font-awesome-icon :icon="showPassword ? 'eye-slash' : 'eye'" class="password-icon" />
            </button>
          </div>

          <button type="submit" class="auth-button primary">
            Einloggen
          </button>
        </form>

        <div class="auth-switch">
          <span>Noch keinen Account?</span>
          <button @click="changeToLogin" class="link-button">
            Hier Registrieren
          </button>
        </div>
      </div>

      <!-- Registration Section -->
      <div class="register-section" v-else>
        <div class="registration-steps">
          <button class="step-dot" :class="{ active: currentStep === 1 }" @click="currentStep = 1"></button>
          <button class="step-dot" :class="{ active: currentStep === 2 }" @click="currentStep = 2"
            :disabled="!canProceedToStep2"></button>
        </div>

        <!-- Step 1: Basic Info -->
        <div v-show="currentStep === 1">
          <h2 class="auth-title">Account Erstellen</h2>
          <form @submit.prevent="handleStep(2)">
            <div class="input-group">
              <input type="text" v-model="username" placeholder="Nutzername" required class="auth-input" />
            </div>

            <div class="input-group password-group">
              <input :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="Passwort" required
                class="auth-input" />
              <button type="button" class="password-toggle" @click="showPassword = !showPassword">
                <font-awesome-icon :icon="showPassword ? 'eye-slash' : 'eye'" class="password-icon" />
              </button>
            </div>
            <small class="password-info">Das Passwort muss zwischen 6 und 40 Zeichen lang sein.</small>

            <div class="input-group password-group">
              <input :type="showPassword ? 'text' : 'password'" v-model="passwordRepeat"
                placeholder="Passwort wiederholen" required class="auth-input" />
              <button type="button" class="password-toggle" @click="showPassword = !showPassword">
                <font-awesome-icon :icon="showPassword ? 'eye-slash' : 'eye'" class="password-icon" />
              </button>
            </div>

            <div class="input-group role-selection">
              <select v-model="role" required class="auth-select">
                <option value="USER">Nutzer</option>
                <option value="ADMIN">Administrator</option>
              </select>
              <v-tooltip>
                <button type="button" class="info-icon">
                  <font-awesome-icon icon="circle-question" class="icon" />
                </button>
                <template #popper>
                  <div class="tooltip-content">
                    <h4>Account Roles</h4>
                    <p>Regular User: Basic access</p>
                    <p>Administrator: Full system access</p>
                  </div>
                </template>
              </v-tooltip>
            </div>

            <button type="submit" class="auth-button primary" :disabled="!canProceedToStep2">
              Weiter
            </button>
          </form>
        </div>

        <!-- Step 2: Additional Info -->
        <div v-show="currentStep === 2">
          <h2 class="auth-title">Profil ergänzen</h2>
          <form @submit.prevent="handleRegistration">
            <div class="input-group">
              <input type="text" v-model="firstName" placeholder="Vorname" required class="auth-input" />
            </div>

            <div class="input-group">
              <input type="text" v-model="lastName" placeholder="Nachname" required class="auth-input" />
            </div>

            <div class="input-group">
              <textarea v-model="bio" placeholder="Über Sie (optional)" class="auth-textarea" rows="3"></textarea>
              <v-tooltip>
                <button type="button" class="info-icon">
                  <font-awesome-icon icon="circle-question" />
                </button>
                <template #popper>
                  <div class="tooltip-content">
                    Diese Informationen werden auf Ihrem Nutzerprofil angezeigt.
                  </div>
                </template>
              </v-tooltip>
            </div>

            <div class="input-group">
              <TagInput v-model="tags" :available-tags="allTags" :tagSelect="selectionOnlyTags" />
            </div>

            <div class="form-actions">
              <button type="button" class="auth-button secondary" @click="currentStep = 1">
                Zurück
              </button>
              <button type="submit" class="auth-button primary" :disabled="!isRegistrationValid">
                Registrierung abschließen
              </button>
            </div>
          </form>
        </div>

        <div class="auth-switch">
          <span>Bereits Registriert?</span>
          <button @click="changeToLogin" class="link-button">
            Hier Einloggen
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import api from "@/util/api"
import { useAuth } from "@/util/auth";
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import '@/assets/login.css'

import { showToast, Toast } from "@/types/toasts";
import { faXmark, faCheck } from "@fortawesome/free-solid-svg-icons";

import TagInput from './profile/TagInput.vue';
import { Tag } from "@/types/Tag";

const { setToken } = useAuth();
const router = useRouter();
const route = useRoute();

const currentStep = ref(1);
const showPassword = ref(false);

// Form models
const username = ref('');
const password = ref('');
const passwordRepeat = ref('');
const role = ref('USER');
const firstName = ref('');
const lastName = ref('');
const bio = ref('');
const tags = ref<Tag[]>([]);
const allTags = ref<Tag[]>([])
const isRegistered = ref(true);
const selectionOnlyTags = true;

// Computed properties
const canProceedToStep2 = computed(() => {
  return (
    username.value &&
    password.value &&
    password.value === passwordRepeat.value &&
    role.value
  );
});

const isRegistrationValid = computed(() => {
  return (
    firstName.value &&
    lastName.value &&
    canProceedToStep2.value
  );
});

// Methods
const fetchTags = async () => {
  try {
    const tagResponse = await api.get(`/tags`);
    allTags.value = tagResponse.data;
  } catch (error) {
    console.error('Error fetching tags:', error)
  }
};

const handleStep = (step: number) => {
  if (step === 2 && canProceedToStep2.value) {
    fetchTags();
    currentStep.value = 2;
  }
};

const handleRegistration = async () => {
  if (isRegistrationValid.value) {
    try {
      const roles = ["USER"];
      if (role.value === "ADMIN") {
        roles.push("ADMIN");
      }
      const userRegisterData = {
        username: username.value,
        password: password.value,
        roles: roles,
        firstName: firstName.value,
        lastName: lastName.value,
        bio: bio.value,
        tags: tags.value.map(tag => tag.name)
      };

      const response = await api.post(`/auth/signup`, userRegisterData);

      if (response.status === 200) {
        changeToLogin();

        showToast(
          new Toast(
            "Erfolg",
            `Sie haben sich erfolgreich registriert. Fahren Sie mit dem Login fort.`,
            "success",
            faXmark,
            5
          )
        );
      }
      // Add toast based on error messages from backend

    } catch (error) {
      showToast(
        new Toast(
          "Fehler",
          `Registrierung ist fehlgeschlagen. Versuchen Sie es später erneut.`,
          "error",
          faXmark,
          5
        )
      );
    }
  }
};

const handleLogin = async () => {
  try {
    const userLoginData = {
      username: username.value,
      password: password.value,
    };

    const response = await api.post(`/auth/signin`, userLoginData);
    setToken(response.data.token);
    if (response.status === 200) {
      const redirect = route.query.redirect;
      const redirectPath = Array.isArray(redirect) ? redirect[0] : redirect || '/home';
      router.replace(redirectPath);
    }
    // add toast for bad credentials or not found etc.
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `${error.statusText}`,
        "error",
        faXmark,
        5
      )
    );
  }
};

const changeToLogin = () => {
  isRegistered.value = !isRegistered.value;
  currentStep.value = 1;
  resetForm();
};

const resetForm = () => {
  username.value = '';
  password.value = '';
  passwordRepeat.value = '';
  role.value = 'USER';
  firstName.value = '';
  lastName.value = '';
  bio.value = '';
  tags.value = [];
};
</script>
