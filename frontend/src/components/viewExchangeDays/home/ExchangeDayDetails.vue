<template>
  <div class="exchangeDayDetails">
    <div class="exchangeDayHeader">
      <h1>{{ selectedExchangeDay?.name }} - Exchange Day</h1>
      <button v-if="isAdmin" class="manage-button" @click="handleManage(selectedExchangeDay?.id)">
        Verwalten
      </button>
    </div>

    <div class="exchangeDayContent">
      <!-- Left Column -->
      <div class="left-column">
        <div class="description">
          <p>{{ selectedExchangeDay?.description }}</p>
        </div>

        <div class="info-grid">
          <div class="info-box">
            <div class="info-header">
              <CalendarIcon class="icon" />
              <h4>Termin</h4>
            </div>
            <div v-if="selectedExchangeDay?.startDate !== selectedExchangeDay?.endDate" class="info-content">
              {{ formatDateLong(selectedExchangeDay?.startDate) }}<br>
              bis {{ formatDateLong(selectedExchangeDay?.endDate) }}
            </div>
            <div v-else class="info-content">{{formatDateLong(selectedExchangeDay?.startDate)}}</div>
          </div>

          <div class="info-box">
            <div class="info-header">
              <LocationIcon class="icon" />
              <h4>Ort</h4>
            </div>
            <div class="info-content">
              {{ selectedExchangeDay?.location.street }} {{ selectedExchangeDay?.location.houseNumber }}<br>
              {{ selectedExchangeDay?.location.postalCode }} {{ selectedExchangeDay?.location.city }}<br>
              {{ selectedExchangeDay?.location.country }}
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column (Map) -->
      <div class="right-column">
        <MapComponent v-if="coordinates" :coordinates="coordinates" />
      </div>
    </div>
    <div class="exchangeDayHeader">
        <h2>Geplante Workshops</h2>
      </div>
    <!-- Events Section -->
    <div class="scrollableEventList">
      
      <div class="event-grid">
        <div v-for="event in events" :key="event.id" class="event-item">
          <EventDetails :event="event" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, computed, ref, watch } from "vue";
import EventDetails from "@/components/viewExchangeDays/home/EventDetails.vue";
import config from "@/config";
import "@/assets/exchange-day-details.css";
import { ExchangeDay } from "@/types/ExchangeDay";
import { Event } from "@/types/Event";
import { useRouter } from "vue-router";
import Cookies from "js-cookie";
import { showToast, Toast } from "@/types/toasts";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import MapComponent from "../MapComponent.vue";
import { CalendarIcon, MapPinIcon as LocationIcon } from '@heroicons/vue/24/outline';

const router = useRouter();
const selectedExchangeDay = ref<ExchangeDay | null>(null);

const props = defineProps<{ exchangeDay: ExchangeDay | null }>();

const events = ref<Event[]>([]);
const isPastExchangeDay = ref(false);
const coordinates = ref(null);

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

const isAdmin = computed(() => Cookies.get('role') === 'ADMIN');

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

    if(selectedExchangeDay.value?.location){
      await geocodeAddress();
    }

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

const handleManage = (exchangeDayId) => {
  router.push({ name: "manageExchangeDay", params: { exchangeDayId } });
};

const geocodeAddress = async () => {
  const location = selectedExchangeDay.value?.location;
  const address = `${location.street} ${location.houseNumber}, ${location.postalCode} ${location.city}, ${location.country}`;

  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`
    );
    const data = await response.json();
    if (data.length > 0) {
      coordinates.value = {
        lat: parseFloat(data[0].lat),
        lon: parseFloat(data[0].lon)
      };
    }
  } catch (error) {
    console.error('Geocoding error:', error);
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
  }
);

/**
 * On component mount, fetch the details of the initial `exchangeDay` if provided.
 */
onMounted(async () => {
  try {
    if (props.exchangeDay?.id) {
      await fetchExchangeDayDetails(props.exchangeDay.id);
    }

    if (selectedExchangeDay.value?.location) {
      //await geocodeAddress();
    }
  } catch (error) {
    console.error('Mount error:', error);
  }
});
</script>