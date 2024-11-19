<template>
  <div class="container">
    <div class="events">
      <button @click="toggleWorkshopBox" class="action-button" type="button":disabled="showExchangeDayBox">Neuer Workshop</button>
      <button @click="toggleExchangeDay" class="action-button" type="button":disabled="showWorkshopBox">Neuer Exchange Day</button>

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
            <button type="submit" class="login-button">Workshop erstellen</button>
          </form>
        </div>
      </transition>

      <transition name="roll">
        <div v-if="showExchangeDayBox" class="create-box">
          <h2 class="login-header">Neuer Exchange Day</h2>
          <form @submit.prevent="createExchangeDay">
            <div class="input-group">
              <label for="name2">Name</label>
              <input type="text" id="name2" v-model="name2" required />
            </div>
            <div class="input-group">
              <label for="date">Datum</label>
              <input type="date" id="date" v-model="date" required />
            </div>
            <div class="input-group">
              <label for="location">Ort</label>
              <input type="location" id="location" v-model="location" required />
            </div>
            <div class="input-group">
              <label for="description">Beschreibung</label>
              <input type="description" id="description" v-model="description" required />
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

const showWorkshopBox = ref(false);
const name = ref('');
const description = ref('');
const startTime = ref('');
const endTime = ref('');
const room = ref('');

const toggleWorkshopBox = () => {
  showWorkshopBox.value = !showWorkshopBox.value;
  if (showWorkshopBox.value) {
    showExchangeDayBox.value = false; 
  }
};

const CreateWorkshop = () => {
  alert(`Workshop erstellt: 
  Name: ${name.value}
  Beschreibung: ${description.value}
  Startzeit: ${startTime.value}
  Endzeit: ${endTime.value}
  Raum: ${room.value}`);
  console.log('Workshop erstellt:', { 
    name: name.value, 
    description: description.value, 
    startTime: startTime.value, 
    endTime: endTime.value, 
    room: room.value 
  });
};

const showExchangeDayBox = ref(false);
const name2 = ref('');
const date= ref('');
const location = ref('');
const description2 = ref('');

const toggleExchangeDay = () => {
  showExchangeDayBox.value = !showExchangeDayBox.value;
  if (showExchangeDayBox.value) {
    showWorkshopBox.value = false; 
  }
};

const createExchangeDay = () => {
  alert(`Exchange Day erstellt: 
  Name: ${name2.value}
  Datum: ${date.value}
  Ort: ${location.value}
  Beschreibung: ${description2.value}`);
  console.log('ExchangeDay erstellt:', { 
    name2: name2.value, 
    date: date.value, 
    location: location.value,
    description2: description2.value,
  });
};

</script>
