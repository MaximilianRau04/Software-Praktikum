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
    <div class="input-group">
      <label for="exchangeDaySelect">ExchangeDay auswählen</label>
      <select
        id="exchangeDaySelect"
        v-model="selectedExchangeDay"
        @change="handleExchangeDayChange"
        class="exchange-day-select"
      >
        <option :value="null">Alle Events</option>
        <option
          v-for="exchangeDay in exchangeDays"
          :key="exchangeDay.id"
          :value="exchangeDay"
        >
          {{ exchangeDay.name }} ({{ formatDate(exchangeDay.startDate) }} -
          {{ formatDate(exchangeDay.endDate) }})
        </option>
      </select>
    </div>
    <div class="event-columns">
      <div class="event-column list-left">
        <h2>Event Liste</h2>
        <div v-if="filteredEvents.length" class="event-list">
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
            <div class="tags-container">
              <span
                v-if="event.tags && event.tags.length > 0"
                v-for="tag in event.tags.slice(0, 5)"
                :key="tag.id"
                class="tag-chip"
              >
                <TagIcon class="icon-tag" />
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
        <div v-else class="no-events">
          <p>Keine Events gefunden</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import { useRouter } from "vue-router";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";
import { TagIcon } from "@heroicons/vue/24/outline";

defineProps({
  showEventListBox: Boolean,
});

const emit = defineEmits(["update:showEventListBox"]);

const events = ref([]);
const searchTerm = ref("");
const router = useRouter();
const exchangeDays = ref([]);
const selectedExchangeDay = ref(null);
const isLoading = ref(false);

onMounted(async () => {
  await fetchExchangeDays();
  await fetchEvents();
});

/**
 * Fetches all events from the API.
 */
const fetchEvents = async () => {
  isLoading.value = true;
  try {
    const response = await api.get(`/events`);
    if (response.status !== 200) {
      throw new Error("Fehler beim Laden der Events");
    }
    events.value = await response.data;

    // Fetch tags for all events
    await Promise.all(
      events.value.map(async (event) => {
        event.tags = await fetchTagsForEvent(event.id);
      })
    );
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Laden der Events", "error"));
  } finally {
    isLoading.value = false;
  }
};

const fetchExchangeDays = async () => {
  try {
    const response = await api.get(`/exchange-days`);
    if (response.status === 200) {
      exchangeDays.value = await response.data;
    } else {
      throw new Error("Fehler beim Abrufen der ExchangeDays");
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Abrufen der ExchangeDays.", "error")
    );
  }
};

const handleExchangeDayChange = async () => {
  if (selectedExchangeDay.value) {
    await fetchEventsByExchangeDay();
  } else {
    await fetchEvents();
  }
};

/**
 * Fetches all events for the selected ExchangeDay.
 */
const fetchEventsByExchangeDay = async () => {
  isLoading.value = true;
  try {
    const response = await api.get(
      `/exchange-days/${selectedExchangeDay.value.id}/events`
    );
    if (response.status === 200) {
      events.value = await response.data;

      await Promise.all(
        events.value.map(async (event) => {
          event.tags = await fetchTagsForEvent(event.id);
        })
      );
    } else {
      throw new Error("Fehler beim Abrufen der Events für den ExchangeDay");
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Abrufen der Events.", "error"));
  } finally {
    isLoading.value = false;
  }
};

/**
 * Fetches the tags for a specific event.
 * @param eventId The ID of the event to fetch tags for.
 */
const fetchTagsForEvent = async (eventId: number): Promise<any[]> => {
  try {
    const res = await api.get(`/events/${eventId}/tags`);
    if (res.status !== 200) throw new Error("Failed to fetch tags");
    return await res.data;
  } catch (error) {
    console.error(`Error fetching tags for event ${eventId}:`, error);
    return [];
  }
};

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString("de-DE", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

/**
 * Filters the events based on the search term.
 */
const filteredEvents = computed(() => {
  return events.value.filter((event) => {
    const searchMatch =
      searchTerm.value === "" ||
      (event.name &&
        event.name.toLowerCase().includes(searchTerm.value.toLowerCase())) ||
      (event.description &&
        event.description
          .toLowerCase()
          .includes(searchTerm.value.toLowerCase()));

    return searchMatch;
  });
});

const showFeedbackSummary = (eventId: number): void => {
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
  max-width: 600px;
  margin: 10px auto;
  padding: 15px;
  background-color: #f1f5f9;
  border-radius: 8px;
  border: 1px solid #000000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-family: "Roboto", sans-serif;
  color: #000000;
}

h2 h3 p {
  margin: 0.5rem;
}

.register-button {
  background-color: #009ee2;
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

.tag-chip {
  background: linear-gradient(135deg, #f0f5ff, #e6eeff);
  border: 1px solid #d0ddf5;
  color: #2f5f9e;
  padding: 0.5rem 1.2rem;
  border-radius: 24px;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.tag-chip:hover {
  background: linear-gradient(135deg, #e6eeff, #d7e4ff);
  transform: translateY(-2px);
}

.tags-container {
  width: 60%;
}
</style>
