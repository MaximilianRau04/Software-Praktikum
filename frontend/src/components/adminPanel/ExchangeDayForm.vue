<template>
  <div class="create-box">
    <h2 class="login-header">Neuer Exchange Day</h2>
    <form>
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
      <button type="submit" class="login-button" @click="createExchangeDay">
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
  try {
    const response = await fetch(exchangeApiUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: exchangeName.value,
        startDate: startDate.value,
        endDate: endDate.value,
        locationId: location.value.id,
        description: exchangeDescription.value,
      }),
    });

    if (response.ok) {
      const data = await response.json();
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

// Fetch locations from the backend
onMounted(async () => {
  setStartDateToToday();
  try {
    const response = await fetch(locationsApiUrl);
    if (response.ok) {
      const data = await response.json();
      locations.value = data;
    }
  } catch (error) {
    showToast(new Toast("Fehler beim Abrufen der Locations", "error"));
  }
});
</script>
