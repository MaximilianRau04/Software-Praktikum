<template>
  <div class="container">
    <!-- left side for details -->
    <div class="leftSide">
      <ExchangeDayDetails v-if="selectedExchangeDay" :exchangeDay="selectedExchangeDay" />
    </div>

    <!-- right side for scrollables -->
    <div class="rightSide">
      <!-- Date Filter / Datepicker bleibt fixiert -->
      <div class="date-filter">
        <div class="date-picker-container">
          <input type="date" id="date-picker-from" v-model="selectedDateFrom" @change="filterExchangeDays" />
          <span class="date-range-separator">bis</span>
          <input type="date" id="date-picker-to" v-model="selectedDateTo" @change="filterExchangeDays" />
          <button type="button" @click="resetDateFilters" class="reset-button">✕</button>
        </div>
      </div>

      <!-- Kommende Exchange Days -->
      <div class="upcoming-section">
        <h2>Kommende Exchange Days</h2>
        <div class="scrollableEvents upcoming-list">
          <ScrollableDivs :exchangeDays="upcomingExchangeDaysList" @select-exchange-day="selectExchangeDay" />
        </div>
      </div>

      <!-- Vergangene Exchange Days -->
      <div class="past-section">
        <h2>Vergangene Exchange Days</h2>
        <div class="scrollableEvents past-list">
          <ScrollableDivs :exchangeDays="pastExchangeDaysList" @select-exchange-day="selectExchangeDay" />
        </div>
      </div>
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
const selectedDateFrom = ref("");
const selectedDateTo = ref("");
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

function filterExchangeDays() {
  if (!selectedDateFrom.value && !selectedDateTo.value) {
    categorizeExchangeDays();
    return;
  }

  const startTimestamp = selectedDateFrom.value
    ? new Date(selectedDateFrom.value).setHours(0, 0, 0, 0)
    : today;
  const endTimestamp = selectedDateTo.value
    ? new Date(selectedDateTo.value).setHours(0, 0, 0, 0)
    : Infinity;

  upcomingExchangeDaysList.value = exchangeDays.value.filter((day) => {
    const startDate = new Date(day.startDate).setHours(0, 0, 0, 0);
    const endDate = new Date(day.endDate).setHours(0, 0, 0, 0);

    return (
      startDate >= today &&
      startDate >= startTimestamp && startDate <= endTimestamp
    );
  });

  pastExchangeDaysList.value = exchangeDays.value.filter((day) => {
    const endDate = new Date(day.endDate).setHours(0, 0, 0, 0);

    return endDate < today && endDate >= startTimestamp && endDate <= endTimestamp;
  });
}

function resetDateFilters() {
  selectedDateFrom.value = "";
  selectedDateTo.value = "";
  categorizeExchangeDays();
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
