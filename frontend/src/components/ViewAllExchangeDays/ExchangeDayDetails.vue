<!--
 * This Vue component displays detailed information about a selected exchange day,
 * including its name, location, description, date, and associated events.
-->
<template>
  <div v-if="selectedExchangeDay" class="exchangeDayDetails">

    <!-- Displaying exchange day details -->
    <div class="exchangeDayInfos">
      <h1>{{ selectedExchangeDay.name }}</h1>
      <p><strong>Location:</strong> {{ selectedExchangeDay.location }}</p>
      <p><strong>Description:</strong> {{ selectedExchangeDay.description }}</p>
      <p><strong>Date:</strong> {{ formatDate(selectedExchangeDay.date) }}</p>
      <p>Id: {{ selectedExchangeDay.id }}</p>
    </div>
    
    <!-- Displaying associated events -->
    <div class="scrollableEvents"> 
      <h2>Events</h2>
      <div v-for="event in events" :key="event.id" v-if="events.length > 0">
         <!-- show event information -->
        <EventDetails :event="event" />
      </div>
      <p v-else>No events found.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, ref, watch } from "vue";
import EventDetails from '@/components/ViewAllExchangeDays/EventDetails.vue';
import config from "../../config";
import '../../assets/exchange-day-details.css'; 
import { ExchangeDay, exchangeDays } from '../../types/ExchangeDay'; 
const selectedExchangeDay = ref<ExchangeDay | null>(null);
import { Event } from '../../types/Event';


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

    // Store the fetched exchange day details in `selectedExchangeDay
    selectedExchangeDay.value = {
      id: data.id,
      name: data.name,
      date: new Date(data.date).getTime(),
      description: data.description,
      location: data.location,
      events: data.eventIds || []
    };

     // Fetch details of associated events, if available
    if (Array.isArray(data.eventIds) && data.eventIds.length > 0) {
      console.log("Event IDs found:", data.eventIds);
      await fetchEventDetails(data.eventIds);
    } else {
      console.warn("No event IDs found in exchangeDayDetails.");
      events.value = []; 
    }
  } catch (error) {
    console.error("Error fetching exchange day details:", error);
  }
}

/**
 * Fetches the details of events associated with the exchange day.
 * 
 * @param {number[]} eventIds - Array of event IDs to fetch.
 */
async function fetchEventDetails(eventIds: number[]) {

  const fetchedEvents: Event[] = [];
  const eventFetches = eventIds.map(async (eventId) => {
    try {
      const response = await fetch(`${config.apiBaseUrl}/events/${eventId}`);
      if (!response.ok) throw new Error(`Failed to fetch event ${eventId}`);
      const eventData = await response.json();
      console.log("Event data loaded:", eventData);
      fetchedEvents.push(eventData as Event);
    } catch (error) {
      console.error("Error fetching event:", error);
    }
  });

  // Wait for all event fetch operations to complete
  await Promise.all(eventFetches); 
  events.value = fetchedEvents; 
  console.log("All events loaded:", events.value);
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
onMounted(() => {
  if (props.exchangeDay?.id != null) {
    fetchExchangeDayDetails(props.exchangeDay.id);
  }
});
</script>