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

            <button
              @click="pinComment(comment)"
              :disabled="!trainerProfileId"
              class="pin-btn"
              :class="{ 'pin-btn-disabled': !trainerProfileId }"
            >
              Pin Comment
            </button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import config from "@/config";
import Cookies from "js-cookie";

export default {
  name: "EventSummary",
  data() {
    return {
      data: null,
      isLoading: true,
      error: null,
      trainerProfileId: null
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
    async fetchData() {
      try {
        this.isLoading = true;
        await Promise.all([
          this.fetchEventSummary(),
          this.fetchTrainerProfileId()
        ]);
      } catch (err) {
        this.error = err.message || "An unknown error occurred";
        console.error("Error fetching data:", err);
      } finally {
        this.isLoading = false;
      }
    },

    async fetchEventSummary() {
      const response = await fetch(
        `${config.apiBaseUrl}/events/${this.eventId}/summary`
      );

      if (!response.ok) {
        throw new Error(`Failed to fetch event summary: ${response.status}`);
      }

      this.data = await response.json();
    },

    async fetchTrainerProfileId() {
      const userId = Cookies.get("userId");

      if (!userId) {
        throw new Error("User ID not found in cookies. Please log in again.");
      }

      try {
        const response = await fetch(
          `${config.apiBaseUrl}/users/${userId}/trainerProfile`
        );

        if (!response.ok) {
          throw new Error(`Failed to fetch trainer profile: ${response.status}`);
        }

        const trainerProfile = await response.json();
        
        if (!trainerProfile || !trainerProfile.id) {
          throw new Error("Trainer profile ID not found in response");
        }

        this.trainerProfileId = trainerProfile.id;
      } catch (err) {
        console.error("Error fetching trainer profile ID:", err);
        throw new Error("Failed to fetch trainer profile: " + err.message);
      }
    },

    async pinComment(comment) {
      if (!this.trainerProfileId) {
        this.error = "Trainer profile ID is not available. Please try again later.";
        return;
      }

      if (!comment || !comment.feedbackId) {
        this.error = "Invalid comment or feedback ID";
        return;
      }

      try {
        const response = await fetch(
          `${config.apiBaseUrl}/trainerProfiles/${this.trainerProfileId}/${comment.feedbackId}/pin?commentType=improvement`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (!response.ok) {
          throw new Error(`Failed to pin comment: ${response.status}`);
        }

        alert(`Comment successfully pinned: "${comment.comment}"`);
      } catch (err) {
        console.error("Error pinning comment:", err);
        this.error = err.message;
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

.pin-btn {
  background-color: #009ee2;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  margin-top: 8px;
  display: inline-block;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pin-btn:hover {
  background-color: #2980b9;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
}

.pin-btn:active {
  transform: translateY(0);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.pin-btn:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.3);
}
</style>
