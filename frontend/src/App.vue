<template>
  <main>
    <router-view />
  </main>
</template>

<script setup>
import { onMounted } from "vue";
import { RouterView, useRouter } from "vue-router";
import Cookies from "js-cookie";
import "./assets/app.css";
import { globalState } from "@/types/User";

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
