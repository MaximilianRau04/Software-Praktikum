<template>
  <div class="event-details">
    <h2>{{ event.name }}</h2>
    <p>
      <strong>Beschreibung:</strong> {{ event.description || "No Description" }}
    </p>
    <p><strong>Event ID:</strong> {{ event.id }}</p>
    <p><strong>Startzeit:</strong> {{ event.startTime || "No Starttime" }}</p>
    <p><strong>Endzeit:</strong> {{ event.endTime || "No Endtime" }}</p>
    <p><strong>Raum:</strong> {{ event.room || "No Room" }}</p>

    <button
      @click="register(event.id)"
      class="register-button"
      :disabled="isAlreadyRegistered"
    >
      {{ isAlreadyRegistered ? "Bereits registriert" : "Registrieren" }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import "../../assets/event-details.css";
import { Event } from "../../types/Event";
import { defineProps } from "vue";
import config from "../../config";
import Cookies from "js-cookie";

const props = defineProps<{ event: Event }>();
const isAlreadyRegistered = ref(false);
const userId = Cookies.get("userId");

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
    console.error("Error checking registration status:", error);
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
      window.alert("Bitte melden sie sich zuvor an.");
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
      alert("Registrierung fehlgeschlagen. Bitte erneut versuchen.");
    } else if (response.status === 409) {
      alert("Sie sind bereits registriert.");
    } else if (response.ok) {
      alert(`Sie wurden erfolgreich zu ${props.event.name} angemeldet.`);
      isAlreadyRegistered.value = true;
    }
  } catch (error) {
    console.error("Error registering for event:", error);
  }
};

onMounted(() => {
  checkRegistrationStatus();
});
</script>
