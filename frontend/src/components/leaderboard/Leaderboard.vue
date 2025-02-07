<template>
  <div class="leaderboard-container">
    <!-- Current User Position Banner -->
    <div v-if="currentUserPosition" class="current-user-banner">
      <div class="banner-content">
        <span class="banner-rank">Dein Rang: #{{ currentUserPosition.rank }}</span>
        <span class="banner-username">{{ currentUserPosition.username }}</span>
        <span class="banner-points">{{ currentUserPosition.totalPoints }} Punkte</span>
      </div>
    </div>

    <div class="leaderboard-header">
      <h1>🏆 Rangliste</h1>
      <div class="leaderboard-controls">
        <input v-model="searchQuery" placeholder="Nutzer suchen..." @input="handleSearch" class="search-input" />
  
      </div>
    </div>

    <div class="leaderboard-content">
      <div v-if="isLoading" class="loading-state">
        <p>Loading...</p>
      </div>

      <div v-else>
        <div v-if="leaderboard.length === 0" class="empty-state-message">
          Keine Einträge gefunden
        </div>

        <div v-else class="leaderboard-entries">
          <div v-for="entry in sortedLeaderboard" :key="entry.userId" class="leaderboard-card" :class="{
            'current-user': entry.userId === currentUserPosition?.userId,
            'podium-1': entry.rank === 1,
            'podium-2': entry.rank === 2,
            'podium-3': entry.rank === 3
          }">


            <div class="entry-rank">
              <span v-if="entry.rank === 1" class="trophy-icon">🥇</span>
              <span v-else-if="entry.rank === 2" class="trophy-icon">🥈</span>
              <span v-else-if="entry.rank === 3" class="trophy-icon">🥉</span>
              {{ entry.rank }}
            </div>


            <div class="entry-user clickable-username" @click="goToProfile(entry.username)">
              <span class="username">{{ entry.username }}</span>
              <span v-if="entry.userId === currentUserPosition?.userId" class="you-badge">
                (Du)
              </span>
            </div>

            <div class="entry-points">
              {{ entry.totalPoints }} Punkte
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="leaderboard-pagination">
      <button @click="fetchLeaderboard(currentPage - 1)" :disabled="currentPage === 1" class="pagination-button">
        Zurück
      </button>
      <span class="page-indicator">Seite {{ currentPage }}</span>
      <button @click="fetchLeaderboard(currentPage + 1)" :disabled="!hasMore" class="pagination-button">
        Weiter
      </button>
    </div>
  </div>
</template>

<script>
import api from "@/util/api";
import { useAuth } from "@/util/auth";
import { showToast, Toast } from "@/types/toasts";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import Avatar from "./Avatar.vue";

export default {
  components: { Avatar },
  data() {
    return {
      leaderboard: [],
      currentPage: 1,
      pageSize: 10,
      hasMore: false,
      searchQuery: '',
      sortAsc: false,
      currentUser: null,
      currentUserEntry: null,
      searchTimeout: null,
      currentUserPosition: null,
    };
  },
  methods: {
    goToProfile(username) {
      this.$router.push(`/profile/${username}`);
    },

    async fetchLeaderboard(page) {
      try {
        const response = await api.get(`/leaderboard`, {
          params: {
            page: page - 1,
            size: this.pageSize,
            search: this.searchQuery,
            sort: this.sortAsc ? "asc" : "desc"
          }
        });

      this.leaderboard = response.data.content
      .map(entry => ({ ...entry, rank: entry.rank }))
      .sort((a, b) => a.rank - b.rank);


      this.currentPage = page;
      this.hasMore = !response.data.last;

      } catch (error) {
        showToast(new Toast("Error", "Failed to load leaderboard", "error", faXmark, 5));
      }
    },

    async fetchCurrentUserPosition() {
      try {
        const response = await api.get('/leaderboard/current-position');
        this.currentUserPosition = response.data;

        const userPage = Math.ceil(this.currentUserPosition.rank / this.pageSize);
        if (userPage !== this.currentPage) {
          this.fetchLeaderboard(userPage);
        }

      } catch (error) {
        console.error('Could not fetch current user position');
      }
    },

    handleSearch() {
      clearTimeout(this.searchTimeout);
      this.searchTimeout = setTimeout(() => {
        this.fetchLeaderboard(1);
      }, 300);
    },

    toggleSortOrder() {
      this.sortAsc = !this.sortAsc;
      this.fetchLeaderboard(1);
    }
  },
  computed: {
    sortedLeaderboard() {
      return [...this.leaderboard].sort((a, b) =>
        this.sortAsc ? b.rank - a.rank : a.rank - b.rank
      );
    }

  }
  ,
  async mounted() {
    await this.fetchLeaderboard(this.currentPage);
    await this.fetchCurrentUserPosition();

    this.currentUser = useAuth().getUserData();
  }
};
</script>

<style scoped>
.current-user-banner {
  background: #e3f2fd;
  border: 2px solid #2196F3;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  padding: 1rem;
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.banner-rank {
  font-weight: 600;
  color: #1976D2;
}

.banner-username {
  flex-grow: 1;
  color: #333;
}

.banner-points {
  background: #2196F3;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 16px;
  font-size: 0.9rem;
}

.current-user .entry-user {
  font-weight: 600;
  color: #1976D2;
}

.current-user .entry-points {
  color: #1976D2;
  font-weight: 600;
}

.leaderboard-container {
  padding: 2rem;
  max-width: 1000px;
  margin: 0 auto;
  height: calc(100vh - 4rem);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.leaderboard-header {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.leaderboard-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #2196F3;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
}

.sort-button {
  padding: 1rem 1.5rem;
  background: #2196F3;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s ease;
}

.sort-button:hover {
  background: #1976D2;
}

.leaderboard-entries {
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  flex: 1;
  overflow-y: auto;
  padding-right: 0.5rem;
}

.leaderboard-card {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding: 1.5rem;
  background: #ffffff;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.leaderboard-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.entry-rank {
  font-weight: 600;
  color: #2196F3;
  min-width: 50px;
  text-align: center;
}

.trophy-icon {
  margin-right: 0.5rem;
}

.entry-user {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.username {
  font-weight: 500;
  color: #333;
}

.you-badge {
  background: #e3f2fd;
  color: #1976D2;
  padding: 0.25rem 0.75rem;
  border-radius: 16px;
  font-size: 0.9rem;
}

.entry-points {
  color: #666;
  font-size: 0.95rem;
  min-width: 120px;
  text-align: right;
}

.podium-1 {
  border-left: 4px solid #FFD700;
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.05) 0%, rgba(255, 215, 0, 0) 100%);
}

.podium-2 {
  border-left: 4px solid #C0C0C0;
  background: linear-gradient(90deg, rgba(192, 192, 192, 0.05) 0%, rgba(192, 192, 192, 0) 100%);
}

.podium-3 {
  border-left: 4px solid #CD7F32;
  background: linear-gradient(90deg, rgba(205, 127, 50, 0.05) 0%, rgba(205, 127, 50, 0) 100%);
}

.current-user {
  background-color: #f5faff;
  border-color: #2196F3;
}

.empty-state-message {
  color: #888;
  text-align: center;
  padding: 2rem;
  border: 2px dashed #eee;
  border-radius: 8px;
  margin: 1rem 0;
}

.leaderboard-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  padding: 1rem 0;
  border-top: 1px solid #eee;
}

.pagination-button {
  padding: 0.75rem 1.5rem;
  background: #2196F3;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s ease;
}

.pagination-button:disabled {
  background: #90caf9;
  cursor: not-allowed;
}

.active-sort {
  background-color: #1976D2 !important;
  color: white !important;
}

@media (max-width: 768px) {
  .leaderboard-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .entry-rank,
  .entry-points {
    text-align: left;
    min-width: auto;
  }

  .leaderboard-controls {
    flex-direction: column;
  }

  .sort-button {
    width: 100%;
  }
}
</style>