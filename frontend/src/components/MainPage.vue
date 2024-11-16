<template>
  <header class="header">
    <nav class="header-nav">
      <div class="nav-left">
        <RouterLink to="/home" class="nav-button">HOME</RouterLink>
        <RouterLink to="/events" class="nav-button">EVENTS</RouterLink>
      </div>

      <div class="nav-right">
        <RouterLink to="/user" class="nav-button">user: {{userData.username}}</RouterLink>
        <RouterLink to="/" class="nav-button">LOG OUT</RouterLink>
      </div>
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

<script setup lang="ts">
import { RouterLink } from 'vue-router';
import { ref, onMounted } from "vue";
import ScrollableDivs from './Scrollable.vue';
import ExchangeDayDetails from './ExchangeDayDetails.vue'; 
import config from "../config";
import '../assets/main.css';
import { ExchangeDay, exchangeDays, selectedExchangeDay } from '../types/ExchangeDay'; 
import { useRoute } from 'vue-router';


const route = useRoute();
const userData = route.query;

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


