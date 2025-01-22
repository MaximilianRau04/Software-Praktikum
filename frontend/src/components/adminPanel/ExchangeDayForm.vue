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
      <button type="submit" class="login-button" @click="createExchangeDay">
        Exchange Day erstellen
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { defineEmits, watch, onMounted } from "vue";
import config from "@/config";

const exchangeName = ref("");
const startDate = ref("");
const endDate = ref("");
const location = ref("");
const exchangeDescription = ref("");

const emit = defineEmits(["update:showExchangeDayBox"]);

const exchangeApiUrl = `${config.apiBaseUrl}/exchange-days`;

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
    } else {
      alert("Fehler beim Erstellen des Exchange Days.");
    }
  } catch (error) {
    console.error("Fehler beim Erstellen des Exchange Days:", error);
    alert("Fehler beim Erstellen des Exchange Days.");
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
  location.value = "";
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

onMounted(() => {
  setStartDateToToday();
});
</script>
