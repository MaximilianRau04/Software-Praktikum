<template>
  <main>
    <router-view />
  </main>
  <div class="toast-container">
    <div
      v-for="toast in activeToasts"
      :key="toast.key"
      :class="['toast', toast.type, { 'fade-in': !toast.expired }]"
    >
      <div>
        <h3>{{ toast.title }}</h3>
        <p>{{ toast.message }}</p>
      </div>
      <span class="close" @click="toast.close()">×</span>
    </div>
  </div>

  <Toasts vertical-position="top" horizontal-position="end">
    <template v-for="toast of activeToasts" :key="toast.key">
      <Toast :type="toast.type" class="alert alert-border-left alert-info">
        <div class="flex-fill flex flex-column">
          <div class="flex">
            <h3 class="flex-fill">{{ toast.title }}</h3>
            <Close @click="toast.close()"/>
          </div>
          <div class="flex">
            <font-awesome-icon
              :icon="toast.icon"
              size="xl"
              class="mie8 pbs2"
            ></font-awesome-icon>
            <div class="flex-fill">
              {{ toast.message }}
            </div>
          </div>
        </div>
      </Toast>
      <div class="mbe14" />
    </template>
  </Toasts>
</template>

<script setup>
import { onMounted } from "vue";
import { RouterView, useRouter } from "vue-router";
import Cookies from "js-cookie";
import "./assets/app.css";
import { globalState } from "@/types/User";
import { activeToasts } from "@/types/toasts";
import {
  Close,
  Header,
  HeaderNav,
  HeaderNavItem,
  Toast,
  Toasts,
} from "agnostic-vue";
import "agnostic-vue/dist/index.css";

const router = useRouter();

onMounted(() => {
  const userId = Cookies.get("userId");
  if (userId) {
    const userData = {
      id: userId,
      username: Cookies.get("username"),
      firstname: Cookies.get("firstname"),
      lastname: Cookies.get("lastname"),
      role: Cookies.get("role"),
    };
    globalState.setUser(userData);
  }
});
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 10px;
  right: 10px;
  z-index: 9999;
  max-width: 400px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none;
}

.toast {
  background-color: #333;
  color: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  font-size: 15px;
  line-height: 1.5;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  transition: all 0.3s ease-in-out;
  pointer-events: all;
}

.toast.success {
  background-color: #36b13a;
}

.toast.error {
  background-color: rgb(185, 49, 40);
}

.toast.info {
  background-color: #2196f3;
}

.toast.warning {
  background-color: #d99b3f;
}

.toast .close {
  cursor: pointer;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  transition: color 0.2s ease-in-out;
}

.toast .close:hover {
  color: #e0e0e0;
}

.toast.fade-in {
  animation: fadeIn 0.3s ease-in-out forwards;
}

.toast.fade-out {
  animation: fadeOut 0.3s ease-in-out forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeOut {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(20px);
  }
}

.toast h3 {
  font-size: 16px;
  font-weight: bold;
  margin: 0;
}

.toast p {
  font-size: 14px;
  margin: 5px 0 0 0;
}

@media (max-width: 600px) {
  .toast-container {
    top: 20px;
    right: 20px;
  }

  .toast {
    font-size: 14px;
    padding: 12px 16px;
    max-width: 90%;
  }
}
</style>
