<template>
  <div class="container">
    <!-- left side for details -->
    <div class="leftSide">
      <ExchangeDayDetails
        v-if="selectedExchangeDay"
        :exchangeDay="selectedExchangeDay"
      />
    </div>

    <!-- right side for scrollables -->
    <div class="rightSide">
      <h2>Kommende Exchange Days</h2>
      <ScrollableDivs
        :exchangeDays="upcomingExchangeDaysList"
        @select-exchange-day="selectExchangeDay"
      />

      <h2>Vergangene Exchange Days</h2>
      <ScrollableDivs
        :exchangeDays="pastExchangeDaysList"
        @select-exchange-day="selectExchangeDay"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import ScrollableDivs from "@/components/viewExchangeDays/home/Scrollable.vue";
import ExchangeDayDetails from "@/components/viewExchangeDays/home/ExchangeDayDetails.vue";
import config from "@/config";
import "@/assets/main.css";
import { ExchangeDay } from "@/types/ExchangeDay";
import { showToast, Toast } from "@/types/toasts";
import { faXmark, faCheck } from "@fortawesome/free-solid-svg-icons";

const exchangeDays = ref<ExchangeDay[]>([]);
const upcomingExchangeDaysList = ref<ExchangeDay[]>([]);
const pastExchangeDaysList = ref<ExchangeDay[]>([]);
const selectedExchangeDay = ref<ExchangeDay | null>(null);
const today = new Date().setHours(0, 0, 0, 0);

/**
 * Fetches all Exchange Days from the API and categorizes them.
 */
function fetchAllExchangeDays() {
  fetch(`${config.apiBaseUrl}/exchange-days`)
    .then((response) => response.json())
    .then((data: ExchangeDay[]) => {
      exchangeDays.value = data.map((day) => ({
        ...day,
        startDate: new Date(day.startDate).getTime(),
        endDate: new Date(day.endDate).getTime(),
      }));

      categorizeExchangeDays();
    })
    .catch((error) =>
      showToast(
        new Toast(
          "Error",
          `Fehler beim Abrufen der Exchange Days.`,
          "error",
          faXmark,
          10,
        ),
      ),
    );
}

/**
 * Categorizes Exchange Days into upcoming and past based on today's date.
 */
 function categorizeExchangeDays() {
  upcomingExchangeDaysList.value = exchangeDays.value.filter((day) => {
    const startDate = new Date(day.startDate).setHours(0, 0, 0, 0);
    const endDate = new Date(day.endDate).setHours(0, 0, 0, 0);
    return startDate > today || (startDate <= today && endDate >= today);
  });

  pastExchangeDaysList.value = exchangeDays.value.filter((day) => {
    const endDate = new Date(day.endDate).setHours(0, 0, 0, 0);
    return endDate < today;
  });

  if (upcomingExchangeDaysList.value.length > 0) {
    selectExchangeDay(upcomingExchangeDaysList.value[0]);
  }
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
