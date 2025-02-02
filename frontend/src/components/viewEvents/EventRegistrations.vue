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
            class="event-details"
          >

            <div class="event-header-home">
              <h2>{{ event.name }}</h2>
              <div class="meta-grid">
                <div class="meta-item">
                  <CalendarIcon class="icon-small" />
                  <span class="value">{{ formatDate(event.date) }}</span>
                </div>
                <div class="meta-item">
                  <ClockIcon class="icon-small" />
                  <span class="value">
                    {{ event.startTime ? event.startTime.split(':').slice(0, 2).join(':') : '-' }} –
                    {{ event.endTime ? event.endTime.split(':').slice(0, 2).join(':') : '-' }}
                  </span>
                </div>
                <div class="meta-item">
                  <MapPinIcon class="icon-small" />
                  <span class="value">{{ event.room?.name || "-" }}</span>
                </div>
              </div>
            </div>

            <button @click="goToEvent(event.id)" class="register-button">
              <ArrowRightCircleIcon class="icon-button" />
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
            class="event-details"
          >

            <div class="event-header-home">
              <h2>{{ event.name }}</h2>
              <div class="meta-grid">
                <div class="meta-item">
                  <CalendarIcon class="icon-small" />
                  <span class="value">{{ formatDate(event.date) }}</span>
                </div>
                <div class="meta-item">
                  <ClockIcon class="icon-small" />
                  <span class="value">
                    {{ event.startTime ? event.startTime.split(':').slice(0, 2).join(':') : '-' }} –
                    {{ event.endTime ? event.endTime.split(':').slice(0, 2).join(':') : '-' }}
                  </span>
                </div>
                <div class="meta-item">
                  <MapPinIcon class="icon-small" />
                  <span class="value">{{ event.room?.name || "-" }}</span>
                </div>
              </div>
            </div>

            <div class="event-tags" v-if="event.tags?.length">
              <span class="tag" v-for="tag in event.tags" :key="tag.id">
                <TagIcon class="icon-tag" />
                {{ tag.name }}
              </span>
            </div>

            <button @click="goToEvent(event.id)" class="register-button">
              <ArrowRightCircleIcon class="icon-button" />
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
            class="event-details"
          >

            <div class="event-header-home">
              <h2>{{ event.name }}</h2>
              <div class="meta-grid">
                <div class="meta-item">
                  <CalendarIcon class="icon-small" />
                  <span class="value">{{ formatDate(event.date) }}</span>
                </div>
                <div class="meta-item">
                  <ClockIcon class="icon-small" />
                  <span class="value">
                    {{ event.startTime ? event.startTime.split(':').slice(0, 2).join(':') : '-' }} –
                    {{ event.endTime ? event.endTime.split(':').slice(0, 2).join(':') : '-' }}
                  </span>
                </div>
                <div class="meta-item">
                  <MapPinIcon class="icon-small" />
                  <span class="value">{{ event.room?.name || "-" }}</span>
                </div>
              </div>
            </div>

            <button @click="goToFeedback(event.id)" class="register-button">
              <ArrowRightCircleIcon class="icon-button" />
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
              class="event-details"
            >

              <div class="event-header-home">
                <h2>{{ event.name }}</h2>
                <div class="meta-grid">
                  <div class="meta-item">
                    <CalendarIcon class="icon-small" />
                    <span class="value">{{ formatDate(event.date) }}</span>
                  </div>
                  <div class="meta-item">
                    <ClockIcon class="icon-small" />
                    <span class="value">
                      {{ event.startTime ? event.startTime.split(':').slice(0, 2).join(':') : '-' }} –
                      {{ event.endTime ? event.endTime.split(':').slice(0, 2).join(':') : '-' }}
                    </span>
                  </div>
                  <div class="meta-item">
                    <MapPinIcon class="icon-small" />
                    <span class="value">{{ event.room?.name || "-" }}</span>
                  </div>
                </div>
              </div>

              <button @click="goToEvent(event.id)" class="register-button">
                <ArrowRightCircleIcon class="icon-button" />
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
import type { User } from "@/types/User";
import { Event } from "@/types/Event";
import { useRouter } from "vue-router";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import { faXmark, faCheck } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";
import { useAuth } from "@/util/auth";
import "@/assets/event-details.css";
import { 
  UserCircleIcon, 
  CalendarIcon, 
  ClockIcon, 
  MapPinIcon, 
  TagIcon, 
  ArrowRightCircleIcon 
} from '@heroicons/vue/24/outline'

const router = useRouter();
const auth = useAuth();

const registeredEvents = ref([]);
const recommendedEvents = ref([]);
const pendingFeedbackEvents = ref([]);
const pastEvents = ref([]);
const isLoading = ref(true);
const today = new Date().toISOString();

const searchQuery = ref("");

 /**
  * Filters the registered events based on the search query
  */
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

 /** 
  * Filters the recommended events based on the search query
  */
const filteredRecommendedEvents = computed(() =>
  recommendedEvents.value
    .filter((event) =>
      event.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
    )
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()),
);

/** 
  * Filters the feedback events based on the search query
  */
const filteredFeedbackEvents = computed(() =>
  pendingFeedbackEvents.value
    .filter((event) =>
      event.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
    )
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()),
);

/** 
 * Filters the past events based on the search query
 */
const filteredPastEvents = computed(() =>
  pastEvents.value
    .filter((event) =>
      event.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
    )
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()),
);

/** 
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

    const eventResponse = await api.get(`/users/${userId}/associatedEvents`)
    const events = await eventResponse.data;

    if (eventResponse.status !== 200) {
      showToast(
        new Toast("Fehler", `Events konnten nicht geladen werden.`, "error", faXmark, 5)
      );
    }

    registeredEvents.value = events.registeredEvents;
    recommendedEvents.value = events.recommendedEvents;
    pendingFeedbackEvents.value = events.confirmedEvents;
    pastEvents.value = events.completedEvents;

    for (const event of recommendedEvents.value) {
      event.tags = await fetchTagsForEvent(event.id);
    }

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

/**
 * Navigates to the feedback page for the given event.
 * If the user is not authenticated, they are redirected to the login page.
 * If the user is not eligible to give feedback, they are redirected to the home page.
 * If the user is eligible to give feedback, they are redirected to the feedback page.
 *
 * @param {string} eventId - The ID of the event.
 */
const goToFeedback = async (eventId: string) => {

  try {
    if (!useAuth().isAuthenticated.value) {
      router.push({
        name: 'login',
        query: { redirect: router.currentRoute.value.fullPath }
      });
      return;
    }

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
      params: { eventId: eventId.toString() },
      query: { token: token }
    });
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Zugriff auf Feedback-Formular", "error", faXmark, 5)
    );
  }
};

/**
 * Formats a timestamp into a human-readable date string.
 *
 * @param {string} timestamp - The date in milliseconds.
 * @returns {string} - The formatted date string in 'DD.MM.YYYY' format.
 */
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

.event-details {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem; 
  display: flex;
  flex-direction: column;
  gap: 0.75rem; 
  border: 1px solid #e2e8f0;
  min-height: 80px;
  max-height: fit-content;
  overflow: hidden;
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
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow: hidden;
  padding: 1rem;
}

.events-column h2 {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 1rem 0;
  color: #1e293b;
}

.registered-events-column {
  border-left: 4px solid #009ee2;
}

.recommended-events-column {
  border-left: 4px solid #00b300;
}

.feedback-past-column {
  border-left: 4px solid #ff0000;
}

.events-list {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.event-header-home {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.event-header-home h2 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #000;
  margin: 0;
  padding: 0;
  background: none;
  border: none;
}

.meta-grid {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.icon-small {
  width: 1rem;
  height: 1rem;
  color: #009ee2;
}

.register-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #002855;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  align-self: flex-end;
  margin-top: auto;
}

.empty-state-message {
  text-align: center;
  padding: 2rem;
  color: #94a3b8;
  background: #f8fafc;
  border-radius: 8px;
  border: 2px dashed #e2e8f0;
}

.past-events-section {
  margin-top: 2rem;
  border-top: 1px solid #e2e8f0;
  padding-top: 1rem;
}

.event-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  background: #f0f8ff;
  color: #006699;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  border: 1px solid #006699;
  font-size: 0.75rem;
  font-weight: 500;
}

@media (max-width: 1200px) {
  .events-columns-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}
</style>

