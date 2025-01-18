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
        <p>
          <a :href="qrCodeUrl">{{ qrCodeUrl }}</a>
        </p>
        <a :href="qrCodeUrl" :download="'event-' + eventId + '-qr-code.png'">
          <button class="download-button">QR-Code herunterladen</button>
        </a>
        <button @click="closeQRCodeModal" class="close-modal-button">
          Schließen
        </button>
      </div>
    </div>

    <div v-else class="scrollableEvents">
      <div v-if="registeredEvents.length > 0">
        <ul>
          <li
            v-for="event in registeredEvents"
            :key="event.id"
            class="event-item"
            @click="updateEvent(event.id)"
          >
            <div class="event-details">
              <h3>{{ event.name }}</h3>
              <p>
                <strong>Beschreibung: </strong>
                {{ event.description || "Keine Beschreibung verfügbar." }}
              </p>
              <p><strong>Event ID:</strong> {{ event.id }}</p>
              <p><strong>Datum:</strong> {{ formatDate(event.date) }}</p>
              <p><strong>Startzeit: </strong> {{ event.startTime }}</p>
              <p><strong>Endzeit: </strong>{{ event.endTime }}</p>
              <p><strong>Raum: </strong> {{ event.room }}</p>

              <button
                class="openFeedback-button button"
                @click.stop="openFeedback(event.id)"
                v-if="organizerStatus[event.id]"
              >
                Feedback anzeigen
              </button>

              <button
                class="showQR-button button"
                @click.stop="openQRCode(event.id)"
                v-if="organizerStatus[event.id]"
              >
                QR-Code anzeigen
              </button>

              <button
                class="forum-button button"
                @click.stop="openForum(event.id)"
              >
                Diskussionsforum anzeigen
              </button>

              <button
                class="unregister-button button"
                @click.stop="unregisterFromEvent(event.id)"
              >
                Abmelden
              </button>

              <button
                class="delete-button button"
                @click.stop="deleteEvent(event.id)"
                v-if="organizerStatus[event.id]"
              >
                Löschen
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
import { Event } from "@/types/Event";
import config from "@/config";
import { useRouter } from "vue-router";
import Cookies from "js-cookie";

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
  router.push({
    name: "feedbackSummary",
    params: { eventId: eventId.toString() },
  });
};

/**
 * Navigates to the feedback summary page for a specific event.
 * @param {number} eventId - The ID of the event.
 */
const openForum = (eventId: number) => {
  router.push({ name: "forum", params: { eventId: eventId.toString() } });
};

/**
 * Unregisters the user from a specific event after confirmation.
 * @param {number} eventId - The ID of the event.
 */
const unregisterFromEvent = async (eventId: number) => {
  if (!confirm("Are you sure you want to unregister from this event?")) {
    return;
  }
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/users/${userId}/eventRegistration?eventId=${eventId}`,
      {
        method: "DELETE",
      }
    );

    if (response.status === 404) {
      alert("Registration not found.");
      throw new Error(response.statusText);
    }

    const event = registeredEvents.value.find((event) => event.id === eventId);
    registeredEvents.value = registeredEvents.value.filter(
      (event) => event.id !== eventId
    );
    alert(`You have successfully unregistered from ${event?.name}.`);
  } catch (error) {
    console.error("Error unregistering from event:", error);
    alert("Unregistering failed. Please try again.");
  }
};

/**
 * Checks if the current user is the organizer of a specific event.
 * @param {number} eventId - The ID of the event.
 * @returns {Promise<boolean>} - True if the user is the organizer, otherwise false.
 */
const isOrganizer = async (eventId: number): Promise<boolean> => {
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/events/${eventId}/organizer`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

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
    events.map((event) =>
      isOrganizer(event.id).then((isOrg) => ({ eventId: event.id, isOrg }))
    )
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
    const response = await fetch(
      `${config.apiBaseUrl}/users/${userId}/registeredEvents`
    );
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

/**
 * Deletes an event after confirmation.
 * @param {number} eventId - The ID of the event.
 */
const deleteEvent = async (eventId: number) => {
  if (!confirm("Are you sure you want to delete this event?")) {
    return;
  }
  try {
    const response = await fetch(`${config.apiBaseUrl}/events/${eventId}`, {
      method: "DELETE",
    });

    if (response.status === 404) {
      alert("Event not found.");
      throw new Error(response.statusText);
    }

    registeredEvents.value = registeredEvents.value.filter(
      (event) => event.id !== eventId
    );
    alert(`Event with ID ${eventId} has been successfully deleted.`);
  } catch (error) {
    console.error("Error deleting event:", error);
    alert("Deleting event failed. Please try again.");
  }
};

/**
 * Updates the event details page with the selected event.
 * @param {number} eventId - The ID of the event.
 */
const updateEvent = (eventId: number) => {
  if (organizerStatus.value[eventId]) {
    router.push({
      name: "updateEvent",
      params: { eventId: eventId.toString() },
    });
  }
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

.button {
  background-color: black;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  font-size: 0.85rem;
  border-radius: 6px;
  cursor: pointer;
  position: absolute;
  bottom: 10px;
  transition:
    background-color 0.3s ease,
    transform 0.3s ease;
}

.button:hover {
  background-color: #005fa3;
}

.button:active {
  background-color: #000000;
  transform: scale(0.98);
}

.unregister-button {
  right: 10px;
}

.showQR-button {
  right: 10px;
  bottom: 30%;
}

.openFeedback-button {
  right: 10px;
  bottom: 55%;
}

.forum-button {
  right: 10px;
  bottom: 80%;
}

.delete-button {
  right: 100px;
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

.qr-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
}

.qr-modal {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  width: 300px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.qr-modal img {
  max-width: 100%;
  margin-top: 10px;
}

.close-modal-button {
  background-color: #009ee2;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 20px;
}

.close-modal-button:hover {
  background-color: #007db8;
}

.download-button {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border-radius: 6px;
  margin-top: 15px;
  text-decoration: none;
}

.download-button:hover {
  background-color: #218838;
}

.download-button:active {
  background-color: #1e7e34;
  transform: scale(0.98);
}
</style>