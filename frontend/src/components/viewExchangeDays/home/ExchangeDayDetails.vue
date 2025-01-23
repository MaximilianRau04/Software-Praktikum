<!--
 * This Vue component displays detailed information about a selected exchange day,
 * including its name, location, description, date, and associated events.
-->
<template>
  <div v-if="selectedExchangeDay" class="exchangeDayDetails">
    <!-- Displaying exchange day details -->
    <div class="exchangeDayInfos">
      <h1>{{ selectedExchangeDay.name }}</h1>
      <p>
        <strong>Ort:</strong> {{ selectedExchangeDay.location.street }}
        {{ selectedExchangeDay.location.houseNumber }},
        {{ selectedExchangeDay.location.city }},
        {{ selectedExchangeDay.location.country }}
      </p>
      <p>
        <strong>Beschreibung:</strong> {{ selectedExchangeDay.description }}
      </p>
      <p>
        <strong>Datum:</strong>
        {{ formatDate(selectedExchangeDay.startDate) }} bis
        {{ formatDate(selectedExchangeDay.endDate) }}
      </p>
      <p>Id: {{ selectedExchangeDay.id }}</p>
    </div>

    <!-- Displaying associated events -->
    <div class="scrollableEvents">
      <h2>Workshops</h2>
      <div v-for="event in events" :key="event.id" v-if="events.length > 0">
        <!-- show event information -->
        <EventDetails :event="event" />
      </div>
      <p v-else>Keine Workshops vorhanden...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, ref, watch } from "vue";
import EventDetails from "@/components/viewExchangeDays/home/EventDetails.vue";
import config from "@/config";
import "@/assets/exchange-day-details.css";
import { ExchangeDay, exchangeDays } from "@/types/ExchangeDay";
const selectedExchangeDay = ref<ExchangeDay | null>(null);
import { Event } from "@/types/Event";

const props = defineProps<{
  exchangeDay: ExchangeDay | null;
}>();

const events = ref<Event[]>([]);

/**
 * Formats a timestamp into a human-readable date string.
 *
 * @param {number} timestamp - The date in milliseconds.
 * @returns {string} - The formatted date string in 'DD.MM.YYYY' format.
 */
function formatDate(timestamp: number): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE");
}

/**
 * Fetches the details of a selected exchange day from the API.
 *
 * @param {number} id - The ID of the exchange day to fetch.
 */
async function fetchExchangeDayDetails(id: number) {
  try {
    const response = await fetch(`${config.apiBaseUrl}/exchange-days/${id}`);
    if (!response.ok) throw new Error("Failed to fetch exchange day details.");

    const data = await response.json();
    console.log("ExchangeDay data loaded:", data);

    selectedExchangeDay.value = {
      id: data.id,
      name: data.name,
      startDate: new Date(data.startDate).getTime(),
      endDate: new Date(data.endDate).getTime(),
      description: data.description,
      location: data.location,
    };

    await fetchEventDetails();
  } catch (error) {
    console.error("Error fetching exchange day details:", error);
  }
}

/**
 * Fetches the details of events associated with the exchange day.
 */
async function fetchEventDetails() {
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/exchange-days/${selectedExchangeDay.value.id}/events`,
    );
    if (!response.ok) {
      throw new Error(
        `Failed to fetch events from exchange day ${selectedExchangeDay.value.id}`,
      );
    }
    const responseData: Event[] = await response.json();
    events.value = responseData;
  } catch (error) {
    console.error("Error fetching event:", error);
  }
}

/**
 * Watch for changes to the `exchangeDay` prop and fetch new details when it changes.
 */
watch(
  () => props.exchangeDay?.id,
  (newId, oldId) => {
    if (newId !== oldId && newId != null) {
      fetchExchangeDayDetails(newId);
    }
  },
);

/**
 * On component mount, fetch the details of the initial `exchangeDay` if provided.
 */
onMounted(() => {
  if (props.exchangeDay?.id != null) {
    fetchExchangeDayDetails(props.exchangeDay.id);
  }
});
</script>
