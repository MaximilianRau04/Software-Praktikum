<template>
  <div class="event-page">
    <div class="search-bar-container">
      <input
        type="text"
        v-model="searchTerm"
        placeholder="Suche nach Events..."
        class="search-bar"
      />
    </div>
    <div class="event-columns">
      <div class="event-column list-left">
        <h2>Event Liste</h2>
        <div v-if="filteredEvents.length === 0" class="empty-state">
          Keine Events gefunden
        </div>
        <div v-else>
          <div
            v-for="event in filteredEvents"
            :key="event.id"
            class="event-details"
          >
            <h3>
              <strong>{{ event.name }}</strong>
            </h3>
            <p>{{ event.description }}</p>
            <p>{{ formatDate(event.date) }}</p>
            <div class="tag-chips">
                <span
                v-for="(tag, index) in (event.tags || []).slice(0, 5)"
                :key="tag.id"
                class="chip"
              >
                {{ tag.name }}
              </span>
            </div>
            <button
              @click="showFeedbackSummary(event.id)"
              class="register-button"
            >
              Feedback anzeigen
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import { useRouter } from "vue-router";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

const props = defineProps({
  showEventListBox: Boolean,
});

const emit = defineEmits(["update:showEventListBox"]);

const events = ref([]);
const searchTerm = ref("");
const router = useRouter();

onMounted(async () => {
  await fetchEvents();
});

/**
 * Fetches all events from the API
 */
const fetchEvents = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/events`);
    if (!response.ok) {
      throw new Error("Fehler beim Laden der Events");
    }
    events.value = await response.json();

    for (const event of events.value) {
      event.tags = await fetchTagsForEvent(event.id);
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Laden der Events", "error"));
  }
};

/**
 * Fetches all tags for a specific event
 * @param {number} eventId The ID of the event
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

/**
 * Formats a date string into a human-readable format
 * @param {string} dateString The date string to format
 * @returns {string} The formatted date string
 */
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString("de-DE", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

/**
 * Filters events based on the search term
 */
const filteredEvents = computed(() => {
  return events.value.filter(
    (event) =>
      (event.name &&
        event.name.toLowerCase().includes(searchTerm.value.toLowerCase())) ||
      (event.description &&
        event.description
          .toLowerCase()
          .includes(searchTerm.value.toLowerCase()))
  );
});

const showFeedbackSummary = (eventId) => {
  router.push(`/feedback/${eventId}`);
};
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
  background-color: #EAEAEA;
  border: 1px solid #01172F;
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
  background-color: #009EE2;
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
