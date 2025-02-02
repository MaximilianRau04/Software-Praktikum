<template>
  <div class="event-card">
    <div class="heading">
      <h4 class="event-title">{{ event.name }}</h4>
    </div>
    <div class="date-time">
      <span class="date">
        {{ new Date(event.date).toLocaleDateString("de-DE", {
          weekday: "long", day: "numeric", month: "long", year:
            "numeric" }) }}
      </span>
      <span class="time">
        {{
          new Date(event.date + "T" + event.startTime).toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" })
        }}
        -
        {{
          new Date(event.date + "T" + event.endTime).toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" })
        }}
      </span>
    </div>
    <div class="event-description">
      <p>{{ event.description }}</p>
    </div>
    <button v-if="showRegisterButton" class="details-btn" @click="showDetails">Details anzeigen</button>
    <div v-if="showRating" class="rating">
      <div class="star" v-for="n in 5" :key="n" :class="{ filled: n <= Math.round(event.averageRating) }">★</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "EventCard",
  props: {
    event: {
      type: Object,
      required: true,
    },
    showRating: {
      type: Boolean,
      default: false,
    },
    showRegisterButton: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    showDetails() {
      this.$router.push({ name: "EventPage", params: { eventId: this.event.id } });
    },
  },
};
</script>

<style scoped>
.event-card {
  position: relative;
  padding: 0rem;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

h4{
  margin:0;
}

.event-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.event-tag {
  background-color: #e0e0e0;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.event-rating {
  color: #ffc107;
  font-weight: bold;
}

.register-button {
  background-color: #007bff;
  color: white;
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.register-button:hover {
  background-color: #0056b3;
}

.heading{
  display: flex;
  flex-direction: column;
  margin-top: 10px 0 0 0;
  padding: 0;
}

.event-title {
  max-width: 280px;
  font-size: 1.25rem;
  font-weight: bold;
  word-wrap: break-word;
  hyphens: auto;
}

hr {
  border: 1px solid #ddd;
  margin: 0.5rem 0;
}

.event-description {
  font-size: 1rem;
  color: #333;
}

.details-btn {
  padding: 0.75rem 1.25rem;
  background-color: #009ee2;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 1rem;
  transition: background-color 0.3s;
}

.details-btn:hover {
  background-color: #01172f;
}

.date-time {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #555;
}

.date-time .date {
  font-weight: bold;
}

.date-time .time {
  color: #777;
}

.rating {
  display: flex;
  gap: 2px;
  position: absolute;
  top: 1rem;
  right: 1rem;
}

.star {
  color: #ccc;
}

.star.filled {
  color: #ffd700;
}
</style>