<!--
  This Vue component is responsible for displaying the details of a specific event.
  It is used to provide detailed information about an event
-->
<template>
  <div class="event-details">
    <h2>{{ event.name }}</h2>
    <p><strong>Beschreibung:</strong> {{ event.description || 'No Description' }}</p>
    <p><strong>Event ID:</strong> {{ event.id }}</p>
    <p><strong>Startzeit:</strong> {{ event.startTime || 'No Starttime' }}</p>
    <p><strong>Endzeit:</strong> {{ event.endTime || 'No Endtime' }}</p>
    <p><strong>Raum:</strong> {{ event.room || 'No Room' }}</p>

    <button 
      @click="register(event.id)" 
      class="register-button" 
      :disabled="isAlreadyRegistered">
      {{ isAlreadyRegistered ? "Bereits registriert" : "Registrieren" }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Event } from '../../types/Event';
import { defineProps } from 'vue';
import '../../assets/event-details.css';

// Define props
const props = defineProps<{ event: Event }>();
const isAlreadyRegistered = ref(false);
const apiUrl = 'http://localhost:8080/api/users';
const userId = localStorage.getItem('userId');
const user = ref<any>(null); 

/**
 * Checks the registration status of the current user for the event.
 */
const checkRegistrationStatus = async () => {
  if (!userId) {
    console.error("User ID not found in local storage.");
    return;
  }

  try {
    const response = await fetch(`${apiUrl}/${userId}`);
    if (!response.ok) {
      throw new Error("Failed to fetch user data.");
    }
    
    user.value = await response.json();
    console.log("User data:", user.value); 
    
    if (user.value && Array.isArray(user.value.registeredEvents)) {

      const registeredEventIds = user.value.registeredEvents.map(event => event.id);

      isAlreadyRegistered.value = registeredEventIds.includes(props.event.id);
    } else {
      console.warn("User data does not contain registeredEvents.");
      isAlreadyRegistered.value = false;
    }
  } catch (error) {
    console.error("Error checking registration status:", error);
    isAlreadyRegistered.value = false;
  }
};

/**
 * Registers the current user for the event.
 * @param {number} eventId - The ID of the event to register for.
 */
const register = async (eventId: number) => {
  try {
    if (!userId) throw new Error("User ID not found in local storage.");
    const response = await fetch(`${apiUrl}/${userId}/eventRegistration?eventId=${eventId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
    });
    if (!response.ok) throw new Error("Failed to fetch user data.");

    const userData = await response.json();
    const registeredEventIds = userData.registeredEventIds || [];

    if (registeredEventIds.includes(eventId)) {
      alert("Du bist bereits für dieses Event registriert.");
      return;
    }

    const putResponse = await fetch(`${apiUrl}/${userId}/eventRemoval?eventId=${eventId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        userId,
        eventId,
      }),
    });

    if (putResponse.ok) {
      alert(`Erfolgreich registriert für Event: ${props.event.name}`);
      isAlreadyRegistered.value = true;
    } else {
      throw new Error("Registrierung fehlgeschlagen.");
    }
  } catch (error) {
    console.error("Error registering for event:", error);
    alert("Registrierung fehlgeschlagen. Bitte erneut versuchen.");
  }
};


onMounted(() => {
  checkRegistrationStatus();
});
</script>
