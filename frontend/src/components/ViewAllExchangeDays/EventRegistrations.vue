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

const registeredEvents = ref<Event[]>([]);

const apiUrl = 'http://localhost:8080/api/users';
const userId = localStorage.getItem('userId');

if (!userId) {
  throw new Error("User ID not found in local storage.");
}

/**
 * Fetches the registered events for the current user.
 */
const fetchRegisteredEvents = async () => {
  try {
    console.log('Fetching user data...');

    // fetch user data
    const response = await fetch(`${apiUrl}/${userId}/participations`);
    if (!response.ok) throw new Error("Failed to fetch participations.");

    const responseData: EventParticipation[] = await response.json();
    console.log('User data retrieved:', responseData);


    // Set the registered events to the user data
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
  // put request to remove event from user
  try {
    if (!userId) throw new Error("User ID not found in local storage.");

    const response = await fetch(`${apiUrl}/${userId}/eventRegistration?eventId=${eventId}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });
    console.log(response.status);
    console.log(`${apiUrl}/${userId}/eventRegistration?eventId=${eventId}`);
    if (response.status === 404) {
      alert("Registrierung nicht vorhanden.");
      throw new Error(response.statusText);
    }

    const event = registeredEvents.value.find(event => event.id === eventId);

    // Update the registered events after unregistration
    registeredEvents.value = registeredEvents.value.filter(event => event.id !== eventId);
    alert(`Sie wurden erfolgreich von ${event.name} abgemeldet.`)
  } catch (error) {
    console.error("Error unregistering from event:", error);
    alert('Die Abmeldung ist fehlgeschlagen. Bitte versuche es erneut.');
  };
}

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

.unregister-button {
  background-color: black;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  font-size: 1.1rem;
  border-radius: 6px;
  cursor: pointer;
  position: absolute;
  bottom: 10px;
  right: 10px;
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.unregister-button:hover {
  background-color: #005FA3;
}

.unregister-button:active {
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