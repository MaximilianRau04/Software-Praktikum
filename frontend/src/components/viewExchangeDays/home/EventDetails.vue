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
    <p><strong>Raum:</strong> {{ event.room || "No Room" }}</p>

    <button
      v-if="!isPastEvent"
      @click="register(event.id)"
      class="register-button"
      :disabled="isAlreadyRegistered"
    >
      {{ isAlreadyRegistered ? "Bereits registriert" : "Registrieren" }}
    </button>

    <p v-else class="past-event-message">
      Eine Anmeldung ist nicht mehr möglich...
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import "@/assets/event-details.css";
import { Event } from "@/types/Event";
import { defineProps } from "vue";
import config from "@/config";
import Cookies from "js-cookie";
import { showToast, Toast } from "@/types/toasts";
import { faXmark, faCheck } from "@fortawesome/free-solid-svg-icons";

const props = defineProps<{ event: Event }>();
const isAlreadyRegistered = ref(false);
const isPastEvent = ref(false);
const userId = Cookies.get("userId");

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

/**
 * Checks if the user is already registered for the event.
 */
const checkRegistrationStatus = async () => {
  if (!userId) {
    return;
  }
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/users/${userId}/registeredEvents`,
    );
    if (!response.ok) throw new Error("Failed to fetch user data.");

    const registeredEvents = await response.json();

    isAlreadyRegistered.value = registeredEvents.some(
      (event: { id: number }) => event.id === props.event.id,
    );
  } catch (error) {
    showToast(
      new Toast("Error", `Fehler bei der Registreirung`, "error", faXmark, 10),
    );
    isAlreadyRegistered.value = false;
  }
};

/**
 * Registers the user for the event.
 * @param eventId The ID of the event to register for.
 */
const register = async (eventId: number) => {
  try {
    if (!userId) {
      showToast(
        new Toast(
          "Warning",
          `Bitte melden sie sich zuvor an.`,
          "warning",
          faXmark,
          10,
        ),
      );
      return;
    }

    const response = await fetch(
      `${config.apiBaseUrl}/users/${userId}/eventRegistration?eventId=${eventId}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      },
    );

    if (response.status === 404) {
      showToast(
        new Toast(
          "Error",
          `Registrierung fehlgeschlagen. Bitte versuchen sie es erneut`,
          "error",
          faXmark,
          10,
        ),
      );
    } else if (response.status === 409) {
      showToast(
        new Toast("Error", `Sie sin bereits registriert`, "error", faXmark, 10),
      );
    } else if (response.ok) {
      showToast(
        new Toast(
          "Success",
          `Sie wurden erfolgreich zu ${props.event.name} angemeldet!`,
          "success",
          faCheck,
          5,
        ),
      );
      isAlreadyRegistered.value = true;
    }
  } catch (error) {
    showToast(
      new Toast(
        "Error",
        `Fehler Fetchen der exchange days: ${error.message}`,
        "error",
        faXmark,
        10,
      ),
    );
  }
};

onMounted(() => {
  checkIfPastEvent();
  checkRegistrationStatus();
});
</script>
