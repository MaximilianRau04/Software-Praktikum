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
              <button v-if="isOrganizer(event.organizer)" class="showQR-button" @click="openQRCode(event.id)">QR-Code anzeigen</button>
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

const router = useRouter();

const registeredEvents = ref<Event[]>([]);

const userId = localStorage.getItem('userId');

if (!userId) {
  throw new Error("User ID not found in local storage.");
}

const openQRCode = (eventId: number) => {
  const qrCodeUrl = `${config.apiBaseUrl}/events/${eventId}/qr-code`; 
  window.open(qrCodeUrl, '_blank');
};

/**
 * Fetches the registered events for the current user.
 */
const fetchRegisteredEvents = async () => {
  try {
    console.log('Fetching user data...');

    const response = await fetch(`${config.apiBaseUrl}/users/${userId}/participations`);
    if (!response.ok) throw new Error("Failed to fetch participations.");

    const responseData: EventParticipation[] = await response.json();
    console.log('User data retrieved:', responseData);

    registeredEvents.value = responseData.map(participation => participation.event);
    console.log('Registered Events loaded:', registeredEvents.value);

  } catch (error) {
    console.error("Error fetching registered events:", error);
    registeredEvents.value = [];
  }
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
      headers: {
      },
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
 * Navigates to the QR code feedback page for the given event.
 * @param {number} eventId - The ID of the event to show the QR code feedback for.
 */
 const showQRCode = (eventId: number) => {
  const event = registeredEvents.value.find((event) => event.id === eventId);
  if (!event) {
    alert("Event nicht gefunden.");
    return;
  }

  router.push(`/events/${eventId}/qrCode`);
};

/**
 * Checks if the current user is the organizer of the event.
 * @param {String} organizerId - The ID of the event organizer.
 * @returns {boolean} - True if the user is the organizer, otherwise false.
 */
const isOrganizer = (organizerId: String): boolean => {
  return organizerId == userId!;
};

onMounted(() => {
  fetchRegisteredEvents();
});
</script>

<style scoped>
.exchangeDayDetails {
  display: flex;
  flex-direction: column;
  padding: 15px;
  background-color: #EAEAEA;
  border: 1px solid black;
  border-radius: 8px;
  margin: 20px auto;
  font-family: Arial, sans-serif;
  box-sizing: border-box;
  height: 85vh;
  overflow: hidden;
}

h1 {
  margin-top: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.scrollableEvents {
  overflow-y: auto;
  flex-grow: 1;
  padding-right: 10px;
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
.showQR-button {
  background-color: black;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  font-size: 1.1rem;
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
  left: 10px;
}

.unregister-button:hover,
.showQR-button:hover {
  background-color: #005FA3;
}

.unregister-button:active,
.showQR-button:active {
  background-color: #000000;
  transform: scale(0.98);
}

.event-item h3 {
  font-size: 18px;
  color: black;
  margin: 0 0 8px;
}

.event-item p {
  font-size: 14px;
  color: #000000;
  margin: 4px 0;
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