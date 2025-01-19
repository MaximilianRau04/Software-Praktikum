<!--
  This component allows for the planning and creation of new events.
  It provides an user interface to input and save event details.
  After filling out the form inputs, the user can save the event, which will then be displayed in the application.
-->
<template>
  <div class="container">
    <div class="events">
      <button @click="toggleWorkshopBox" class="action-button" type="button">
        <img
          src="@/images/plus.png"
          alt="Plus"
          class="plus-icon"
          width="35"
          height="35"
        />
        Neuer Workshop
      </button>
      <button @click="toggleExchangeDayBox" class="action-button" type="button">
        <img
          src="@/images/plus.png"
          alt="Plus"
          class="plus-icon"
          width="35"
          height="35"
        />
        Neuer Exchange Day
      </button>

      <!-- Workshop form -->
      <transition name="roll">
        <div v-if="showWorkshopBox" class="create-box">
          <h2 class="login-header">Neuer Workshop</h2>
          <form @submit.prevent="createWorkshop">
            <div class="input-group">
              <label for="name">Name</label>
              <input type="text" id="name" v-model="name" required />
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
              <input type="text" id="room" v-model="room" />
            </div>
            <div class="input-group">
              <label for="description">Beschreibung</label>
              <input type="text" id="description" v-model="description" />
            </div>
            <button type="submit" class="login-button">
              Workshop erstellen
            </button>
          </form>
        </div>
      </transition>

      <!-- Exchange Day form -->
      <transition name="roll">
        <div v-if="showExchangeDay" class="create-box">
          <h2 class="login-header">Neuer Exchange Day</h2>
          <form @submit.prevent="createExchangeDay">
            <div class="input-group">
              <label for="exchangeName">Name</label>
              <input
                type="text"
                id="exchangeName"
                v-model="exchangeName"
                required
              />
            </div>
            <div class="input-group">
              <label for="startDate">Startdatum</label>
              <input type="date" id="startDate" v-model="startDate" required />
            </div>
            <div class="input-group">
              <label for="endDate">Enddatum</label>
              <input type="date" id="endDate" v-model="endDate" required />
            </div>
            <div class="input-group">
              <label for="location">Ort</label>
              <input type="text" id="location" v-model="location" />
            </div>
            <div class="input-group">
              <label for="exchangeDescription">Beschreibung</label>
              <input
                type="text"
                id="exchangeDescription"
                v-model="exchangeDescription"
              />
            </div>
            <button type="submit" class="login-button">
              Exchange Day erstellen
            </button>
          </form>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import "@/assets/event-planning.css";
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import config from "@/config";
import Cookies from "js-cookie";

const showWorkshopBox = ref(false);
const showExchangeDay = ref(false);

// Workshop form data
const name = ref("");
const description = ref("");
const date = ref("");
const startTime = ref(new Date().toISOString().slice(11, 16));
const endTime = ref("");
const room = ref("");
const exchangeDaySelect = ref("");
const userId = Cookies.get("userId");

const exchangeDays = ref([]);
const users = ref([]);
const selectedExchangeDay = computed(() => {
  return (
    exchangeDays.value.find((day) => day.id === exchangeDaySelect.value) || null
  );
});

// Exchange Day form data
const exchangeName = ref("");
const exchangeDescription = ref("");
const startDate = ref(new Date().toISOString().slice(0, 10));
const endDate = ref(new Date().toISOString().slice(0, 10));
const location = ref("");

const apiUrl = `${config.apiBaseUrl}/events`;
const exchangeApiUrl = `${config.apiBaseUrl}/exchange-days`;

const router = useRouter();

/**
 * Initializes the start time of the Workshop form.
 */
const initializeStartTime = () => {
  const now = new Date();

  const formatter = new Intl.DateTimeFormat("de-DE", {
    timeZone: "Europe/Berlin",
    hour: "2-digit",
    minute: "2-digit",
    hourCycle: "h23",
  });

  const formattedTime = formatter.format(now);
  startTime.value = formattedTime;
};

/**
 * Fetches all Exchange Days and users from the API when the component is mounted.
 */
onMounted(async () => {
  initializeStartTime();
  fetchData();
});

const fetchData = async () => {
  try {
    const [exchangeDaysResponse, usersResponse] = await Promise.all([
      fetch(`${config.apiBaseUrl}/exchange-days`),
      fetch(`${config.apiBaseUrl}/users`),
    ]);
    if (!exchangeDaysResponse.ok) {
      throw new Error("Fehler beim Laden der Exchange Days");
    }
    if (!usersResponse.ok) {
      throw new Error("Fehler beim Laden der Benutzer");
    }

    exchangeDays.value = await exchangeDaysResponse.json();
    users.value = await usersResponse.json();
  } catch (error) {
    console.error(error.message, error);
  }
};

fetchData();

/**
 * Toggles the visibility of the Workshop form.
 */
const toggleWorkshopBox = () => {
  showWorkshopBox.value = !showWorkshopBox.value;
  if (showWorkshopBox.value) showExchangeDay.value = false;
};

/**
 * Toggles the visibility of the Exchange Day form.
 */
const toggleExchangeDayBox = () => {
  showExchangeDay.value = !showExchangeDay.value;
  if (showExchangeDay.value) showWorkshopBox.value = false;
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
 * Creates a new Workshop using the form data.
 */
const createWorkshop = async () => {
  if (!selectedExchangeDay.value) {
    alert("Bitte wählen Sie einen gültigen Exchange Day aus.");
    return;
  }

  const selectedDate = new Date(date.value);
  const start = new Date(selectedExchangeDay.value.startDate);
  const end = new Date(selectedExchangeDay.value.endDate);

  if (selectedDate < start || selectedDate > end) {
    alert(
      "Das Datum muss zwischen dem Start- und Enddatum des ausgewählten Exchange Days liegen.",
    );
    return;
  }

  try {
    const response = await fetch(apiUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: name.value,
        date: date.value,
        startTime: startTime.value,
        endTime: endTime.value,
        room: room.value,
        description: description.value,
        exchangeDayId: exchangeDaySelect.value,
        organizerId: userId,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      console.log("Workshop erstellt:", data);
      alert(`Workshop erstellt: ${data.name}`);
      resetWorkshopForm();
      fetchData();
    } else {
      alert("Fehler beim Erstellen des Workshops.");
    }
  } catch (error) {
    console.error("Fehler beim Erstellen des Workshops:", error);
    alert("Fehler beim Erstellen des Workshops.");
  }
};

/**
 * Creates a new Exchange Day using the form data.
 */
const createExchangeDay = async () => {
  try {
    const response = await fetch(exchangeApiUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: exchangeName.value,
        startDate: startDate.value,
        endDate: endDate.value,
        location: location.value,
        description: exchangeDescription.value,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      console.log("Exchange Day erstellt:", data);
      alert(`Exchange Day erstellt: ${data.name}`);
      resetExchangeDayForm();
      fetchData();
    } else {
      alert("Fehler beim Erstellen des Exchange Days.");
    }
  } catch (error) {
    console.error("Fehler beim Erstellen des Exchange Days:", error);
    alert("Fehler beim Erstellen des Exchange Days.");
  }
};

/**
 * Resets the Workshop form fields.
 */
const resetWorkshopForm = () => {
  name.value = "";
  date.value = "";
  startTime.value = "";
  endTime.value = "";
  room.value = "";
  description.value = "";
  exchangeDaySelect.value = "";
  showWorkshopBox.value = false;
};

/**
 * Resets the Exchange Day form fields.
 */
const resetExchangeDayForm = () => {
  exchangeName.value = "";
  exchangeDescription.value = "";
  startDate.value = "";
  endDate.value = "";
  location.value = "";
  showExchangeDay.value = false;
};
</script>
