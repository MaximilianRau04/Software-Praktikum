<script setup>
import { RouterView } from 'vue-router';
import './assets/app.css';
</script>

<template>
  <main>
    <RouterView />
  </main>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
}
main{
  padding:0;
  max-height: 85vh;
  box-sizing: border-box; 
}

</style>