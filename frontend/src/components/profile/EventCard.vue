<template>
  <div class="event-card">
    <div class="date-time">
      <span class="date">{{
        new Date(event.date).toLocaleDateString("de-DE")
      }}</span>
      <span class="time">
        {{
          new Date(event.date + "T" + event.startTime).toLocaleTimeString(
            "de-DE",
            {
              hour: "2-digit",
              minute: "2-digit",
            },
          )
        }}
        -
        {{
          new Date(event.date + "T" + event.endTime).toLocaleTimeString(
            "de-DE",
            {
              hour: "2-digit",
              minute: "2-digit",
            },
          )
        }}
      </span>
    </div>
    <h4>{{ event.name }}</h4>
    <p>{{ event.description }}</p>
    <div class="rating">
      <div
        class="star"
        v-for="n in Math.round(event.averageRating)"
        :key="n"
      ></div>
    </div>
    <button v-if="showRegisterButton" class="register-btn">Register</button>
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
    formatDate(date) {
      const options = { year: "numeric", month: "long", day: "numeric" };
      return new Date(date).toLocaleDateString(undefined, options);
    },
  },
};
</script>

<style scoped>
.event-card {
  padding: 1rem;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
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
</style>
