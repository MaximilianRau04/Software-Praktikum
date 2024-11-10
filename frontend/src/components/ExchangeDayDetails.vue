<template>
  <div v-if="selectedExchangeDay" class="exchange-day-details">
    <h1>{{ selectedExchangeDay.name }}</h1>
    <p><strong>Location:</strong> {{ selectedExchangeDay.location }}</p>
    <p><strong>Description:</strong> {{ selectedExchangeDay.description }}</p>
    <p><strong>Date:</strong> {{ formatDate(selectedExchangeDay.date) }}</p>
    <p>Id: {{ selectedExchangeDay.id }}</p>
    
    <h2>Events</h2>
    <div v-for="event in events" :key="event.id">
      <EventDetails :event="event" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, ref, watch } from "vue";
import EventDetails from './EventDetails.vue';
import config from "../config";
import '../assets/exchangeDayDetails.css'; 
import { ExchangeDay, exchangeDays, selectedExchangeDay } from '../types/ExchangeDay'; 
import { Event } from '../types/Event';


const props = defineProps<{
  exchangeDay: ExchangeDay | null;
}>();

const events = ref<Event[]>([]);

function formatDate(timestamp: number): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE");
}

async function fetchExchangeDayDetails(id: number) {
  try {
    const response = await fetch(`${config.apiBaseUrl}/exchange-days/${id}`);
    if (!response.ok) throw new Error("Failed to fetch exchange day details.");
    
    const data = await response.json();
    console.log("ExchangeDay data loaded:", data);

    selectedExchangeDay.value = {
      id: data.id,
      name: data.name,
      date: new Date(data.date).getTime(),
      description: data.description,
      location: data.location,
      events: data.eventIds || []
    };

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

  await Promise.all(eventFetches); 
  events.value = fetchedEvents; 
  console.log("All events loaded:", events.value);
}

watch(
  () => props.exchangeDay?.id,
  (newId, oldId) => {
    if (newId !== oldId && newId != null) {
      fetchExchangeDayDetails(newId);
    }
  }
);

onMounted(() => {
  if (props.exchangeDay?.id != null) {
    fetchExchangeDayDetails(props.exchangeDay.id);
  }
});
</script>

