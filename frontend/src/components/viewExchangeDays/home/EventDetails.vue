<template>
  <div class="event-details">
    <h2>{{ event.name }}</h2>
    <p>
      <strong>Beschreibung:</strong> {{ event.description || "No Description" }}
    </p>
    <p><strong>Event ID:</strong> {{ event.id }}</p>
    <p><strong>Datum:</strong> {{ formatDate(event.date) }}</p>
    <p><strong>Startzeit:</strong> {{ event.startTime || "No Starttime" }}</p>
    <p><strong>Endzeit:</strong> {{ event.endTime || "No Endtime" }}</p>
    <p><strong>Raum:</strong> {{ event.room.name || "No Room" }}</p>
    <button
      @click="goToEvent(event.id)"
      class="register-button"
    >Details
    </button>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import "@/assets/event-details.css";
import { Event } from "@/types/Event";
import { defineProps } from "vue";

const router = useRouter();
const props = defineProps<{ event: Event }>();
const isPastEvent = ref(false);
const goToEvent = (eventId) => {
  router.push({ name: "EventPage", params: { eventId } });
};



/**
 * Formats a timestamp into a human-readable date string.
 *
 * @param {string} timestamp - The date in milliseconds.
 * @returns {string} - The formatted date string in 'DD.MM.YYYY' format.
 */
function formatDate(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE");
}

/**
 * Checks if the event is in the past.
 */
function checkIfPastEvent() {
  const eventDate = new Date(props.event.date);
  const now = new Date();
  const currentTime = now.getTime();
  isPastEvent.value = eventDate <= now && new Date(`${props.event.date}T${props.event.startTime}`).getTime() <= currentTime;
}

onMounted(() => {
  checkIfPastEvent();
  console.log(props.event);
});
</script>
