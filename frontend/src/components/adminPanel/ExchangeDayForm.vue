<template>
  <div class="create-box">
    <h2 class="login-header">Neuer Exchange Day</h2>
    <form @submit.prevent="createExchangeDay">
      <div class="input-group">
        <label for="exchangeName">Name</label>
        <input type="text" id="exchangeName" v-model="exchangeName" required />
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
        <!-- Dropdown für Locations -->
        <select id="location" v-model="location" required>
          <option value="" disabled>Wähle einen Ort</option>
          <option v-for="loc in locations" :key="loc.id" :value="loc">
            {{ loc.street }} {{ loc.houseNumber }}, {{ loc.city }},
            {{ loc.country }}
          </option>
        </select>
      </div>
      <div class="input-group">
        <label for="exchangeDescription">Beschreibung</label>
        <input
          type="text"
          id="exchangeDescription"
          v-model="exchangeDescription"
        />
      </div>
      <button type="submit" class="action-button">
        Exchange Day erstellen
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { defineEmits } from "vue";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import api from "@/util/api";

const exchangeName = ref("");
const startDate = ref("");
const endDate = ref("");
const location = ref<any>(null);
const exchangeDescription = ref("");
const locations = ref<any[]>([]);

const emit = defineEmits(["update:showExchangeDayBox"]);

const exchangeApiUrl = `${config.apiBaseUrl}/exchange-days`;
const locationsApiUrl = `${config.apiBaseUrl}/locations`;

/**
 * Creates a new Exchange Day using the form data.
 */
const createExchangeDay = async () => {
  const exchangeDayData = {
        name: exchangeName.value,
        startDate: startDate.value,
        endDate: endDate.value,
        locationId: location.value.id,
        description: exchangeDescription.value,
      }

  try {
    const response = await api.post(`/exchange-days`, exchangeDayData);

    if (response.status === 201) {
      const data = await response.data;
      showToast(
        new Toast("Success", "Exchange Day erfolgreich erstellt", "success"),
      );
      resetExchangeDayForm();
    } else {
      showToast(
        new Toast("Error", "Fehler beim Erstellen des Exchange Days", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Erstellen des Exchange Days", "error"),
    );
  }
};

/**
 * Resets the Exchange Day form fields.
 */
const resetExchangeDayForm = () => {
  exchangeName.value = "";
  exchangeDescription.value = "";
  startDate.value = "";
  endDate.value = "";
  location.value = null;
  emit("update:showExchangeDayBox", false);
};

/**
 * Sets the start date to today.
 */
const setStartDateToToday = () => {
  const today = new Date();
  const formattedDate = today.toISOString().split("T")[0];
  startDate.value = formattedDate;
};

/**
 * Updates the end date to the day after the start date.
 */
const updateEndDate = (startDate) => {
  const startDateObject = new Date(startDate);
  const nextDay = new Date(startDateObject);
  nextDay.setDate(startDateObject.getDate() + 1);

  const formattedEndDate = nextDay.toISOString().split("T")[0];
  endDate.value = formattedEndDate;
};

watch(startDate, (newStartDate) => {
  if (newStartDate) {
    updateEndDate(newStartDate);
  }
});

onMounted(async () => {
  setStartDateToToday();
  try {
    const response = await api.get(`/locations`);
    if (response.status === 200) {
      const data = await response.data;
      locations.value = data;
    }
  } catch (error) {
    showToast(new Toast("Fehler beim Abrufen der Locations", "error"));
  }
});
</script>
