<template>
  <div class="event-page">
    <div class="search-bar-container">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Nach Events suchen..."
        class="search-bar"
      />
    </div>

    <div class="event-columns">
      <div class="event-column">
        <h2>Angemeldete Events</h2>
        <div v-if="isLoading">
          <p>Loading...</p>
        </div>
        <div
          v-else-if="filteredRegisteredEvents.length === 0"
          class="empty-state"
        >
          Keine angemeldeten Events gefunden
        </div>
        <div v-else class="list-left">
          <div
            v-for="event in filteredRegisteredEvents"
            :key="event.id"
            class="event-item"
          >
            <div class="event-details">
              <h3>{{ event.name }}</h3>
              <p>{{ formatDate(event.date) }}</p>
              <button @click="goToEvent(event.id)" class="register-button">
                Details
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="event-column">
        <h2>Empfohlene Events</h2>
        <div v-if="isLoading">
          <p>Loading...</p>
        </div>
        <div
          v-else-if="filteredRecommendedEvents.length === 0"
          class="empty-state"
        >
          Keine empfohlenen Events gefunden
        </div>
        <div v-else class="list-left">
          <div
            v-for="event in filteredRecommendedEvents"
            :key="event.id"
            class="event-item"
          >
            <div class="event-details">
              <h3>{{ event.name }}</h3>
              <p>{{ formatDate(event.date) }}</p>
              <!-- Tags anzeigen als Chips -->
              <div class="tag-chips">
                <span
                  v-for="(tag, index) in event.tags.slice(0, 5)"
                  :key="tag.id"
                  class="chip"
                >
                  {{ tag.name }}
                </span>
              </div>
              <button @click="goToEvent(event.id)" class="register-button">
                Details
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="event-column">
        <div class="feedback-section">
          <h2>Feedback geben</h2>
          <div v-if="isLoading">
            <p>Loading...</p>
          </div>
          <div
            v-else-if="filteredFeedbackEvents.length === 0"
            class="empty-state"
          >
            Keine Events für Feedback verfügbar
          </div>
          <div v-else class="list-right">
            <div
              v-for="event in filteredFeedbackEvents"
              :key="event.id"
              class="event-item"
            >
              <div class="event-details">
                <h3>{{ event.name }}</h3>
                <p>{{ formatDate(event.date) }}</p>
                <button @click="goToEvent(event.id)" class="register-button">
                  Feedback
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="past-events-section">
          <h2>Vergangene Events</h2>
          <div v-if="isLoading">
            <p>Loading...</p>
          </div>
          <div v-else-if="filteredPastEvents.length === 0" class="empty-state">
            Keine vergangenen Events
          </div>
          <div v-else class="list-right">
            <div
              v-for="event in filteredPastEvents"
              :key="event.id"
              class="event-item"
            >
              <div class="event-details">
                <h3>{{ event.name }}</h3>
                <p>{{ formatDate(event.date) }}</p>
                <button @click="goToEvent(event.id)" class="register-button">
                  Details
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import config from "@/config";
import Cookies from "js-cookie";
import { showToast, Toast } from "@/types/toasts";
import { faXmark, faCheck } from "@fortawesome/free-solid-svg-icons";

const router = useRouter();
const registeredEvents = ref([]);
const recommendedEvents = ref([]);
const pendingFeedbackEvents = ref([]);
const pastEvents = ref([]);
const isLoading = ref(true);
const today = new Date().toISOString();

const searchQuery = ref("");
const userId = Cookies.get("userId");

const filteredRegisteredEvents = computed(() =>
  registeredEvents.value
    .filter((event) => {
      const eventDate = new Date(event.date);
      const [hours, minutes] = event.startTime.split(":").map(Number);
      eventDate.setHours(hours, minutes, 0, 0);
      return eventDate > new Date();
    })
    .filter((event) =>
      event.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
    )
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()),
);

const filteredRecommendedEvents = computed(() =>
  recommendedEvents.value
    .filter((event) =>
      event.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
    )
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()),
);

const filteredFeedbackEvents = computed(() =>
  pendingFeedbackEvents.value
    .filter((event) =>
      event.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
    )
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()),
);

const filteredPastEvents = computed(() =>
  pastEvents.value
    .filter((event) =>
      event.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
    )
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()),
);

/*
 * Fetches the tags for a given event
 */
const fetchTagsForEvent = async (eventId) => {
  try {
    const res = await fetch(`${config.apiBaseUrl}/events/${eventId}/tags`);
    if (!res.ok) throw new Error("Failed to fetch tags");
    return await res.json();
  } catch (error) {
    showToast(
      new Toast(
        "Error",
        `Fehler beim Laden der Tags für Event ${eventId}`,
        "error",
        faXmark,
        10,
      ),
    );
    return [];
  }
};

/*
 * Fetches the events for the user
 */
const fetchEvents = async () => {
  if (!userId) {
    showToast(
      new Toast("Error", `Keine Benutzer-ID gefunden`, "error", faXmark, 10),
    );
    isLoading.value = false;
    return;
  }

  try {
    isLoading.value = true;

    const [registeredRes, recommendedRes, feedbackRes] = await Promise.all([
      fetch(`${config.apiBaseUrl}/users/${userId}/registeredEvents`),
      fetch(`${config.apiBaseUrl}/users/${userId}/recommendedEvents?limit=5`),
      fetch(`${config.apiBaseUrl}/users/${userId}/pendingFeedbackEvents`),
    ]);

    if (!registeredRes.ok || !recommendedRes.ok || !feedbackRes.ok) {
      showToast(
        new Toast("Error", `Fehler der Laden der Events`, "error", faXmark, 10),
      );
    }

    registeredEvents.value = await registeredRes.json();
    recommendedEvents.value = await recommendedRes.json();
    pendingFeedbackEvents.value = await feedbackRes.json();

    for (const event of recommendedEvents.value) {
      event.tags = await fetchTagsForEvent(event.id);
    }

    pastEvents.value = registeredEvents.value.filter(
      (event) =>
        new Date(event.date) <= new Date(today) &&
        event.startTime <= new Date().toLocaleTimeString(),
    );
  } catch (error) {
    showToast(
      new Toast("Error", `Fehler der Laden der Events`, "error", faXmark, 10),
    );
  } finally {
    isLoading.value = false;
  }
};

const goToEvent = (eventId) => {
  router.push({ name: "EventPage", params: { eventId } });
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString("de-DE");
};

onMounted(() => {
  fetchEvents();
});
</script>

<style scoped>
.event-page {
  padding: 20px;
  background: #f9f9f9;
  max-height: 90%;
}

.search-bar-container {
  margin-bottom: 20px;
  text-align: center;
}

.search-bar {
  width: 80%;
  max-width: 600px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.search-bar:focus {
  border-color: #007bff;
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.2);
  outline: none;
}

.event-columns {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  overflow: auto;
}

.event-column {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: auto;
}

.event-details {
  background-color: #eaf4fb;
  border: 1px solid #2c94e4;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.8rem;
  position: relative;
  overflow: auto;
  color: #333;
}

h2 h3 p {
  margin: 0.5rem;
}

.register-button {
  background-color: #003e81;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  font-size: 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease;
  position: absolute;
  bottom: 0.5rem;
  right: 0.5rem;
}

.register-button:hover {
  background-color: #0056b3;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.register-button:disabled {
  background-color: #ccc;
}

.empty-state {
  text-align: center;
  font-size: 1.2rem;
  color: #777;
}

.list-right {
  max-height: 29vh;
  overflow-y: auto;
}

.list-left {
  max-height: 65vh;
  overflow-y: auto;
}

.tag-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-top: 5px;
}

.chip {
  background-color: #d9f2ff;
  padding: 6px 12px;
  border-radius: 50px;
  font-size: 0.9rem;
  color: #007bff;
  font-weight: bold;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease;
}

.chip:hover {
  background-color: #007bff;
  color: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}
</style>
