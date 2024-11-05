<script setup>
import { RouterView } from 'vue-router';
</script>

<template>
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
  background-color: #01172F;;
}

#app {
  height: 100%;
  width: 100%;
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