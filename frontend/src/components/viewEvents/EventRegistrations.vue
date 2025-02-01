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
//

// TODO: ADD EVENT CARDS FROM DASHBOARD

//
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import { faXmark, faCheck } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";
import { useAuth } from "@/util/auth";

const router = useRouter();
const auth = useAuth();

const registeredEvents = ref([]);
const recommendedEvents = ref([]);
const pendingFeedbackEvents = ref([]);
const pastEvents = ref([]);
const isLoading = ref(true);
const today = new Date().toISOString();

const searchQuery = ref("");

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
    const response = await api.get(`/events/${eventId}/tags`);
    if (response.status !== 200) throw new Error("Failed to fetch tags");
    return await response.data;
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `Event-Tags für Event: ${eventId} konnten nicht geladen werden.`,
        "error",
        faXmark,
        5
      )
    );
    return [];
  }
};

/*
 * Fetches the events for the user
 */
const fetchEvents = async () => {
  const userId = auth.getUserId();

  if (!userId) {
    showToast(
      new Toast("Fehler", `Loggen Sie sich bitte ein, um auf Ihre Events zuzugreifen.`, "error", faXmark, 5)
    );
    isLoading.value = false;
    auth.clearToken();
    router.push(`/login`);
    return;
  }

  try {
    isLoading.value = true;

    const [registeredRes, recommendedRes, feedbackRes] = await Promise.all([
      api.get(`/users/${userId}/registeredEvents`),
      api.get(`/users/${userId}/recommendedEvents?limit=5`),
      api.get(`/users/${userId}/pendingFeedbackEvents`),
    ]);

    if (registeredRes.status !== 200 || recommendedRes.status !== 200 || feedbackRes.status !== 200) {
      showToast(
        new Toast("Fehler", `Events konnten nicht geladen werden.`, "error", faXmark, 5)
      );
    }

    registeredEvents.value = await registeredRes.data;
    recommendedEvents.value = await recommendedRes.data;
    pendingFeedbackEvents.value = await feedbackRes.data;

    for (const event of recommendedEvents.value) {
      event.tags = await fetchTagsForEvent(event.id);
    }

    pastEvents.value = registeredEvents.value.filter(
      (event) => new Date(event.date) <= new Date(today) && event.startTime <= new Date().toLocaleTimeString(),
    );
  } catch (error) {
    showToast(
      new Toast("Fehler", `Events konnten nicht geladen werden.`, "error", faXmark, 5)
    );
  } finally {
    isLoading.value = false;
  }
};

const goToEvent = (eventId) => {
  router.push({ name: "EventPage", params: { eventId } });
};

const goToFeedback = async (eventId: string) => {
  if (!auth.isAuthenticated.value) {
    router.push({
      name: 'login',
      query: { returnUrl: `/events/${eventId}/feedback` }
    });
    return;
  }

  try {
    const isValid = await verifyFeedbackEligibility(eventId);
    
    if (!isValid) {
      router.push('/home');
      showToast(
        new Toast("Zugriff verweigert", "Sie sind nicht berechtigt Feedback für dieses Event zu geben", "error", faXmark, 5)
      );
      return;
    }

    const token = await fetchAttendanceToken(eventId);
    
    router.push({
      name: "feedback",
      params: { eventId },
      query: { token: token }
    });
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Zugriff auf Feedback-Formular", "error", faXmark, 5)
    );
  }
};


const verifyFeedbackEligibility = async (eventId: string): Promise<boolean> => {
  try {
    const response = await api.get(`/events/${eventId}/feedback-eligibility`);
    return response.data.isEligible;
  } catch (error) {
    if (error.response?.status === 403) {
      return false;
    }
    throw error;
  }
};

const fetchAttendanceToken = async (eventId: string): Promise<string> => {
  const response = await api.get(`/events/${eventId}/attendance-token`);
  return response.data.attendanceToken;
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
  max-width: 1200px;
  max-height: 90%;
  margin: 0 auto;
  height: calc(100vh - 4rem);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background: #f8fafc;
}

.event-search-container {
  position: relative;
  margin-bottom: 0.5rem;
  width: 94%;
}

.event-search-input {
  width: 100%;
  padding: 1rem 1rem 1rem 3rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  background: white url('~@/assets/search-icon.svg') no-repeat 1rem center;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.event-search-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59,130,246,0.1);
}

.events-columns-container {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr; 
  gap: 1rem; 
  min-height: 0;
}

.events-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.events-column h2 {
  font-size: 1.25rem;
  font-weight: 600;
  padding: 1.5rem;
  margin: 0;
  background: #f8fafc;
  border-bottom: 2px solid #e2e8f0;
  color: #1e293b;
}

.registered-events-column h2 {
  border-left: 4px solid #3b82f6;
}

.recommended-events-column h2 {
  border-left: 4px solid #10b981;
}

.feedback-past-column h2 {
  border-left: 4px solid #e91818;
}

.events-list {
  padding: 1rem;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.event-card {
  background: white;
  border-radius: 8px;
  padding: 1.25rem;
  margin-bottom: 1rem;
  border: 1px solid #e2e8f0;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  position: relative;
}

.event-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.event-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.event-date {
  color: #64748b;
  font-size: 0.9rem;
  margin-bottom: 0.75rem;
}

.event-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin: 0.75rem 0;
}

.event-tag {
  background: #f1f5f9;
  color: #475569;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  border: 1px solid #e2e8f0;
}

.event-action-button {
  background: #009ee2;
  color: white;
  border: none;
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: 0.2s ease;
  align-self: flex-start;
}

.event-action-button:hover {
  background: #0174a5;
}

.empty-state-message {
  text-align: center;
  padding: 2rem;
  color: #94a3b8;
  border: 2px dashed #e2e8f0;
  border-radius: 8px;
  margin: 1rem;
  background: #f8fafc;
}

@media (max-width: 1200px) {
  .events-columns-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .events-column {
    min-height: auto;
    height: auto;
  }
  
  .feedback-past-column {
    grid-template-rows: auto auto;
  }
}

.feedback-list {
  overflow-y: auto;
}

.past-events-section {
  border-top: 2px solid #e2e8f0;
  padding-top: 1.5rem;
  margin: 1rem 0;
  flex: 1; 
  max-height: 40%; 
}

.feedback-past-column {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  height: 100%;
}

.feedback-section {
  flex: 3; 
  min-height: 65vh; 
  overflow-y: auto; 
  padding-bottom: 1rem;
}

.feedback-section .event-card {
  padding: 1.5rem;
  margin-bottom: 1rem;
  min-height: 120px; 
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.past-events-section .event-card {
  padding: 1rem;
  margin-bottom: 0.75rem;
}

.past-events-section .event-title {
  font-size: 0.95rem;
}

.progress-indicator {
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
  margin: 0.5rem 0;
}

.progress-bar {
  height: 100%;
  background: #3b82f6;
  width: 60%;
  transition: width 0.3s ease;
}
</style>

