<script setup lang="ts">
import { RouterLink } from 'vue-router';
import { ref, onMounted } from "vue";
import ScrollableDivs from './Scrollable.vue';
import ExchangeDayDetails from './ExchangeDayDetails.vue'; 
import config from "../config";
import '../assets/main.css';

import { ExchangeDay, exchangeDays, selectedExchangeDay } from '../types/ExchangeDay'; 

function fetchAllExchangeDays() {
  fetch(`${config.apiBaseUrl}/exchange-days`) 
    .then(response => response.json())
    .then(data => data as ExchangeDay[])
    .then(data => {
      exchangeDays.value = data;
    })
    .catch(error => console.error(error));
}

function selectExchangeDay(exchangeDay: ExchangeDay) {
  selectedExchangeDay.value = exchangeDay;
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
      <ExchangeDayDetails :exchangeDay="selectedExchangeDay" />
    </div>
    <div class="rightSide">
      <ScrollableDivs :items="exchangeDays" @select-exchange-day="selectExchangeDay" />
    </div>
  </div>
</template>

