<template>
  <div class="event-dashboard">
    <div class="event-search-container">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Nach Events suchen..."
        class="event-search-input"
      />
    </div>

    <div class="events-columns-container">
      <!-- Left Column -->
      <div class="events-column registered-events-column">
        <h2>Angemeldete Events</h2>
        <div v-if="isLoading">
          <p>Loading...</p>
        </div>
        <div
          v-else-if="filteredRegisteredEvents.length === 0"
          class="empty-state-message"
        >
          Keine angemeldeten Events gefunden
        </div>
        <div v-else class="events-list">
          <div
            v-for="event in filteredRegisteredEvents"
            :key="event.id"
            class="event-card"
          >
            <h3 class="event-title">{{ event.name }}</h3>
            <p class="event-date">{{ formatDate(event.date) }}</p>
            <button @click="goToEvent(event.id)" class="event-action-button">
              Details
            </button>
          </div>
        </div>
      </div>

      <!-- Center Column -->
      <div class="events-column recommended-events-column">
        <h2>Empfohlene Events</h2>
        <div v-if="isLoading">
          <p>Loading...</p>
        </div>
        <div
          v-else-if="filteredRecommendedEvents.length === 0"
          class="empty-state-message"
        >
          Nehmen Sie an Workshops teil und geben Sie Feedback, damit wir Ihnen Vorschläge für interessante Workshops machen können.
        </div>
        <div v-else class="events-list">
          <div
            v-for="event in filteredRecommendedEvents"
            :key="event.id"
            class="event-card"
          >
            <h3 class="event-title">{{ event.name }}</h3>
            <p class="event-date">{{ formatDate(event.date) }}</p>
            <div class="event-tags-container">
              <span
                v-for="(tag, index) in event.tags.slice(0, 5)"
                :key="tag.id"
                class="event-tag"
              >
                {{ tag.name }}
              </span>
            </div>
            <button @click="goToEvent(event.id)" class="event-action-button">
              Details
            </button>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="events-column feedback-past-column">
        <div class="feedback-section">
          <h2>Feedback geben</h2>
          <div v-if="isLoading">
            <p>Loading...</p>
          </div>
          <div
            v-else-if="filteredFeedbackEvents.length === 0"
            class="empty-state-message"
          >
            Keine Events für Feedback verfügbar
          </div>
          <div v-else class="events-list">
            <div
              v-for="event in filteredFeedbackEvents"
              :key="event.id"
              class="event-card"
            >
              <h3 class="event-title">{{ event.name }}</h3>
              <p class="event-date">{{ formatDate(event.date) }}</p>
              <button @click="goToFeedback(event.id)" class="event-action-button">
                Feedback
              </button>
            </div>
          </div>
        </div>

        <div class="past-events-section">
          <h2>Vergangene Events</h2>
          <div v-if="isLoading">
            <p>Loading...</p>
          </div>
          <div v-else-if="filteredPastEvents.length === 0" class="empty-state-message">
            Keine vergangenen Events
          </div>
          <div v-else class="events-list">
            <div
              v-for="event in filteredPastEvents"
              :key="event.id"
              class="event-card"
            >
              <h3 class="event-title">{{ event.name }}</h3>
              <p class="event-date">{{ formatDate(event.date) }}</p>
              <button @click="goToEvent(event.id)" class="event-action-button">
                Details
              </button>
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
      const [hours, minutes] = event.startTime.split(':').map(Number);
      eventDate.setHours(hours, minutes, 0, 0);
      return eventDate > new Date();
    })
    .filter((event) =>
      event.name.toLowerCase().includes(searchQuery.value.toLowerCase())
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
        10
      )
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
      new Toast("Error", `Keine Benutzer-ID gefunden`, "error", faXmark, 10)
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
        new Toast("Error", `Fehler beim Laden der Events`, "error", faXmark, 10)
      );
    }

    registeredEvents.value = await registeredRes.json();
    recommendedEvents.value = await recommendedRes.json();
    pendingFeedbackEvents.value = await feedbackRes.json();

    for (const event of recommendedEvents.value) {
      event.tags = await fetchTagsForEvent(event.id);
    }

    pastEvents.value = registeredEvents.value.filter(
      (event) => new Date(event.date) <= new Date(today) && event.startTime <= new Date().toLocaleTimeString(),
    );
  } catch (error) {
    showToast(
      new Toast("Error", `Fehler beim Laden der Events`, "error", faXmark, 10)
    );
  } finally {
    isLoading.value = false;
  }
};

const goToEvent = (eventId) => {
  router.push({ name: "EventPage", params: { eventId } });
};

const goToFeedback = async (eventId) => {
  const token = await fetchAttendanceToken(eventId);
  if (token) {
    router.push({
      name: "feedback",
      params: { eventId },
      query: { token }
    });
  } else {
    showToast(
      new Toast("Error", "Fehler beim Laden des Teilnahme-Tokens", "error", faXmark, 10)
    );
  }
};

const fetchAttendanceToken = async (eventId) => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/events/${eventId}/attendance-token`);
    if (!response.ok) throw new Error("Failed to fetch attendance token");
    const data = await response.json();
    return data.attendanceToken;
  } catch (error) {
    console.error("Error fetching attendance token:", error);
    showToast(
      new Toast("Error", "Fehler beim Laden des Teilnahme-Tokens", "error", faXmark, 10)
    );
    return null;
  }
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
.event-dashboard {
  padding: 2rem;
  max-width: 1600px;
  margin: 0 auto;
  height: calc(100vh - 4rem);
  display: flex;
  flex-direction: column;
}

.events-columns-container {
  flex: 1;
  display: flex;
  gap: 2rem;
  min-height: 0;
}

.event-search-input {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.event-search-input:focus {
  outline: none;
  border-color: #2196F3;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
}

.events-columns-container {
  display: flex;
  gap: 2rem;
  min-height: 70vh;
}

.events-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.registered-events-column,
.recommended-events-column {
  padding: 1.5rem;
}

.feedback-past-column {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  height: 100%;
}

.feedback-section,
.past-events-section {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  margin: 0 1rem;
}

.events-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 0.5rem;
  margin-top: 1rem;
  min-height: 0;
}

.event-card {
  position: relative;
  padding: 1.5rem;
  margin-bottom: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: transform 0.2s ease;
  min-height: 60px;
  display: flex;
  flex-direction: column;
}

.event-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.event-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.event-date {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 auto 0;
}

.event-action-button {
  align-self: flex-end;
  margin-top: 1rem;
  padding: 0.5rem 1.25rem;
  background: #2196F3;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s ease;
}

.event-action-button:hover {
  background: #1976D2;
}

.empty-state-message {
  color: #888;
  text-align: center;
  padding: 2rem;
  border: 2px dashed #eee;
  border-radius: 8px;
  margin-top: 1rem;
}

.event-tags-container {
  margin: 0.5rem 0 1rem 0;
}

.event-tag {
  display: inline-block;
  background: #e3f2fd;
  color: #1976D2;
  padding: 0.25rem 0.75rem;
  border-radius: 16px;
  font-size: 0.8rem;
  margin-right: 0.5rem;
}

@media (max-width: 1200px) {
  .events-columns-container {
    flex-direction: column;
  }
  
  .events-column {
    min-height: 400px;
  }
}
</style>
