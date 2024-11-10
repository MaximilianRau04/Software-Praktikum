<template>
  <div class="event-details">
    <h2>{{ event.name }}</h2>
    
    <!-- Event-Details mit engerem Abstand -->
    <p><strong>Description:</strong> {{ event.description || 'No Description' }}</p>
    <p><strong>Event ID:</strong> {{ event.id }}</p>
    <p><strong>StartTime:</strong> {{ event.startTime || 'No Starttime' }}</p>
    <p><strong>Endtime:</strong> {{ event.endTime || 'No Endtime' }}</p>
    <p><strong>Room:</strong> {{ event.room || 'No Room' }}</p>
    
    <!-- Registrierungs-Button in der unteren rechten Ecke -->
    <button @click="register(event.id)" class="register-button">Register</button>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from "vue";
import { Event } from '../types/Event';

const props = defineProps<{ event: Event }>();

const register = (eventId: number) => {
  alert(`You have registered for event ID: ${eventId}`);
};
</script>

<style scoped>
.event-details {
  background-color: #009EE2;
  border: 1px solid #ccd;
  border-radius: 8px;
  padding: 0.5rem;
  margin-bottom: 1.5rem;
  position: relative;
}

h2 {
  margin: 0 0 0.5rem 0;
}

p {
  margin: 0.2rem 0;
}

.register-button {
  background-color: black;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  font-size: 1.1rem;
  border-radius: 6px;
  cursor: pointer;
  position: absolute;
  bottom: 0.5rem; 
  right: 0.5rem; 
}

.register-button:hover {
  background-color: #005FA3;
}
</style>

