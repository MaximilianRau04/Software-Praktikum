<template>
  <div class="create-box">
    <h2 class="login-header">ExchangeDay bearbeiten</h2>
    <form @submit.prevent="updateExchangeDay">
      <!-- exchange day select -->
      <div class="input-group">
        <label for="exchangeDaySelect">ExchangeDay auswählen</label>
        <select
          id="exchangeDaySelect"
          v-model="selectedExchangeDay"
          @change="fetchExchangeDayDetails"
          required
        >
          <option value="" disabled>Wähle einen ExchangeDay</option>
          <option
            v-for="exchangeDay in exchangeDays"
            :key="exchangeDay.id"
            :value="exchangeDay"
          >
            {{ exchangeDay.name }} ({{ formatDate(exchangeDay.startDate) }} -
            {{ formatDate(exchangeDay.endDate) }})
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="name">Name</label>
        <input type="text" id="name" v-model="name" required />
      </div>

      <div class="input-group">
        <label for="description">Beschreibung</label>
        <input type="text" id="description" v-model="description" />
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
        <select id="location" v-model="location" required>
          <option value="" disabled>Wähle einen Ort</option>
          <option v-for="loc in locations" :key="loc.id" :value="loc">
            {{ loc.street }} {{ loc.houseNumber }}, {{ loc.city }},
            {{ loc.country }}
          </option>
        </select>
      </div>

      <div class="button-group">
        <button type="submit" class="update-button">
          ExchangeDay aktualisieren
        </button>
        <button
          type="button"
          class="delete-button"
          @click="deleteExchangeDay"
          :disabled="!selectedExchangeDay"
        >
          ExchangeDay löschen
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { showToast, Toast } from "@/types/toasts";
import api from "@/util/api";
import "@/assets/adminPanel/event-list.css";

const selectedExchangeDay = ref<any | null>(null);
const exchangeDays = ref<any[]>([]);
const locations = ref<any[]>([]);
const name = ref("");
const description = ref("");
const startDate = ref("");
const endDate = ref("");
const location = ref<any>(null);

/**
 * Fetches a list of all ExchangeDays.
 */
const fetchExchangeDays = async () => {
  try {
    const response = await api.get(`/exchange-days`);
    if (response.status === 200) {
      exchangeDays.value = await response.data;
    } else {
      showToast(
        new Toast("Error", "Fehler beim Abrufen der ExchangeDays", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Abrufen der ExchangeDays", "error"),
    );
  }
};

/**
 * Fetches a list of all Locations.
 */
const fetchLocations = async () => {
  try {
    const response = await api.get(`/locations`);
    if (response.status === 200) {
      locations.value = await response.data;
    } else {
      showToast(
        new Toast("Error", "Fehler beim Abrufen der Standorte", "error"),
      );
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Abrufen der Standorte", "error"));
  }
};

/**
 * Fetches the details of the selected ExchangeDay.
 */
const fetchExchangeDayDetails = async () => {
  if (!selectedExchangeDay.value) return;

  try {
    const response = await api.get(
      `/exchange-days/${selectedExchangeDay.value.id}`,
    );
    if (response.status === 200) {
      const data = await response.data;
      name.value = data.name;
      description.value = data.description;
      startDate.value = data.startDate;
      endDate.value = data.endDate;
      location.value = data.location;
    } else {
      showToast(
        new Toast(
          "Error",
          "Fehler beim Abrufen der ExchangeDay-Details",
          "error",
        ),
      );
    }
  } catch (error) {
    showToast(
      new Toast(
        "Error",
        "Fehler beim Abrufen der ExchangeDay-Details",
        "error",
      ),
    );
  }
};

/**
 * Updates the selected ExchangeDay with new data.
 */
const updateExchangeDay = async () => {
  if (!selectedExchangeDay.value) return;

  const payload = {
    name: name.value,
    description: description.value,
    startDate: startDate.value,
    endDate: endDate.value,
    locationId: location.value.id,
  };

  try {
    const response = await api.put(
      `/exchange-days/${selectedExchangeDay.value.id}`,
      payload,
    );

    if (response.status === 200) {
      showToast(
        new Toast("Success", "ExchangeDay erfolgreich aktualisiert", "success"),
      );
      await fetchExchangeDays();
    } else {
      showToast(
        new Toast(
          "Error",
          "Fehler beim Aktualisieren des ExchangeDays",
          "error",
        ),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Aktualisieren des ExchangeDays", "error"),
    );
  }
};

/**
 * Deletes the selected ExchangeDay after confirmation.
 */
const deleteExchangeDay = async () => {
  if (!selectedExchangeDay.value) {
    showToast(new Toast("Warnung", "Kein ExchangeDay ausgewählt", "warning"));
    return;
  }

  if (confirm("Möchten Sie diesen ExchangeDay wirklich löschen?")) {
    try {
      const response = await api.delete(
        `/exchange-days/${selectedExchangeDay.value.id}`,
      );

      if (response.status === 204) {
        showToast(
          new Toast("Erfolg", "Exchange Day erfolgreich gelöscht", "success"),
        );
        selectedExchangeDay.value = null;
        name.value = "";
        description.value = "";
        startDate.value = "";
        endDate.value = "";
        location.value = null;
        await fetchExchangeDays();
      } else {
        showToast(
          new Toast("Error", "Fehler beim Löschen des ExchangeDays", "error"),
        );
      }
    } catch (error) {
      showToast(
        new Toast("Error", "Fehler beim Löschen des ExchangeDays", "error"),
      );
    }
  }
};

/**
 * Formats a timestamp into a human-readable date string.
 *
 * @param {number} timestamp - The date in milliseconds.
 * @returns {string} - The formatted date string in 'DD.MM.YYYY' format.
 */
function formatDate(timestamp: number): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE");
}

onMounted(async () => {
  await fetchExchangeDays();
  await fetchLocations();
});
</script>
