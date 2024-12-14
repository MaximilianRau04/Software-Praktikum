<template>
  <div class="event-summary">
    <h1>Event Summary</h1>

    <div v-if="isLoading">
      <p>Loading...</p>
    </div>

    <div v-else-if="error">
      <p class="error">Error: {{ error }}</p>
    </div>

    <div v-else>
      <div class="general-info">
        <h2>General Information</h2>
        <p><strong>Event ID:</strong> {{ data.eventId || 'undefined' }}</p>
        <p><strong>Event Name:</strong> {{ data.eventName || 'undefined' }}</p>
        <p><strong>Organizer Name:</strong> {{ data.organizerName || 'undefined' }}</p>
      </div>

      <div class="numerical-feedback">
        <h2>Numerical Feedback</h2>
        <table v-if="Object.keys(data.numericalFeedback).length > 0">
          <thead>
            <tr>
              <th>Category</th>
              <th>Average Score</th>
              <th>Median Score</th>
              <th>Response Count</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(scoreData, category) in data.numericalFeedback" :key="category">
              <td>{{ formatKey(category) }}</td>
              <td>{{ scoreData.average }}</td>
              <td>{{ scoreData.median }}</td>
              <td>{{ scoreData.responseCount }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else>No numerical feedback available.</p>
      </div>

      <div class="common-words">
        <h2>Common Words</h2>
        <ul>
          <li v-for="(word, index) in data.commonWords" :key="index">{{ word }}</li>
        </ul>
      </div>

      <div class="comments">
        <h2>Comments</h2>
        <ul>
          <li v-for="(comment, index) in data.comments" :key="index">
            <p><strong>Comment:</strong> {{ comment.comment }}</p>
            <p><strong>Sentiment:</strong> {{ comment.sentiment || 'undefined' }}</p>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EventSummary',
  props: {
    eventId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      data: null,
      isLoading: true,
      error: null
    };
  },
  methods: {
    async fetchData() {
      try {
        const response = await fetch(`/events/${this.eventId}/summary`);
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        this.data = await response.json();
      } catch (err) {
        this.error = err.message || 'An unknown error occurred';
      } finally {
        this.isLoading = false;
      }
    },
    formatKey(key) {
      return key.replace(/([A-Z])/g, ' $1').replace(/^./, (str) => str.toUpperCase());
    }
  },
  mounted() {
    this.fetchData();
  }
};
</script>

<style scoped>
.event-summary {
  font-family: Arial, sans-serif;
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  max-height: 90vh;
}

.general-info, .numerical-feedback, .common-words, .comments {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
}

h1, h2 {
  color: #2c3e50;
  margin-bottom: 10px;
}

ul {
  list-style-type: disc;
  padding-left: 20px;
}

p {
  margin: 5px 0;
}

.error {
  color: red;
  font-weight: bold;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 8px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f4f4f4;
}
</style>
