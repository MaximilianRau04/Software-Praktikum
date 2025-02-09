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
import { ref, onMounted, computed } from "vue";
import { showToast, Toast } from "@/types/toasts";
import { useRouter } from "vue-router";
import api from "@/util/api";
import { TagIcon } from "@heroicons/vue/24/outline";
import "@/assets/adminPanel/event-list.css";

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
      }),
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
      new Toast("Error", "Fehler beim Abrufen der ExchangeDays.", "error"),
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
      `/exchange-days/${selectedExchangeDay.value.id}/events`,
    );
    if (response.status === 200) {
      events.value = await response.data;

      await Promise.all(
        events.value.map(async (event) => {
          event.tags = await fetchTagsForEvent(event.id);
        }),
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
