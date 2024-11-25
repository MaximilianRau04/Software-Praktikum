<template>
 <div class="container">
    <div class="events">
      <button @click="toggleWorkshopBox" class="action-button" type="button"><img src="@/images/plus.png" alt="Plus" class="plus-icon" width="35" height="35" />Neuer Workshop</button>
      <button @click="toggleExchangeDayBox" class="action-button" type="button"><img src="@/images/plus.png" alt="Plus" class="plus-icon" width="35" height="35" />Neuer Exchange Day</button>
      
      <transition name="roll">
        <div v-if="showWorkshopBox" class="create-box">
          <h2 class="login-header">Neuer Workshop</h2>
          <form @submit.prevent="CreateWorkshop">
            <div class="input-group">
              <label for="name">Name</label>
              <input type="text" id="name" v-model="name" required />
            </div>
            <div class="input-group">
              <label for="description">Beschreibung</label>
              <input type="text" id="description" v-model="description" required />
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
              <label for="exchangeDayId">Exchange Day ID</label>
              <input type="text" id="exchangeDayId" v-model="exchangeDayId" required />
            </div>
            <div class="input-group">
              <label for="organizer">Veranstalter</label>
              <input type="organizer" id="organizer" v-model="organizer" required />
            </div>
            <button type="submit" class="login-button">Workshop erstellen</button>
          </form>
        </div>
      </transition>

      <transition name="roll">
        <div v-if="showExchangeDay" class="create-box">
          <h2 class="login-header">Neuer Exchange Day</h2>
          <form @submit.prevent="CreateExchangeDay">
            <div class="input-group">
              <label for="date">Datum</label>
              <input type="date" id="date" v-model="date" required />
            </div>
            <div class="input-group">
              <label for="Exchangename">Name</label>
              <input type="text" id="exchangeName" v-model="exchangeName" required />
            </div>
            <div class="input-group">
              <label for="location">Ort</label>
              <input type="location" id="location" v-model="location" required />
            </div>
            <div class="input-group">
              <label for="ExchangeDescription">Beschreibung</label>
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
import { ref } from 'vue';
import axios from 'axios';


const showWorkshopBox = ref(false);
const showExchangeDay = ref(false);

// workshop Attributes
const name = ref('');
const description = ref('');
const startTime = ref('');
const endTime = ref('');
const room = ref('');
const exchangeDayId = ref('');
const organizer = ref('');

// exchange day Attributes
const exchangeName = ref('');
const exchangeDescription = ref('');
const date = ref('');
const location = ref('');

const toggleWorkshopBox = () => {
  showWorkshopBox.value = !showWorkshopBox.value;
  if (showWorkshopBox.value) {
    showExchangeDay.value = false; 
  }
};

const CreateWorkshop = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/events', {
      name: name.value,
      description: description.value,
      startTime: startTime.value,
      endTime: endTime.value,
      room: room.value,
      exchangeDayId: exchangeDayId.value,
      organizer: organizer.value,
    });

    console.log('Workshop erstellt:', response.data);
    alert(`Workshop erstellt: 
      Name: ${response.data.name}
      Beschreibung: ${response.data.description}
      Startzeit: ${response.data.startTime}
      Endzeit: ${response.data.endTime}
      Raum: ${response.data.room}
      Exchange Day ID: ${response.data.exchangeDayId}}`);

    // Formular zurücksetzen
    showWorkshopBox.value = false;
    name.value = '';
    description.value = '';
    startTime.value = '';
    endTime.value = '';
    room.value = '';
  } catch (error) {
    console.error('Fehler beim Erstellen des Workshops:', error);
    alert('Fehler beim Erstellen des Workshops!');
  }
};

const toggleExchangeDayBox = () => {
  showExchangeDay.value = !showExchangeDay.value;
  if (showExchangeDay.value) {
    showWorkshopBox.value = false; 
  }
};

const CreateExchangeDay = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/exchange-days', {
      name: exchangeName.value,
      date: date.value,
      location: location.value,
      description: exchangeDescription.value,
    });

    console.log('Exchange Day erstellt:', response.data);
    alert(`Exchange Day erstellt: 
      Datum: ${response.data.date}
      Name: ${response.data.name}
      Ort: ${response.data.location}
      Beschreibung: ${response.data.description}`);

    // Formular zurücksetzen
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