<template>
  <div class="container">
    <div class="events">
      <button @click="toggleWorkshopBox" class="action-button" type="button">Neuer Workshop</button>
      <button @click="createExchangeDay" class="action-button" type="button">Neuer Exchange Day</button>

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
            <button type="submit" class="login-button">Erstellen</button>
          </form>
        </div>
      </transition>
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref } from 'vue';

const showWorkshopBox = ref(false);
const name = ref('');
const description = ref('');
const startTime = ref('');
const endTime = ref('');
const room = ref('');

const toggleWorkshopBox = () => {
  showWorkshopBox.value = !showWorkshopBox.value;
};

const createExchangeDay = () => {
  console.log('Neuer Exchange Day erstellt');
};

const CreateWorkshop = () => {
  console.log('Workshop erstellt:', { 
    name: name.value, 
    description: description.value, 
    startTime: startTime.value, 
    endTime: endTime.value, 
    room: room.value 
  });
};
</script>


<style scoped>
.action-button {
  margin: 10px;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}

.action-button {
  margin: 10px;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}

.login-box {
  overflow: hidden;
}

.roll-enter-active, .roll-leave-active {
  transition: max-height 0.4s ease, opacity 0.4s ease;
}

.roll-enter-from, .roll-leave-to {
  max-height: 0;
  padding: 0;
  opacity: 0;
}

.roll-enter-to, .roll-leave-from {
  opacity: 1;
}
</style>

