<!-- 
  This component allows users to create new Workshops and Exchange Days. 
  It provides two toggleable forms:
    1. Workshop Creation Form - Allows the user to input details like name, description, start and end times, room, associated Exchange Day ID, and organizer.
    2. Exchange Day Creation Form - Allows the user to input details like date, name, location, and description.
  
  Each form submits the data to the backend API via POST requests.
  Upon successful submission, the forms reset and display a confirmation alert.
  Additionally, toggling between forms ensures that only one form is visible at a time
-->
<template>
 <div class="container">
   <!-- Section for creating Workshops and Exchange Days -->
    <div class="events">
       <!-- Button to toggle the form for creating -->
      <button @click="toggleWorkshopBox" class="action-button" type="button"><img src="@/images/plus.png" alt="Plus" class="plus-icon" width="35" height="35" />Neuer Workshop</button>
      <button @click="toggleExchangeDayBox" class="action-button" type="button"><img src="@/images/plus.png" alt="Plus" class="plus-icon" width="35" height="35" />Neuer Exchange Day</button>
      
       <!-- Form for creating a new Workshop -->
      <transition name="roll">
        <div v-if="showWorkshopBox" class="create-box">
          <h2 class="login-header">Neuer Workshop</h2>
          <form @submit.prevent="CreateWorkshop">
            <!-- Input field for the Workshop's name -->
            <div class="input-group">
              <label for="name">Name</label>
              <input type="text" id="name" v-model="name" required />
            </div>
            <!-- Input fields for start time and end time -->
            <div class="input-group">
              <label for="startTime">Startzeit</label>
              <input type="time" id="startTime" v-model="startTime" required />
            </div>
            <div class="input-group">
              <label for="endTime">Endzeit</label>
              <input type="time" id="endTime" v-model="endTime" required />
            </div>
            <!-- Input field for the room where the Workshop will take place -->
            <div class="input-group">
              <label for="room">Raum</label>
              <input type="text" id="room" v-model="room" required />
            </div>
            <!-- Input field for the description -->
            <div class="input-group">
              <label for="description">Beschreibung</label>
              <input type="text" id="description" v-model="description" required />
            </div>
             <!-- Input field for the exchange Day -->
            <div class="input-group">
              <label for="exchangeDaySelect">Exchange Day</label>
              <select id="exchangeDaySelect" v-model="exchangeDaySelect" required>
                <option value="" disabled selected>Bitte wählen Sie einen Exchange Day aus</option>
                <option v-for="exchangeDay in exchangeDays" :key="exchangeDay.id" :value="exchangeDay.id">
                  {{ exchangeDay.name }}
                </option>
              </select>
            </div>
             <!-- Input field for the organizer -->
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

      <!-- Form for creating a new Exchange Day -->
      <transition name="roll">
        <div v-if="showExchangeDay" class="create-box">
          <h2 class="login-header">Neuer Exchange Day</h2>
          <form @submit.prevent="CreateExchangeDay">
             <!-- Input field for the date -->
            <div class="input-group">
              <label for="date">Datum</label>
              <input type="date" id="date" v-model="date" required />
            </div>
              <!-- Input field for the name -->
            <div class="input-group">
              <label for="exchangeName">Name</label>
              <input type="text" id="exchangeName" v-model="exchangeName" required />
            </div>
            <!-- Input field for the location of the Exchange Day -->
            <div class="input-group">
              <label for="location">Ort</label>
              <input type="location" id="location" v-model="location" required />
            </div>
            <!-- Input field for the description -->
            <div class="input-group">
              <label for="exchangeDescription">Beschreibung</label>
              <input type="exchangeDescription" id="exchangeDescription" v-model="exchangeDescription" required />
            </div>
            <button type="submit" class="login-button">Exchange Day erstellen</button>
          </form>
        </div>
      </transition>

    </div>
  </div>
</template>


<script setup lang="ts">
import '../../assets/create.css'; 
import { ref, onMounted } from 'vue';
import axios from 'axios';

const exchangeDays = ref([]);
const users = ref([]);

/**
 * retunrs the exchange days from the backend API 
 */
onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/exchange-days');
    exchangeDays.value = response.data; 
  } catch (error) {
    console.error('Fehler beim Laden der Exchange Days:', error);
  }
});

//returns the users from the backend API
onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/users');
    users.value = response.data;
  } catch (error) {
    console.error('Fehler beim Laden der Benutzer:', error);
  }
});

const showWorkshopBox = ref(false);
const showExchangeDay = ref(false);

// workshop Attributes
const name = ref('');
const description = ref('');
const startTime = ref('');
const endTime = ref('');
const room = ref('');
const exchangeDaySelect = ref('');
const organizerSelect = ref('');

// exchange day Attributes
const exchangeName = ref('');
const exchangeDescription = ref('');
const date = ref('');
const location = ref('');

/**
 * Toggles the visibility of the Workshop Creation Form.
 */
const toggleWorkshopBox = () => {
  showWorkshopBox.value = !showWorkshopBox.value;
  if (showWorkshopBox.value) {
    showExchangeDay.value = false; 
  }
};

/**
 * creates a new Exchange Day by sending a POST request to the backend API
 * Upon successful creation, the form is reset and a confirmation alert is displayed.
 */
const CreateWorkshop = async () => {
  try {
    // create workshop data
    const response = await axios.post('http://localhost:8080/api/events', {
      name: name.value,
      startTime: startTime.value,
      endTime: endTime.value,
      room: room.value,
      description: description.value,
      exchangeDayId: exchangeDaySelect.value, 
      organizerId: organizerSelect.value,
    });

    // log workshop data
    console.log('Workshop erstellt:', response.data);
    alert(`Workshop erstellt: 
      Name: ${response.data.name}
      Beschreibung: ${response.data.description}
      Startzeit: ${response.data.startTime}
      Endzeit: ${response.data.endTime}
      Raum: ${response.data.room}

    `);
    // Reset form fields
    showWorkshopBox.value = false;
    name.value = '';
    startTime.value = '';
    endTime.value = '';
    room.value = '';
    description.value = '';
    exchangeDaySelect.value = '';
    organizerSelect.value = '';
  } catch (error) {
    console.error('Fehler beim Erstellen des Workshops:', error);
  }
};

/**
 * Toggles the visibility of the ExchangeDay Creation Form.
 */
const toggleExchangeDayBox = () => {
  showExchangeDay.value = !showExchangeDay.value;
  if (showExchangeDay.value) {
    showWorkshopBox.value = false; 
  }
};

/**
 * creates a new Exchange Day by sending a POST request to the backend API
 * Upon successful creation, the form is reset and a confirmation alert is displayed.
 */
const CreateExchangeDay = async () => {
  try {
    // create exchange day data
    const response = await axios.post('http://localhost:8080/api/exchange-days', {
      name: exchangeName.value,
      date: date.value,
      location: location.value,
      description: exchangeDescription.value,
    });

    // log exchange day data
    console.log('Exchange Day erstellt:', response.data);
    alert(`Exchange Day erstellt: 
      Datum: ${response.data.date}
      Name: ${response.data.name}
      Ort: ${response.data.location}
      Beschreibung: ${response.data.description}`);

    // Reset form fields
    showExchangeDay.value = false;
    exchangeName.value = '';
    exchangeDescription.value = '';
    date.value = '';
    location.value = '';
  } catch (error) {
    console.error('Fehler beim Erstellen des Exchange Days:', error);
    alert('Fehler beim Erstellen des Exchange Days!');
  }
};

</script>