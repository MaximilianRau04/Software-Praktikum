<template>
  <div class="exchangeDayDetails">
    <h1>Meine Registrierten Events</h1>

    <div v-if="isLoading">
      <p>Events werden geladen...</p>
    </div>
    
    <div v-if="showQRCodeModal" class="qr-modal-overlay">
      <div class="qr-modal">
        <h2>QR-Code für Event Nr.{{ eventId }}</h2>
        <img :src="qrCodeUrl" alt="QR Code" />

        <p><a :href="qrCodeUrl">{{ qrCodeUrl }}</a></p>

        <a :href="qrCodeUrl" :download="'event-' + eventId + '-qr-code.png'">
          <button class="download-button">QR-Code herunterladen</button>
        </a>
        <button @click="closeQRCodeModal" class="close-modal-button">Schließen</button>
      </div>
    </div>

    <div v-else class="scrollableEvents">
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
              <button 
                class="openFeedback-button" @click="openFeedback(event.id)" v-if="organizerStatus[event.id]">
                Feedback anzeigen
              </button>

              <button 
                class="showQR-button" @click="openQRCode(event.id)" v-if="organizerStatus[event.id]">
                QR-Code anzeigen
              </button>

              <button 
                class="unregister-button" 
                @click="unregisterFromEvent(event.id)">
                Abmelden
              </button>
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
import { useRouter } from "vue-router";  
import Cookies from 'js-cookie';
import '../../assets/event-registrations.css';

const router = useRouter();
const registeredEvents = ref<Event[]>([]);
const organizerStatus = ref<{ [eventId: number]: boolean }>({});
const isLoading = ref(true);
const userId = Cookies.get("userId");
const showQRCodeModal = ref(false);
const qrCodeUrl = ref("");
const eventId = ref<number | null>(null);

if (!userId) {
  throw new Error("User ID not found in cookies.");
}

/**
 * Opens the QR code modal for a specific event.
 * @param {number} id - The ID of the event.
 */
const openQRCode = (id: number) => {
  eventId.value = id;
  qrCodeUrl.value = `${config.apiBaseUrl}/events/${id}/qr-code`; 
  showQRCodeModal.value = true;
};


/**
 * Closes the QR code modal.
 */
const closeQRCodeModal = () => {
  showQRCodeModal.value = false;
};

/**
 * Navigates to the feedback summary page for a specific event.
 * @param {number} eventId - The ID of the event.
 */
const openFeedback = (eventId: number) => {
  router.push({ name: 'feedbackSummary', params: { eventId: eventId.toString() } });
};

/**
 * Unregisters the user from a specific event after confirmation.
 * @param {number} eventId - The ID of the event.
 */
const unregisterFromEvent = async (eventId: number) => {
  if (!confirm('Are you sure you want to unregister from this event?')) {
    return;
  }
  try {
    const response = await fetch(`${config.apiBaseUrl}/users/${userId}/eventRegistration?eventId=${eventId}`, {
      method: "DELETE",
    });

    if (response.status === 404) {
      alert("Registration not found.");
      throw new Error(response.statusText);
    }

    const event = registeredEvents.value.find(event => event.id === eventId);
    registeredEvents.value = registeredEvents.value.filter(event => event.id !== eventId);
    alert(`You have successfully unregistered from ${event?.name}.`);
  } catch (error) {
    console.error("Error unregistering from event:", error);
    alert('Unregistering failed. Please try again.');
  }
};

/**
 * Checks if the current user is the organizer of a specific event.
 * @param {number} eventId - The ID of the event.
 * @returns {Promise<boolean>} - True if the user is the organizer, otherwise false.
 */
const isOrganizer = async (eventId: number): Promise<boolean> => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/events/${eventId}/organizer`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      console.error("Failed to fetch event organizer.");
      return false;
    }

    const data = await response.json();
    return data.id === parseInt(userId, 10);
  } catch (error) {
    console.error("Error checking if user is organizer:", error);
    return false;
  }
};

/**
 * Fetches the organizer statuses for all registered events in parallel.
 * @param {Event[]} events - An array of registered events.
 */
const fetchOrganizerStatuses = async (events: Event[]) => {
  const statuses = await Promise.all(
    events.map(event => isOrganizer(event.id).then(isOrg => ({ eventId: event.id, isOrg })))
  );

  statuses.forEach(({ eventId, isOrg }) => {
    organizerStatus.value[eventId] = isOrg;
  });
};

/**
 * Fetches all events the user is registered for and checks organizer status for each.
 */
const fetchRegisteredEvents = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/users/${userId}/registeredEvents`);
    if (!response.ok) throw new Error("Failed to fetch participations.");
    const responseData: Event[] = await response.json();

    registeredEvents.value = responseData;

    await fetchOrganizerStatuses(responseData);
  } catch (error) {
    console.error("Error fetching registered events:", error);
    registeredEvents.value = [];
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchRegisteredEvents();
});
</script>