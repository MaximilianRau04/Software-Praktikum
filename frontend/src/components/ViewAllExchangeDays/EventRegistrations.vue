<template>
  <div class="exchangeDayDetails">
    <h1>Meine Registrierten Events</h1>
    <div class="scrollableEvents">
      <div v-if="registeredEvents.length > 0">
        <ul>
          <li v-for="event in registeredEvents" :key="event.id" class="event-item">
            <div class="event-details">
              <h3>{{ event.name }}</h3>
              <p><strong>Beschreibung: </strong> {{ event.description || 'Keine Beschreibung verfügbar.' }}</p>
              <p><strong>Event ID:</strong> {{ event.id }}</p>
              <p><strong>Startzeit: </strong> {{ event.startTime }}</p>
              <p><strong>Endzeit: </strong>{{ event.endTime }}</p>
              <p><strong>Raum: </strong> {{ event.room }}</p>
              <button class="openFeedback-button" @click="openFeedback(event.id)" v-if="isOrganizer(event)">Feedback anzeigen</button>
              <button class="showQR-button" @click="openQRCode(event.id)" v-if="isOrganizer(event)">QR-Code anzeigen</button>
              <button class="unregister-button" @click="unregisterFromEvent(event.id)">Abmelden</button>
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
import { Event } from '../../types/Event';
import config from '../../config';
import '../../assets/exchange-day-details.css';
import '../../assets/event-details.css';
import { EventParticipation } from "@/types/EventParticipation";
import { Router, useRouter } from "vue-router";  
import Cookies from 'js-cookie';

const router = useRouter();

const registeredEvents = ref<Event[]>([]);

const userId = Cookies.get("userId");

if (!userId) {
  throw new Error("User ID not found in local storage.");
}

/**
 * Opens the QR code for the event in a new tab.
 * @param {number} eventId - The ID of the event to open the QR code for.
 */
const openQRCode = (eventId: number) => {
  const qrCodeUrl = `${config.apiBaseUrl}/events/${eventId}/qr-code`; 
  window.open(qrCodeUrl, '_blank');
};


/**
 * Opens the feedback summary for the event.
 * @param {number} eventId - The ID of the event to open the feedback for.
 */
const openFeedback = (eventId: number) => {
  router.push({ name: 'feedbackSummary', params: { eventId: eventId.toString() } });
};

/**
 * Unregisters the current user from an event.
 * @param {number} eventId - The ID of the event to unregister from.
 */
const unregisterFromEvent = async (eventId: number) => {
  if (!confirm('Möchtest du dich wirklich von diesem Event abmelden?')) {
    return;
  }
  try {
    if (!userId) throw new Error("User ID not found in local storage.");

    const response = await fetch(`${config.apiBaseUrl}/users/${userId}/eventRegistration?eventId=${eventId}`, {
      method: "DELETE",
    });
    console.log(response.status);

    if (response.status === 404) {
      alert("Registrierung nicht vorhanden.");
      throw new Error(response.statusText);
    }

    const event = registeredEvents.value.find(event => event.id === eventId);

    registeredEvents.value = registeredEvents.value.filter(event => event.id !== eventId);
    alert(`Sie wurden erfolgreich von ${event.name} abgemeldet.`)
  } catch (error) {
    console.error("Error unregistering from event:", error);
    alert('Die Abmeldung ist fehlgeschlagen. Bitte versuche es erneut.');
  };
}

/**
 * Fetches the registered events for the current user.
 */
 const fetchRegisteredEvents = async () => {
  try {
    console.log('Fetching user data...');

    const response = await fetch(`${config.apiBaseUrl}/users/${userId}/registeredEvents`);
    if (!response.ok) throw new Error("Failed to fetch participations.");

    const responseData: Event[] = await response.json();
    console.log('User data retrieved:', responseData);

    registeredEvents.value = responseData;
    console.log('Registered Events loaded:', registeredEvents.value);

  } catch (error) {
    console.error("Error fetching registered events:", error);
    registeredEvents.value = [];
  }
}

/**
 * Fetches the organizer of a specific event.
 * @param {number} eventId - The ID of the event to get the organizer for.
 * @returns {Promise<string>} - The ID of the event's organizer.
 */
const fetchEventOrganizer = async (eventId: number): Promise<string> => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/events/${eventId}/organizer`);
    if (!response.ok) throw new Error("Failed to fetch event organizer.");

    const data = await response.json();
    return data.id;
  } catch (error) {
    console.error("Error fetching event organizer:", error);
    return "";
  }
};

/**
 * Checks if the current user is the organizer of the event.
 * @param {Event} event - The event object to check against.
 * @returns {boolean} - True if the user is the organizer, otherwise false.
 */
const isOrganizer = async (event: Event): Promise<boolean> => {
  const organizerId = await fetchEventOrganizer(event.id);
  return organizerId === userId;
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
  background-color: #009EE2;
  border: 1px solid #ccd;
  border-radius: 8px;
  padding: 15px;
  margin: 5px 0;
  position: relative;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  list-style-type: none;
}

.event-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}


.event-details {
  padding: 0.5rem;
  font-size: 16px;
  color: #fff;
}


.event-details p {
  font-size: 18px;
}

h2 {
  font-size: 20px;
  color: #ffffff;
  margin-bottom: 0.5rem;
}

p {
  margin: 0.2rem 0;
  color: #000000;
  font-size: 14px;
}

.unregister-button,
.showQR-button,
.openFeedback-button {
  background-color: black;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  font-size: 0.85rem;
  border-radius: 6px;
  cursor: pointer;
  position: absolute;
  bottom: 10px;
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.unregister-button {
  right: 10px;
}

.showQR-button {
  left: 8px;
}

.openFeedback-button {
  left: 10px;
  bottom: 60px;
}


.unregister-button:hover,
.showQR-button:hover,
.openFeedback-button:hover {
  background-color: #005FA3;
}

.unregister-button:active,
.showQR-button:active,
.openFeedback-button:active {
  background-color: #000000;
  transform: scale(0.98);
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