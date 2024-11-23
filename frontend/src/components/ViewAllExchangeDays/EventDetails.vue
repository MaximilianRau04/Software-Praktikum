<template>
  <div class="event-details">
    <h2>{{ event.name }}</h2>
    
    <p><strong>Description:</strong> {{ event.description || 'No Description' }}</p>
    <p><strong>Event ID:</strong> {{ event.id }}</p>
    <p><strong>StartTime:</strong> {{ event.startTime || 'No Starttime' }}</p>
    <p><strong>Endtime:</strong> {{ event.endTime || 'No Endtime' }}</p>
    <p><strong>Room:</strong> {{ event.room || 'No Room' }}</p>
    
    <button @click="register(event.id)" class="register-button">Register</button>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from "vue";
import { Event } from '../../types/Event';
import '../../assets/event-details.css'; 

const props = defineProps<{ event: Event }>();

const register = (eventId: number) => {
  alert(`You have registered for event ID: ${eventId}`);
};
</script>
