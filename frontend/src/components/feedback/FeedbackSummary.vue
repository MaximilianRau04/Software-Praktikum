<template>
  <div class="event-summary">
    <h1>Event Summary</h1>

    <div v-if="isLoading" class="loading">
      <p>Loading...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>Error: {{ error }}</p>
    </div>

    <div v-else>
      <!-- General Information -->
      <div class="general-info">
        <h2>General Information</h2>
        <table>
          <tr>
            <td><strong>Event ID:</strong></td>
            <td>{{ data.eventId || "undefined" }}</td>
          </tr>
          <tr>
            <td><strong>Event Name:</strong></td>
            <td>{{ data.eventName || "undefined" }}</td>
          </tr>
          <tr>
            <td><strong>Organizer:</strong></td>
            <td>{{ data.organizerName || "undefined" }}</td>
          </tr>
        </table>
      </div>

      <!-- Numerical Feedback -->
      <div class="numerical-feedback">
        <h2>Numerical Feedback</h2>
        <table v-if="numericalFeedbackCategories.length">
          <thead>
            <tr>
              <th>Category</th>
              <th>Average</th>
              <th>Median</th>
              <th>Response Count</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(feedback, category) in data.numericalFeedback"
              :key="category"
            >
              <td>{{ formatKey(category) }}</td>
              <td>{{ feedback.average }}</td>
              <td>{{ feedback.median }}</td>
              <td>{{ feedback.responseCount }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else style="text-align: center; color: #7f8c8d">
          No feedback data available.
        </p>
      </div>

      <!-- Common Words -->
      <div class="common-words">
        <h2>Common Words</h2>
        <ul>
          <li v-for="word in data.commonWords" :key="word">{{ word }}</li>
        </ul>
      </div>

      <!-- Comments -->
      <div class="comments">
        <h2>Comments</h2>
        <ul>
          <li v-for="(comment, index) in data.comments" :key="index">
            <p>
              <strong>Comment {{ index + 1 }}:</strong> {{ comment.comment }}
            </p>
            <p><strong>Sentiment:</strong> {{ comment.sentiment }}</p>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import config from "@/config";

export default {
  name: "EventSummary",
  data() {
    return {
      data: null,
      isLoading: true,
      error: null,
    };
  },
  computed: {
    eventId() {
      return this.$route.params.eventId;
    },
    numericalFeedbackCategories() {
      return this.data && this.data.numericalFeedback
        ? Object.keys(this.data.numericalFeedback)
        : [];
    },
  },
  methods: {
    /**
     * Fetches event summary data from the API
     */
    async fetchData() {
      try {
        const response = await fetch(
          `${config.apiBaseUrl}/events/${this.eventId}/summary`,
        );
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        this.data = await response.json();
      } catch (err) {
        this.error = err.message || "An unknown error occurred";
      } finally {
        this.isLoading = false;
      }
    },
    formatKey(key) {
      if (!key || typeof key !== "string") return "Unknown";
      return key
        .replace(/([A-Z])/g, " $1")
        .replace(/^./, (str) => str.toUpperCase());
    },
  },
  mounted() {
    this.fetchData();
  },
};
</script>

<style>
.event-summary {
  font-family: Arial, sans-serif;
  padding: 15px;
  margin: 0 auto;
  background: #f4f6f8;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  max-height: 80vh;
  min-height: 400px;
  overflow-y: auto;
}

h1,
h2 {
  color: #34495e;
  text-align: center;
}

.loading {
  text-align: center;
  font-size: 1.2em;
  color: #3498db;
}

.error {
  color: #e74c3c;
  text-align: center;
  font-weight: bold;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
  background: #ecf0f1;
  border-radius: 5px;
  overflow: hidden;
}

table th,
table td {
  border: 1px solid #bdc3c7;
  padding: 10px;
  text-align: left;
}

table th {
  background-color: #34495e;
  color: #ecf0f1;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  background: #ecf0f1;
  margin: 5px 0;
  padding: 10px;
  border-radius: 5px;
}
</style>
