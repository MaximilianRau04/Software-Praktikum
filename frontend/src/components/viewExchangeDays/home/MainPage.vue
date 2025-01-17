<!-- 
  This component displays a two-column layout: 
   1. Left Column: Shows details of the currently selected Exchange Day using the `ExchangeDayDetails` component.
   2. Right Column: Displays a scrollable list of all available Exchange Days using the `ScrollableDivs` component. 
      - Users can select an Exchange Day from the list.
      - The selected Exchange Day is shown in the left column.
  Data is fetched from the API on component mount, populating the scrollable list.
-->
<template>
  <div class="container">
    <!-- Left side: Details of the selected Exchange Day -->
    <div class="leftSide">
      <ExchangeDayDetails :exchangeDay="selectedExchangeDay" />
    </div>
    <!-- Right side: Scrollable list of all Exchange Days -->
    <div class="rightSide">
      <ScrollableDivs
        :items="exchangeDays"
        @select-exchange-day="selectExchangeDay"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import ScrollableDivs from "@/components/viewExchangeDays/home/Scrollable.vue";
import ExchangeDayDetails from "@/components/viewExchangeDays/home/ExchangeDayDetails.vue";
import config from "@/config";
import "@/assets/main.css";
import {
  ExchangeDay,
  exchangeDays,
  selectedExchangeDay,
} from "@/types/ExchangeDay";
import { useRoute } from "vue-router";

const route = useRoute();
const userData = route.query;

/**
 * Fetches all Exchange Days from the API and stores them in the `exchangeDays` ref.
 */
function fetchAllExchangeDays() {
  fetch(`${config.apiBaseUrl}/exchange-days`)
    .then((response) => response.json())
    .then((data) => data as ExchangeDay[])
    .then((data) => {
      // Populate exchangeDays with data from the API
      exchangeDays.value = data;
    })
    .catch((error) => console.error(error));
}

/**
 * Sets the selected Exchange Day to the one clicked by the user.
 *
 * @param {ExchangeDay} exchangeDay - The Exchange Day object selected by the user.
 */
function selectExchangeDay(exchangeDay: ExchangeDay) {
  selectedExchangeDay.value = exchangeDay;
}

onMounted(() => fetchAllExchangeDays());
</script>
