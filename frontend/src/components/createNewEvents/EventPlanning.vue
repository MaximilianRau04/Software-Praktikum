<!--
  This component allows for the planning and creation of new events.
  It provides an user interface to input and save event details.
  After filling out the form inputs, the user can save the event, which will then be displayed in the application.
-->
<template>
  <div class="container">
    <div class="events">
      <button @click="toggleWorkshopBox" class="action-button" type="button">
        <img src="@/images/plus.png" alt="Plus" class="plus-icon" width="35" height="35" /> New Workshop
      </button>
      <button @click="toggleExchangeDayBox" class="action-button" type="button">
        <img src="@/images/plus.png" alt="Plus" class="plus-icon" width="35" height="35" /> Neuer Exchange Day
      </button>

      <transition name="roll">
        <div v-if="showWorkshopBox" class="create-box">
          <h2 class="login-header">Neuer Workshop</h2>
          <form @submit.prevent="createWorkshop">
            <div class="input-group">
              <label for="name">Name</label>
              <input type="text" id="name" v-model="name" required />
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
              <input type="text" id="room" v-model="room" required />
            </div>
            <div class="input-group">
              <label for="description">Beschreibung</label>
              <input type="text" id="description" v-model="description" required />
            </div>
            <div class="input-group">
              <label for="exchangeDaySelect">Exchange Day</label>
              <select id="exchangeDaySelect" v-model="exchangeDaySelect" required>
                <option value="" disabled selected>Bitte wählen Sie einen Exchange Day aus</option>
                <option v-for="exchangeDay in exchangeDays" :key="exchangeDay.id" :value="exchangeDay.id">
                  {{ exchangeDay.name }}
                </option>
              </select>
            </div>
            <div class="input-group">
              <label for="organizerSelect">Veranstalter</label>
              <select id="organizerSelect" v-model="organizerSelect" required>
                <option value="" disabled selected>Bitte wählen Sie einen Veranstalter aus</option>
                <option v-for="user in users" :key="user.id" :value="user.id">
                  {{ user.username }}
                </option>
              </select>
            </div>
            <button type="submit" class="login-button">Workshop erstellen</button>
          </form>
        </div>
      </transition>

      <transition name="roll">
        <div v-if="showExchangeDay" class="create-box">
          <h2 class="login-header">Neuer Exchange Day</h2>
          <form @submit.prevent="createExchangeDay">
            <div class="input-group">
              <label for="date">Datum</label>
              <input type="date" id="date" v-model="date" required />
            </div>
            <div class="input-group">
              <label for="exchangeName">Name</label>
              <input type="text" id="exchangeName" v-model="exchangeName" required />
            </div>
            <div class="input-group">
              <label for="location">Ort</label>
              <input type="text" id="location" v-model="location" required />
            </div>
            <div class="input-group">
              <label for="exchangeDescription">Beschreibung</label>
              <input type="text" id="exchangeDescription" v-model="exchangeDescription" required />
            </div>
            <button type="submit" class="login-button">Exchange Day erstellen</button>
          </form>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import '../../assets/event-planning.css'; 
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import config from "../../config";

const showWorkshopBox = ref(false);
const showExchangeDay = ref(false);

// Workshop form data
const name = ref('');
const description = ref('');
const startTime = ref('');
const endTime = ref('');
const room = ref('');
const exchangeDaySelect = ref('');
const organizerSelect = ref('');

const exchangeDays = ref([]);
const users = ref([]);

// Exchange Day form data
const exchangeName = ref('');
const exchangeDescription = ref('');
const date = ref('');
const location = ref('');

const apiUrl = `${config.apiBaseUrl}/events`;
const exchangeApiUrl = `${config.apiBaseUrl}/exchange-days`;

const router = useRouter();

/**
 * Fetches all Exchange Days and users from the API when the component is mounted.
 */
onMounted(async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/exchange-days`);
    exchangeDays.value = await response.json();
  } catch (error) {
    console.error('Fehler beim Laden der Exchange Days:', error);
  }

  try {
    const response = await fetch(`${config.apiBaseUrl}/users`);
    users.value = await response.json();
  } catch (error) {
    console.error('Fehler beim Laden der Benutzer:', error);
  }
});

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
 * Creates a new Workshop using the form data.
 */
const createWorkshop = async () => {
  try {
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: name.value,
        startTime: startTime.value,
        endTime: endTime.value,
        room: room.value,
        description: description.value,
        exchangeDayId: exchangeDaySelect.value,
        organizerId: organizerSelect.value,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      console.log('Workshop erstellt:', data);
      alert(`Workshop erstellt: ${data.name}`);
      resetWorkshopForm();
    } else {
      alert('Fehler beim Erstellen des Workshops.');
    }
  } catch (error) {
    console.error('Fehler beim Erstellen des Workshops:', error);
    alert('Fehler beim Erstellen des Workshops.');
  }
};

/**
 * Creates a new Exchange Day using the form data.
 */
const createExchangeDay = async () => {
  try {
    const response = await fetch(exchangeApiUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: exchangeName.value,
        date: date.value,
        location: location.value,
        description: exchangeDescription.value,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      console.log('Exchange Day erstellt:', data);
      alert(`Exchange Day erstellt: ${data.name}`);
      resetExchangeDayForm();
    } else {
      alert('Fehler beim Erstellen des Exchange Days.');
    }
  } catch (error) {
    console.error('Fehler beim Erstellen des Exchange Days:', error);
    alert('Fehler beim Erstellen des Exchange Days.');
  }
};

/**
 * Resets the Workshop form fields.
 */
const resetWorkshopForm = () => {
  name.value = '';
  startTime.value = '';
  endTime.value = '';
  room.value = '';
  description.value = '';
  exchangeDaySelect.value = '';
  organizerSelect.value = '';
  showWorkshopBox.value = false;
};

/**
 * Resets the Exchange Day form fields.
 */
const resetExchangeDayForm = () => {
  exchangeName.value = '';
  exchangeDescription.value = '';
  date.value = '';
  location.value = '';
  showExchangeDay.value = false;
};
</script>
