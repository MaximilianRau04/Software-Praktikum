<script setup>
import { RouterView } from 'vue-router';
</script>

<template>
  <header class="header">
    <nav class="header-nav">
      <RouterLink to="/" class="nav-button">login</RouterLink>
      <RouterLink to="/main" class="nav-button">main</RouterLink>
    </nav>
  </header>

  <main>
    <RouterView />
  </main>
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  background-color: #01172F;
}

#app {
  height: 100%;
}

.header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 10px;
  background-color: #333;
}

.header-nav {
  display: flex;
  gap: 10px;
}

.nav-button {
  display: inline-block;
  background-color: #0288d1;
  color: white;
  border: none;
  padding: 10px 20px;
  margin: 5px;
  cursor: pointer;
  font-size: 16px;
  text-decoration: none;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.nav-button:hover {
  background-color: #0277bd;
}

.wrapper {
  text-align: center;
  margin: 20px;
}

header {
  background-color: #f4f4f4;
  padding: 10px;
}

main {
  padding: 20px;
}
</style>