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

    <button @click="register(event.id)" class="register-button" :disabled="isAlreadyRegistered">
      {{ isAlreadyRegistered ? "Bereits registriert" : "Registrieren" }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import '../../assets/event-details.css';
import { Event } from '../../types/Event';
import { defineProps } from 'vue';

const props = defineProps<{ event: Event }>();
const isAlreadyRegistered = ref(false);
const apiUrl = 'http://localhost:8080/api/users';
const userId = localStorage.getItem('userId');


/**
 * Registers the current user for the event.
 * @param {number} eventId - The ID of the event to register for.
 */
const register = async (eventId: number) => {
  try {
    if (!userId) throw new Error("User ID not found in local storage.");

    const response = await fetch(`${apiUrl}/${userId}/eventRegistration?eventId=${eventId}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    });
    if (response.status === 404) {
      alert("Registrierung fehlgeschlagen. Bitte erneut versuchen.");
      throw new Error(response.statusText);
    }
    if (response.status === 409){
      alert("Sie sind bereits registriert.");
      throw new Error(response.statusText);
    } 

    alert(`Sie wurden erfolgreich zu ${props.event.name} angemeldet.`)
  } catch (error) {
    console.error("Error registering for event:", error);
  }
};

const remove = async(eventId: number)=> {
  try{
    if (!userId) throw new Error("User ID not found in local storage.");

    const response = await fetch(`${apiUrl}/${userId}/eventRegistration?eventId=${eventId}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });
    if (response.status === 404) {
      alert("Registrierung nicht vorhanden.");
      throw new Error(response.statusText);
    }

    alert(`Sie wurden erfolgreich von ${props.event.name} abgemeldet.`)

  }catch(error){
    console.error("Error registering for event:", error);
  }
}

onMounted(() => {

});
</script>
