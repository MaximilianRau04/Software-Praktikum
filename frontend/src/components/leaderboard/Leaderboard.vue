<template>
  <div class="leaderboard-container">
    <h1>Rangliste</h1>
    <table>
      <thead>
        <tr>
          <th>Rang</th>
          <th>Nutzername</th>
          <th>Gesamtpunktzahl</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(entry, index) in leaderboard" :key="entry.userId">
          <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
          <td @click="goToProfile(entry.username)" class="clickable-username">
            {{ entry.username }}
          </td>
          <td>{{ entry.totalPoints }}</td>
        </tr>
      </tbody>
    </table>
    <div class="pagination">
      <button
        :disabled="currentPage === 1"
        @click="fetchLeaderboard(currentPage - 1)"
      >
        Previous
      </button>
      <span>Seite: {{ currentPage }}</span>
      <button :disabled="!hasMore" @click="fetchLeaderboard(currentPage + 1)">
        Next
      </button>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import config from "@/config.js";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";

export default {
  data() {
    return {
      leaderboard: [],
      currentPage: 1,
      pageSize: 10,
      hasMore: false,
    };
  },
  methods: {
    goToProfile(username) {
      this.$router.push(`/profile/${username}`);
    },

    /**
     * Fetches the leaderboard from the API.
     * @param {number} page The page number to fetch.
     */
    async fetchLeaderboard(page) {
      try {
        const response = await api.get(`/leaderboard`, {
          params: {
            page: page - 1,
            size: this.pageSize,
          },
        });
        this.leaderboard = response.data.content;
        this.currentPage = page;
        this.hasMore = !response.data.last;
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            `Die Rangliste konnte nicht geladen werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },
  },
  mounted() {
    this.fetchLeaderboard(this.currentPage);
  },
};
</script>

<style scoped>
.leaderboard-container {
  margin-top: 20px;
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

th,
td {
  border: 2px solid #121c7c;
  padding: 8px;
  text-align: center;
  color: #12a8ee;
}

th {
  background-color: #f4f4f4;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

button {
  padding: 5px 10px;
  border: 1px solid #ccc;
  background-color: #f4f4f4;
  cursor: pointer;
  border-radius: 4px;
}

button:disabled {
  background-color: #e0e0e0;
  cursor: not-allowed;
}

.clickable-username {
  cursor: pointer;
  color: #12a8ee;
}
</style>
