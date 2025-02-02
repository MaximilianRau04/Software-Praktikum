<template>
  <div class="trainer-events">
    <div class="events-column past-events">
      <h3>Gehaltene Events</h3>
      <div class="events-grid">
        <EventCard
          v-for="(event, index) in pastEvents"
          :key="'past-' + index"
          :event="event"
          :showRating="true"
          class="event-card"
        />
      </div>
    </div>

    <div class="events-column future-events">
      <h3>Geplante Events</h3>
      <div class="events-grid">
        <EventCard
          v-for="(event, index) in futureEvents"
          :key="'future-' + index"
          :event="event"
          :showRating="false"
          :showRegisterButton="true"
          class="event-card"
        />
      </div>
    </div>
  </div>
</template>

<script>
import EventCard from "@/components/profile/EventCard.vue";

export default {
  name: "TrainerEvents",
  components: {
    EventCard,
  },
  props: {
    pastEvents: {
      type: Array,
      required: true,
    },
    futureEvents: {
      type: Array,
      required: true,
    },
  },
};
</script>

<style scoped>
.trainer-events {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.events-column {
  flex: 1;
}

.events-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
  margin-top: 1rem;
}

.past-events .event-card {
  position: relative;
}

h3 {
  margin-top: 2rem;
  font-size: 1.25rem;
  color: #333;
}

.event-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s;
}

.event-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.event-card .date-time {
  display: flex;
  justify-content: space-between;
}

.event-card .date {
  font-weight: bold;
}

.event-card .time {
  color: #555;
}

.event-card .rating {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: flex;
  gap: 2px;
}

.event-card .rating .star {
  width: 20px;
  height: 20px;
  background-color: #ffd700;
}

.event-card .register-btn {
  padding: 0.75rem 1.25rem;
  background-color: #009ee2;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.event-card .register-btn:hover {
  background-color: #01172f;
}

.event-card .register-btn:focus {
  outline: none;
}
</style>
