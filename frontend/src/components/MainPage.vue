<script setup lang="ts">
import { RouterView, RouterLink } from 'vue-router';
import Upcoming from './Upcoming.vue';
import Detail from './Detail.vue';
import ScrollableDivs from './Scrollable.vue';

import config from "../config";
import { onMounted, ref } from "vue";

interface exchangeDay {
  id: number;
  name: string;
  ageInYears: number;
  picUrl: string;
}

const exchangeDays = ref([]);

function fetchAllExchangeDays() {
  fetch(`${config.apiBaseUrl}/exchange-days`)
    .then(response => response.json())
    .then(data => data as exchangeDay[])
    .then(data => {
      console.log(data);
      exchangeDays.value = data;
    })
    .catch(error => console.error(error));
}

onMounted(() => fetchAllExchangeDays());
</script>

<template>
  <header class="header">
    <nav class="header-nav">
      <RouterLink to="/" class="nav-button">login</RouterLink>
      <RouterLink to="/main" class="nav-button">main</RouterLink>
    </nav>
  </header>
  <div class="container">
    <div class="leftSide">
      <div class="catBox" v-for="exchangeDay in exchangeDays" :key="exchangeDay.id">
      <h3>{{ exchangeDay.name }}</h3>
      <p>Age: {{ exchangeDay.ageInYears }} years</p>
      <img v-bind:alt="exchangeDay.name" v-bind:src="exchangeDay.picUrl" class="catLogo"/>
    </div>
    </div>
    <div class="rightSide">
      <ScrollableDivs :items="exchangeDays" />
    </div>
  </div>
</template>


<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  background-color: white; /* Weißer Hintergrund */
}

#app {
  height: 100%;
}

.header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 10px;
}

.header-nav {
  display: flex;
  gap: 10px;
}

.nav-button {
  display: inline-block;
  background-color: #0288d1; /* Hintergrundfarbe der Buttons */
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

.container {
  display: flex;
  height: calc(100% - 50px); /* Adjust based on header height */
}

.leftSide, .rightSide {
  flex: 1;
  padding: 20px;
  background-color: white; /* Hintergrundfarbe der Container */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Optional: Schatten für Container */
  border-radius: 10px; /* Optional: Abgerundete Ecken für Container */
}
</style>