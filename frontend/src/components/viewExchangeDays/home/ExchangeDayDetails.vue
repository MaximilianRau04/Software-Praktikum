<template>
  <div class="exchangeDayDetails">
    <div class="exchangeDayHeader">
      <h1>{{ selectedExchangeDay?.name }}</h1>
    </div>
    <div class="exchangeDayContent">
      <div class="description">
        <p>{{ selectedExchangeDay?.description }}</p>
      </div>
      <div class="exchangeDayInfo">
        <p> {{ formatDateLong(selectedExchangeDay?.startDate) }} bis {{ formatDateLong(selectedExchangeDay?.endDate) }}</p>
        <p> {{ selectedExchangeDay?.location.street }} {{ selectedExchangeDay?.location.houseNumber }}</p>
        <p> {{ selectedExchangeDay?.location.postalCode }} {{ selectedExchangeDay?.location.city }}, {{ selectedExchangeDay?.location.country }} </p>
      </div>
    </div>
    <div class="scrollableEvents">
      <h2>Events</h2>
      <div v-for="event in events" :key="event.id" class="event-item">
        <h3>{{ event.name }}</h3>
        <p>{{ event.description }}</p>
      </div>
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
import { faXmark } from "@fortawesome/free-solid-svg-icons";

const router = useRouter();
const selectedExchangeDay = ref<ExchangeDay | null>(null);

const props = defineProps<{ exchangeDay: ExchangeDay | null }>();

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

/**
 * Formats a timestamp into a long date format with weekday.
 *
 * @param {number} timestamp - The date in milliseconds.
 * @returns {string} - The formatted date string like 'Mittwoch, 31.03.2025'.
 */
function formatDateLong(timestamp: number): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE", {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
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
          `Fehler beim Laden der Exchange days`,
          "error",
          faXmark,
          10
        )
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
        `Fehler beim Laden der exchange days`,
        "error",
        faXmark,
        10
      )
    );
  }
}

/**
 * Fetches the details of events associated with the exchange day.
 */
async function fetchEventDetails() {
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/exchange-days/${selectedExchangeDay.value.id}/events`
    );
    if (!response.ok) {
      throw new Error(
        `Failed to fetch events from exchange day ${selectedExchangeDay.value.id}`
      );
    }
    const responseData: Event[] = await response.json();
    events.value = responseData;
    console.log(events.value);
  } catch (error) {
    showToast(
      new Toast("Error", `Fehler beim Abrufen der Events.`, "error", faXmark, 10)
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
  }
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