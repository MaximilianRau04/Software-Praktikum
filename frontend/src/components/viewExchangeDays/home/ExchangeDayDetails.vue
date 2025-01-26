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
      <p><strong>Id:</strong> {{ selectedExchangeDay.id }}</p>

      <button
        v-if="getCookie('role') === 'ADMIN'"
        @click="navigateToManageExchangeDay(selectedExchangeDay.id)"
        class="edit-button"
      >
        Verwalten
      </button>
    </div>
    <!-- Displaying associated events -->
    <div class="scrollableEvents">
      <h2>Workshops</h2>
      <div v-for="event in events" :key="event.id" v-if="events.length > 0">
        <!-- show event information -->
        <EventDetails :event="event"  v-if="!event.inviteOnly"/>
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
import { ExchangeDay } from "@/types/ExchangeDay";

import { Event } from "@/types/Event";
import { useRouter } from "vue-router";
import Cookies from "js-cookie";
import { showToast, Toast } from "@/types/toasts";
import { faXmark, faCheck } from "@fortawesome/free-solid-svg-icons";

const router = useRouter();
const selectedExchangeDay = ref<ExchangeDay | null>(null);

const props = defineProps<{
  exchangeDay: ExchangeDay | null;
}>();

const events = ref<Event[]>([]);
const isPastExchangeDay = ref(false);

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

function getCookie(name) {
  return Cookies.get(name);
}

/**
 * Checks if the exchange day is in the past.
 */
function checkIfPastExchangeDay() {
  if (selectedExchangeDay.value) {
    const now = new Date();
    const endDate = new Date(selectedExchangeDay.value.endDate);
    isPastExchangeDay.value = endDate < now;
  }
}

/**
 * Fetches the details of a selected exchange day from the API.
 *
 * @param {number} id - The ID of the exchange day to fetch.
 */
async function fetchExchangeDayDetails(id: number) {
  try {
    const response = await fetch(`${config.apiBaseUrl}/exchange-days/${id}`);
    if (!response.ok)
      showToast(
        new Toast(
          "Error",
          `Fehler Fetchen der exchange days`,
          "error",
          faXmark,
          10,
        ),
      );

    const data = await response.json();

    selectedExchangeDay.value = {
      id: data.id,
      name: data.name,
      startDate: new Date(data.startDate).getTime(),
      endDate: new Date(data.endDate).getTime(),
      description: data.description,
      location: data.location,
    };

    checkIfPastExchangeDay();
    await fetchEventDetails();
  } catch (error) {
    showToast(
      new Toast(
        "Error",
        `Fehler Fetchen der exchange days: ${error.message}`,
        "error",
        faXmark,
        10,
      ),
    );
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
    showToast(
      new Toast(
        "Error",
        `Fehler beim Abrufen der Events.`,
        "error",
        faXmark,
        10,
      ),
    );
  }
}

const navigateToManageExchangeDay = (exchangeDayId) => {
  router.push({ name: "manageExchangeDay", params: { exchangeDayId } });
};

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
