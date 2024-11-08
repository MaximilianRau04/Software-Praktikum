<template>
  <div>
    <h2>Exchange Days</h2>
    <ul>
      <li v-for="exchangeDay in exchangeDays" :key="exchangeDay.id" @click="loadEvents(exchangeDay.id)">
        {{ exchangeDay.date }} - {{ exchangeDay.description }}
      </li>
    </ul>

    <div v-if="events.length">
      <h3>Events for Selected Exchange Day</h3>
      <ul>
        <li v-for="event in events" :key="event.id">
          {{ event.name }} - {{ event.startTime }} - {{ event.location }} - {{ event.trainer }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import exchangeDayService from '@/services/exchangeDayService';

export default {
  data() {
    return {
      exchangeDays: [],
      events: []
    };
  },
  methods: {
    // Alle ExchangeDays abrufen
    async fetchExchangeDays() {
      try {
        const response = await exchangeDayService.getAllExchangeDays();
        this.exchangeDays = response.data;
      } catch (error) {
        console.error("Fehler beim Abrufen der ExchangeDays:", error);
      }
    },

    // Events für einen bestimmten ExchangeDay abrufen
    async loadEvents(exchangeDayId) {
      try {
        const response = await exchangeDayService.getEventsByExchangeDayId(exchangeDayId);
        this.events = response.data;
      } catch (error) {
        console.error("Fehler beim Abrufen der Events:", error);
      }
    }
  },
  mounted() {
    // Beim Laden der Komponente alle ExchangeDays abrufen
    this.fetchExchangeDays();
  }
};
</script>

<style scoped>
/* Optionale Stile für die Komponente */
</style>
