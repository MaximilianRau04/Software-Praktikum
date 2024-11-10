<template>
  <div class="event-details">
    <h2>{{ event.name }}</h2>
    <p><strong>Description:</strong> {{ event.description || 'No Description'}}</p>
    <p>Event ID: {{ event.id }}</p>
    <p>StartTime: {{ event.startTime || 'No Starttime'}}</p>
    <p>Endtime: {{ event.endTime || 'No Endtime'}}</p>
    <p>Room: {{ event.room || 'No Room' }}</p>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from "vue";
import { Event } from '../types/Event';

const props = defineProps<{ event: Event }>();

</script>

<style scoped>
.event-details {
  background-color: #009EE2;
  border: 1px solid #ccd;
  border-radius: 8px;
  padding: 0.5rem;
  margin-bottom: 0.5rem;
}
</style>
