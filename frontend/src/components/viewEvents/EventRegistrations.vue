<template>
  <div class="exchangeDayDetails">
    <h1>Meine Registrierten Events</h1>

    <div v-if="isLoading">
      <p>Events werden geladen...</p>
    </div>

    <div v-else class="scrollableEvents">
      <div v-if="registeredEvents.length > 0">
        <ul>
          <li
            v-for="event in registeredEvents"
            :key="event.id"
            class="event-item"
            @click="goToEventPage(event.id)"
          >
            <div class="event-details">
              <h3>{{ event.name }}</h3>
              <p>{{ formatDate(event.date) }}</p>
            </div>
          </li>
        </ul>
      </div>

      <div v-else>
        <p>Keine registrierten Events gefunden.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Event } from "@/types/Event";
import config from "@/config";
import { useRouter } from "vue-router";
import Cookies from "js-cookie";

const router = useRouter();
const registeredEvents = ref<Event[]>([]);
const organizerStatus = ref<{ [eventId: number]: boolean }>({});
const isLoading = ref(true);
const userId = Cookies.get("userId");

if (!userId) {
  throw new Error("User ID not found in cookies.");
}

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
 * Fetches all events the user is registered for and checks organizer status for each.
 */
const fetchRegisteredEvents = async () => {
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/users/${userId}/registeredEvents`,
    );
    if (!response.ok) throw new Error("Failed to fetch participations.");
    const responseData: Event[] = await response.json();

    registeredEvents.value = responseData;
  } catch (error) {
    console.error("Error fetching registered events:", error);
    registeredEvents.value = [];
  } finally {
    isLoading.value = false;
  }
};

/**
 * Updates the event details page with the selected event.
 * @param {number} eventId - The ID of the event.
 */
const goToEventPage = (eventId: number) => {
  router.push({
    name: "EventPage",
    params: { eventId: eventId.toString() },
  });
};

onMounted(() => {
  fetchRegisteredEvents();
});
</script>

<style scoped>
h1 {
  margin-top: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.event-item {
  background-color: #009ee2;
  border: 1px solid #ccd;
  border-radius: 8px;
  padding: 15px;
  margin: 5px 0;
  position: relative;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition:
    box-shadow 0.3s ease,
    transform 0.3s ease;
  list-style-type: none;
}

.event-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.event-details {
  font-size: 16px;
  color: #fff;
  white-space: normal;
  overflow-wrap: break-word;
}

.event-details p {
  font-size: 18px;
  max-width: 80%;
  white-space: normal;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

p {
  color: #000;
  font-size: 14px;
}

h2 {
  font-size: 20px;
  color: #000000;
  margin-top: 20px;
  margin-bottom: 5px;
  border-bottom: 2px solid #ddd;
  padding-bottom: 5px;
}

.empty-state {
  text-align: center;
  color: #888;
  font-size: 16px;
  margin-top: 20px;
}
</style>
