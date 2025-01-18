<template>
  <div class="updateEvent" v-if="isOrganizerRef">
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
      <div class="input-group">
              <label for="exchangeDaySelect">Exchange Day</label>
              <select
                id="exchangeDaySelect"
                v-model="exchangeDaySelect"
                @change="updateSelectedExchangeDay"
                required
              >
                <option value="" disabled selected>
                  Bitte wählen Sie einen Exchange Day aus
                </option>
                <option
                  v-for="exchangeDay in exchangeDays"
                  :key="exchangeDay.id"
                  :value="exchangeDay.id"
                >
                  {{ exchangeDay.name }}
                </option>
              </select>
            </div>
      <div class="input-group">
              <label for="date">Datum</label>
              <input
                type="date"
                id="date"
                v-model="date"
                :min="selectedExchangeDay?.startDate || ''"
                :max="selectedExchangeDay?.endDate || ''"
                :disabled="!exchangeDaySelect"
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
      <div class="button-group">
        <button type="button" class="back-button" @click="goBack">
          Zurück
        </button>
        <button type="submit" class="submit-button">Speichern</button>
      </div>
    </form>
  </div>
  <div v-else>
    <h1>Du bist nicht berechtigt, dieses Event zu bearbeiten.</h1>
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

const selectedExchangeDay = computed(() => {
  return exchangeDays.value.find(day => day.id === exchangeDaySelect.value) || null;
});

const event = ref({
  exchangeDayId: "",
  organizerId: Number(userId),
  name: "",
  startTime: "",
  endTime: "",
  room: "",
  description: "",
  date: "",
});

const exchangeDays = ref([]);
const registeredUsers = ref([]);
const isOrganizerRef = ref(false);

const isOrganizer = async () => {
  console.log(userId);
  const eventId = route.params.eventId;
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/events/${eventId}/organizer`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      },
    );

    if (!response.ok) {
      console.error("Failed to fetch event organizer.");
      return false;
    }

    const data = await response.json();
    isOrganizerRef.value = data.id === parseInt(userId, 10); 
  } catch (error) {
    console.error("Error checking if user is organizer:", error);
    isOrganizerRef.value = false;
  }
};

/**
 * Fetches the details of the event based on the eventId from the URL parameters
 * Sets the event data and associated exchange day and organizer information
 */
const fetchEventDetails = async () => {
  const eventId = route.params.eventId;
  try {
    const response = await fetch(`${config.apiBaseUrl}/events/${eventId}`);
    const eventData = await response.json();
    event.value = eventData;

    const organizerResponse = await fetch(
      `${config.apiBaseUrl}/events/${eventId}/organizer`,
    );
    const organizerData = await organizerResponse.json();
    event.value.organizerId = organizerData.id;

    const exchangeDayResponse = await fetch(
      `${config.apiBaseUrl}/events/${eventId}/exchange-day`,
    );
    const exchangeDayData = await exchangeDayResponse.json();
    event.value.exchangeDayId = exchangeDayData.id;
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
 * Updates the selected exchange day.
 */
 const updateSelectedExchangeDay = () => {
  const selectedDay = exchangeDays.value.find(day => day.id === exchangeDaySelect.value);
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

/**
 * Navigates the user back to the previous page.
 */
const goBack = () => {
  router.back();
};

onMounted(() => {
  fetchExchangeDays();
  fetchEventDetails();
  fetchRegisteredUsers();
  isOrganizer();
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
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
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
</style>
