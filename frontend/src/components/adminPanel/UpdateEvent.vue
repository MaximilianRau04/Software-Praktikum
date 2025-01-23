<template>
  <div class="updateEvent">
    <h1>Event bearbeiten</h1>
    <form @submit.prevent="updateEvent">
      <div class="form-group">
        <label for="name">Event Name:</label>
        <input
          type="text"
          id="name"
          v-model="event.name"
          required
          minlength="3"
          maxlength="100"
        />
      </div>
      <div class="form-group">
        <label for="description">Beschreibung:</label>
        <textarea
          id="description"
          v-model="event.description"
          maxlength="255"
        ></textarea>
      </div>
      <div class="form-group">
        <label for="exchangeDaySelect">Exchange Day</label>
        <select id="exchangeDaySelect" v-model="event.exchangeDayId" required>
<<<<<<< Updated upstream:frontend/src/components/adminPanel/UpdateEvent.vue
          <option
            v-for="exchangeDay in exchangeDays"
            :key="exchangeDay.id"
            :value="exchangeDay.id"
          >
=======
          <option v-for="exchangeDay in exchangeDays" :key="exchangeDay.id" :value="exchangeDay.id">
>>>>>>> Stashed changes:frontend/src/components/createNewEvents/UpdateEvent.vue
            {{ exchangeDay.name }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <label for="date">Datum</label>
        <input
          type="date"
          id="date"
          v-model="event.date"
          :min="selectedExchangeDay?.startDate || ''"
          :max="selectedExchangeDay?.endDate || ''"
          :disabled="!event.exchangeDayId"
          required
        />
      </div>
      <div class="form-group">
        <label for="room">Raum:</label>
        <input type="text" id="room" v-model="event.room" maxlength="50" />
      </div>
      <div class="form-group">
        <label for="startTime">Startzeit:</label>
        <input type="time" id="startTime" v-model="event.startTime" required />
      </div>
      <div class="form-group">
        <label for="endTime">Endzeit:</label>
        <input type="time" id="endTime" v-model="event.endTime" required />
      </div>
      <div class="form-group">
        <label for="organizer">Organizer:</label>
        <select id="organizer" v-model="event.organizerId">
          <option
            v-for="user in registeredUsers"
            :key="user.id"
            :value="user.id"
          >
            {{ user.firstname }} {{ user.lastname }}
          </option>
        </select>
      </div>

      <div class="form-group">
        <label for="experienceLevel">Erfahrungslevel</label>
        <select
          id="experienceLevel"
          v-model="event.recommendedExperience"
          required
        >
          <option value="" disabled selected>
            Bitte wählen Sie ein Erfahrungslevel
          </option>
          <option v-for="level in experienceLevels" :key="level" :value="level">
            {{ germanExperienceLevels[level] }}
          </option>
        </select>
      </div>

      <div class="form-group">
        <label for="tags">Event Tags</label>
        <p>Bitte wählen Sie bis zu 5 Event Tags für Ihr Event aus:</p>

        <!-- Tag input field with autocomplete -->
        <input
          type="text"
          id="tags"
          v-model="tagInput"
          placeholder="Tag eingeben und Komma drücken"
          @input="filterTags"
          @keyup="handleKeyup"
          :disabled="selectedTags.length >= 5"
        />

        <!-- Display selected tags as chips -->
        <div class="tag-chips">
          <span v-for="(tag, index) in selectedTags" :key="index" class="chip">
            {{ tag }}
            <button type="button" class="remove-tag" @click="removeTag(index)">
              &times;
            </button>
          </span>
        </div>

        <!-- Display filtered tag list -->
        <div class="tag-list">
          <button
            v-for="tag in filteredTags"
            :key="tag"
            type="button"
            @click="addTag(tag)"
            :disabled="selectedTags.includes(tag)"
          >
            {{ tag }}
          </button>
        </div>
      </div>
      <div class="button-group">
        <button type="button" class="back-button" @click="goBack">
          Zurück
        </button>
        <button type="button" class="delete-button" @click="deleteEvent">
          Event löschen
        </button>
        <button type="submit" class="submit-button">Speichern</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import Cookies from "js-cookie";
import config from "../../config";

const date = ref("");
const router = useRouter();
const route = useRoute();
const userId = Cookies.get("userId");
const exchangeDayId = ref("");
const exchangeDaySelect = ref("");
const exchangeDays = ref([]);
const registeredUsers = ref([]);

const experienceLevels = ref([]);
const germanExperienceLevels = {
  ALL_LEVELS: "Alle Ebenen",
  JUNIOR: "Junior",
  SENIOR: "Senior",
  EXPERT: "Experte",
};

const selectedTags = ref([]);
const tagInput = ref("");
const allTags = ref([]);
const filteredTags = ref([]);

//Computed property to find selected exchange day
const selectedExchangeDay = computed(() => {
  return (
    exchangeDays.value.find((day) => day.id === event.value.exchangeDayId) ||
    null
  );
});

// Event data
const event = ref({
  exchangeDayId: "",
  organizerId: Number(userId),
  name: "",
  startTime: "",
  endTime: "",
  room: "",
  description: "",
  date: "",
  recommendedExperience: "",
});

/**
 * Deletes an event after confirmation.
 * @param {number} eventId - The ID of the event.
 */
const deleteEvent = async () => {
  const eventId = route.params.eventId;
  if (!confirm("Are you sure you want to delete this event?")) {
    return;
  }
  try {
    const response = await fetch(`${config.apiBaseUrl}/events/${eventId}`, {
      method: "DELETE",
    });

    if (response.status === 404) {
      alert("Event not found.");
      throw new Error(response.statusText);
    }

    router.push({ name: "home" });
    alert(`Event with ID ${eventId} has been successfully deleted.`);
  } catch (error) {
    console.error("Error deleting event:", error);
    alert("Deleting event failed. Please try again.");
  }
};

/**
 * Fetches the details of the event based on the eventId from the URL parameters.
 * Sets the event data and associated exchange day and organizer information.
 */
const fetchEventDetails = async () => {
  const eventId = route.params.eventId;
  try {
    const response = await fetch(`${config.apiBaseUrl}/events/${eventId}`);
    const eventData = await response.json();
    event.value = { ...event.value, ...eventData };

    const organizerResponse = await fetch(
      `${config.apiBaseUrl}/events/${eventId}/organizer`,
    );
    const organizerData = await organizerResponse.json();
    event.value.organizerId = organizerData.id;
  } catch (error) {
    console.error("Error fetching event details:", error);
    alert("Das Event konnte nicht geladen werden.");
  }
};

/**
 * Fetches the list of registered users for the event.
 * Ensures the organizerId is set to a valid user if not present in the list.
 */
const fetchRegisteredUsers = async () => {
  const eventId = route.params.eventId;
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/events/${eventId}/registeredUsers`,
    );
    const users = await response.json();
    registeredUsers.value = users;

    if (!users.some((user) => user.id === event.value.organizerId)) {
      event.value.organizerId = users[0]?.id;
    }
  } catch (error) {
    console.error("Error fetching registered users:", error);
    alert("Die registrierten Nutzer konnten nicht geladen werden.");
  }
};

/**
 * Fetches the list of exchange days from the server.
 */
const fetchExchangeDays = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/exchange-days`);
    if (!response.ok) {
      throw new Error("Failed to fetch exchange days");
    }
    exchangeDays.value = await response.json();
  } catch (error) {
    console.error("Error fetching exchange days:", error);
    alert("Die Exchange Days konnten nicht geladen werden.");
  }
};

/**
 * Fetches the list of experience levels from the server.
 */
const fetchExperienceLevels = async () => {
  try {
    const levelsResponse = await fetch(
      `${config.apiBaseUrl}/events/experience-levels`,
    );
    if (!levelsResponse.ok) {
      throw new Error("Fehler beim Laden der Erfahrungslevel.");
    }

    experienceLevels.value = await levelsResponse.json();
  } catch (error) {
    console.error(error.message, error);
  }
};

/**
 * Fetches the tags associated with the event.
 */
const fetchEventTags = async () => {
  try {
    const eventId = route.params.eventId;
    const tagsResponse = await fetch(
      `${config.apiBaseUrl}/events/${eventId}/tags`,
    );
    if (!tagsResponse.ok) {
      throw new Error("Fehler beim Laden der Event Tags.");
    }
    selectedTags.value = await tagsResponse.json();
    selectedTags.value = selectedTags.value.map((tag) => tag.name);
  } catch (error) {
    console.error(error.message, error);
  }
};

/**
 * Updates the selected exchange day.
 */
const updateSelectedExchangeDay = () => {
  const selectedDay = exchangeDays.value.find(
    (day) => day.id === exchangeDaySelect.value,
  );
  if (selectedDay) {
    date.value = "";
  }
};

/**
 * Updates the event data on the server.
 * Sends a PUT request with the modified event details.
 */
const updateEvent = async () => {
  try {
    const eventId = route.params.eventId;
    const cleanEventData = {
      exchangeDayId: event.value.exchangeDayId,
      organizerId: event.value.organizerId,
      name: event.value.name,
      description: event.value.description,
      date: event.value.date,
      room: event.value.room,
      startTime: event.value.startTime,
      endTime: event.value.endTime,
      recommendedExperience: event.value.recommendedExperience,
      tags: selectedTags.value,
    };

    const response = await fetch(`${config.apiBaseUrl}/events/${eventId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(cleanEventData),
    });

    if (!response.ok) {
      throw new Error("Failed to update event");
    }
    alert("Das Event wurde erfolgreich aktualisiert!");
  } catch (error) {
    console.error("Error updating event:", error);
    alert("Das Event konnte nicht aktualisiert werden.");
  }
};

// Fetch tags from the server
const fetchGlobalTags = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/tags`);
    if (response.ok) {
      const tags = await response.json();
      allTags.value = tags.map((tag) => tag.name);
      filteredTags.value = [...allTags.value];
    } else {
      console.error("Fehler beim Abrufen der Tags.");
    }
  } catch (error) {
    console.error("Fehler beim Abrufen der Tags:", error);
  }
};

// Filter tags based on the input
const filterTags = () => {
  const query = tagInput.value.toLowerCase().trim();
  filteredTags.value = allTags.value.filter((tag) =>
    tag.toLowerCase().includes(query),
  );
};

// Handle keyup event to add a tag when comma is pressed
const handleKeyup = (event) => {
  if (event.key === ",") {
    addTagFromInput();
  }
};

// Add a tag from the input
const addTagFromInput = () => {
  const trimmedInput = tagInput.value.trim().slice(0, -1); // Remove the trailing comma
  if (
    trimmedInput &&
    !selectedTags.value.includes(trimmedInput) &&
    selectedTags.value.length < 5
  ) {
    selectedTags.value.push(trimmedInput);
  }
  tagInput.value = "";
  filteredTags.value = [...allTags.value];
};

// Add a tag from the list
const addTag = (tag) => {
  if (!selectedTags.value.includes(tag) && selectedTags.value.length < 5) {
    selectedTags.value.push(tag);
  }
};

// Remove a tag
const removeTag = (index) => {
  selectedTags.value.splice(index, 1);
};

/**
 * Navigates the user back to the previous page.
 */
const goBack = () => {
  router.back();
};

onMounted(async () => {
  await fetchExchangeDays();
  await fetchEventDetails();
  await fetchRegisteredUsers();
  await fetchExperienceLevels();
  await fetchGlobalTags();
  await fetchEventTags();
});

watch(registeredUsers, (users) => {
  if (
    users.length > 0 &&
    !users.some((user) => user.id === event.value.organizerId)
  ) {
    event.value.organizerId = users[0].id;
  }
});
</script>

<style scoped>
.updateEvent {
  max-width: 600px;
  margin: 40px auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  font-size: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input,
textarea,
select {
  margin-right: 10px;
  width: calc(100% - 15px);

  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  resize: none;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.back-button {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.back-button:hover {
  background-color: #0056b3;
}

.submit-button {
  background-color: #28a745;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.submit-button:hover {
  background-color: #218838;
}

.delete-button {
  background-color: #dc3545;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.delete-button:hover {
  background-color: #c82333;
}
</style>
