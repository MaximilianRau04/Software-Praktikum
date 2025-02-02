<template>
  <div class="create-box">
    <h2 class="login-header">Event bearbeiten</h2>
    <form @submit.prevent="updateEvent">
      <div class="input-group">
        <label for="exchangeDaySelect">ExchangeDay auswählen</label>
        <select id="exchangeDaySelect" v-model="selectedExchangeDay" @change="fetchEventsByExchangeDay" required>
          <option value="" disabled>Wähle einen ExchangeDay</option>
          <option v-for="exchangeDay in exchangeDays" :key="exchangeDay" :value="exchangeDay">
            {{ exchangeDay.name }} ({{ formatDate(exchangeDay.startDate) }} -
            {{ formatDate(exchangeDay.endDate) }})
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="eventSelect">Event auswählen</label>
        <select id="eventSelect" v-model="selectedEvent" @change="fetchEventDetails" :disabled="!selectedExchangeDay"
          required>
          <option value="" disabled>Wähle ein Event</option>
          <option v-for="event in events" :key="event.id" :value="event.id">
            {{ event.name }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="eventName">Name</label>
        <input type="text" id="eventName" v-model="name" required />
      </div>

      <div class="input-group">
        <label for="eventDescription">Beschreibung</label>
        <input type="text" id="eventDescription" v-model="description" />
      </div>

      <div class="input-group">
        <label for="eventDate">Datum</label>
        <input type="date" id="eventDate" v-model="date" :min="selectedExchangeDay?.startDate || ''"
          :max="selectedExchangeDay?.endDate || ''" required />
      </div>

      <div class="input-group">
        <label for="startTime">Startzeit</label>
        <input type="time" id="startTime" v-model="startTime" required />
      </div>

      <div class="input-group">
        <label for="endTime">Endzeit</label>
        <input type="time" id="endTime" v-model="endTime" required />
      </div>

      <div class="input-group">
        <label for="room">Raum</label>
        <select id="room" v-model="room" :disabled="!filteredRooms.length">
          <option value="" disabled>Bitte wählen Sie einen Raum</option>
          <option v-for="availableRoom in filteredRooms" :key="availableRoom.id" :value="availableRoom.id">
            {{ availableRoom.name }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="recommendedExperience">Empfohlenes Erfahrungslevel</label>
        <select id="recommendedExperience" v-model="recommendedExperience" required>
          <option value="" disabled>Wähle ein Erfahrungslevel</option>
          <option v-for="level in experienceLevels" :key="level" :value="level">
            {{ level }}
          </option>
        </select>
      </div>

      <!-- event tags -->
      <div class="input-group">
        <label for="tags">Event Tags</label>
        <p>Bitte wählen Sie bis zu 5 Event Tags für Ihr Event aus:</p>

        <TagInput v-if="selectedTags && allTags" v-model="selectedTags" :available-tags="allTags" :tagSelect="false"
          @new-tag="handleNewTag" />

        <div class="button-group">
          <button type="submit" class="update-button">
            Event aktualisieren
          </button>
          <button type="button" class="delete-button" @click="deleteEvent" :disabled="!selectedEvent">
            Event löschen
          </button>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import api from "@/util/api";
import TagInput from "../profile/TagInput.vue";
import { Tag } from "@/types/Tag";

const emit = defineEmits(["update:selectEventToUpdate"]);

const selectedExchangeDay = ref<any | null>(null);
const selectedEvent = ref<number | null>(null);
const name = ref("");
const description = ref("");
const date = ref("");
const startTime = ref("00:00");
const endTime = ref("01:00");
const recommendedExperience = ref("");
const exchangeDays = ref<any[]>([]);
const events = ref<any[]>([]);
const filteredRooms = ref<any[]>([]);
const experienceLevels = ref<string[]>([]);
const room = ref<any>(null);
const selectedTags = ref<Tag[]>([]);
const exchangeDayId = ref<number | null>(null);
const allTags = ref<Tag[]>([]);
const organizerId = ref<number | null>(null);

/**
 * Fetches the available exchange days from the API.
 */
const fetchExchangeDays = async () => {
  try {
    const response = await api.get(`/exchange-days`);
    if (response.status === 200) {
      const data = await response.data;
      exchangeDays.value = data;
    } else {
      showToast(
        new Toast("Error", "Fehler beim Abrufen der ExchangeDays.", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Abrufen der ExchangeDays.", "error"),
    );
  }
};

/**
 * Fetches events for a selected exchange day.
 */
const fetchEventsByExchangeDay = async () => {
  if (!selectedExchangeDay.value) {
    events.value = [];
    filteredRooms.value = [];
    return;
  }

  try {
    const response = await api.get(
      `/exchange-days/${selectedExchangeDay.value.id}/events`,
    );
    if (response.status === 200) {
      const data = await response.data;
      events.value = data;
      await fetchRoomsForEvent();
    } else {
      showToast(new Toast("Error", "Fehler beim Abrufen der Events.", "error"));
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Abrufen der Events.", "error"));
  }
};

/**
 * Fetches details of the selected event.
 */
const fetchEventDetails = async () => {
  if (!selectedEvent.value) return;

  try {
    const response = await api.get(`/events/${selectedEvent.value}`);
    if (response.status === 200) {
      const data = await response.data;
      name.value = data.name;
      description.value = data.description;
      date.value = data.date;
      startTime.value = data.startTime;
      endTime.value = data.endTime;
      room.value = data.room.id;
      recommendedExperience.value = data.recommendedExperience;
      exchangeDayId.value = data.exchangeDayId;

      await fetchOrganizerId();
      await fetchEventTags();
      await fetchRoomsForEvent();
    } else {
      showToast(
        new Toast("Error", "Fehler beim Abrufen der Eventdetails.", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Abrufen der Eventdetails.", "error"),
    );
  }
};

/**
 * Fetches the organizer ID for the selected event.
 */
const fetchOrganizerId = async () => {
  try {
    const response = await api.get(
      `/events/${selectedEvent.value}/organizer`,
    );
    if (response.status === 200) {
      const data = await response.data;
      organizerId.value = data.id;
    }
  } catch (error) {

  }
};

/**
 * Fetches rooms for the selected event based on the exchange day location.
 */
const fetchRoomsForEvent = async () => {
  if (!selectedExchangeDay.value || !selectedExchangeDay.value.location) {
    filteredRooms.value = [];
    return;
  }

  const exchangeDayLocationId = selectedExchangeDay.value.location.id;
  if (!exchangeDayLocationId) {
    showToast(
      new Toast(
        "Error",
        "Keine gültige Location für den ExchangeDay gefunden.",
        "error",
      ),
    );
    filteredRooms.value = [];
    return;
  }

  try {
    const response = await api.get(
      `/resources/location/${exchangeDayLocationId}`,
    );
    if (response.status === 200) {
      const data = await response.data;
      filteredRooms.value = data;
    } else {
      showToast(new Toast("Error", "Fehler beim Abrufen der Räume.", "error"));
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Abrufen der Räume.", "error"));
  }
};

/**
 * Updates the available rooms based on the location of the selected Exchange Day.
 */
const updateFilteredRooms = async () => {
  if (selectedExchangeDay.value) {
    const exchangeDayLocationId = selectedExchangeDay.value.location.id;

    try {
      const response = await api.get(
        `/resources/location/${exchangeDayLocationId}`,
      );

      if (response.status === 200) {
        const rooms = await response.data;
        filteredRooms.value = rooms;
      }
    } catch (error) {
      showToast(
        new Toast(
          "Error",
          "Fehler beim Laden der Ressourcen für die Location.",
          "error",
        ),
      );
      filteredRooms.value = [];
    }
  } else {
    filteredRooms.value = [];
  }
};

/**
 * Fetches the available experience levels for events.
 */
const fetchExperienceLevels = async () => {
  try {
    const response = await api.get(`/events/experience-levels`);
    if (response.status === 200) {
      const data = await response.data;
      experienceLevels.value = data;
    }
  } catch (error) {

  }
};

/**
 * Updates the selected event with new details.
 */
const updateEvent = async () => {
  if (!selectedEvent.value) {
    showToast(new Toast("Warning", "Kein Event ausgewählt.", "warning"));
    return;
  }

  const updatedEvent = {
    name: name.value,
    description: description.value,
    date: date.value,
    startTime: startTime.value,
    endTime: endTime.value,
    roomId: room.value,
    recommendedExperience: recommendedExperience.value,
    tags: selectedTags.value.map(tag => tag.name),
    organizerId: organizerId.value,
    exchangeDayId: exchangeDayId.value,
  };

  try {
    const response = await api.put(`/events/${selectedEvent.value}`, updatedEvent);

    if (response.status === 200) {
      showToast(
        new Toast("Success", "Event erfolgreich aktualisiert.", "success"),
      );
    } else {
      showToast(
        new Toast("Error", "Fehler beim Aktualisieren des Events.", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Aktualisieren des Events.", "error"),
    );
  }
};

/**
 * Deletes the selected event from the API.
 */
const deleteEvent = async () => {
  if (!selectedEvent.value) {
    showToast(new Toast("Warning", "Kein Event ausgewählt.", "warning"));
    return;
  }

  if (confirm("Möchten Sie dieses Event wirklich löschen?")) {
    try {
      const response = await api.delete(`/events/${selectedEvent.value}`);

      if (response.status === 204) {
        showToast(
          new Toast("Success", "Event erfolgreich gelöscht.", "success"),
        );
        selectedEvent.value = null;
        name.value = "";
        description.value = "";
        date.value = "";
        startTime.value = "00:00";
        endTime.value = "01:00";
        room.value = "";
        recommendedExperience.value = "";
      } else {
        showToast(
          new Toast("Error", "Fehler beim Löschen des Events.", "error"),
        );
      }
    } catch (error) {
      showToast(new Toast("Error", "Fehler beim Löschen des Events.", "error"));
    }
  }
};

/**
 * Fetches all available tags from the API.
 */
const fetchTags = async () => {
  try {
    const response = await api.get(`/tags`);
    if (response.status === 200) {
      const data = await response.data;
      allTags.value = data;
    } else {
    }
  } catch (error) {
  }
};

/**
 * Fetches the tags associated with the selected event.
 */
const fetchEventTags = async () => {
  if (!selectedEvent.value) return;

  try {
    const response = await api.get(`/events/${selectedEvent.value}/tags`);
    if (response.status === 200) {
      const data = await response.data;
      selectedTags.value = data;
    } else {
    }
  } catch (error) {
  }
};

const handleNewTag = (newTag) => {
  if (newTag.key === 'Enter') {
    newTag.preventDefault();
  }
  api.post(`/tags`, newTag)
    .then(response => {
      allTags.value.push(response.data);
    })
};

/**
 * Formats a timestamp into a human-readable date string.
 */
function formatDate(timestamp: number): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE");
}

onMounted(() => {
  fetchExchangeDays();
  fetchExperienceLevels();
  fetchTags();
});
</script>

<style scoped>
.button-group {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.update-button {
  background-color: #009ee2;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 4px;
}

.delete-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 4px;
}

.delete-button:disabled {
  background-color: #e0e0e0;
  color: #6c757d;
  cursor: not-allowed;
}

.tag-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.chip {
  background-color: #e0e0e0;
  padding: 5px 10px;
  border-radius: 15px;
  display: flex;
  align-items: center;
}

.remove-tag {
  background: none;
  border: none;
  color: red;
  cursor: pointer;
  margin-left: 5px;
}

.tag-list button {
  background-color: #f0f0f0;
  border: none;
  padding: 5px 10px;
  margin: 5px;
  border-radius: 5px;
  cursor: pointer;
}

.tag-list button:disabled {
  background-color: #ccc;
}

.tag-list {
  margin-bottom: 20px;
}
</style>
