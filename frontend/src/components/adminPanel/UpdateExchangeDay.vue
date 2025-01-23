<template>
  <div class="updateExchangeDay">
    <h1>Exchange Day bearbeiten</h1>
    <form @submit.prevent="updateExchangeDay">
      <div class="form-group">
        <label for="name">Name:</label>
        <input
          type="text"
          id="name"
          v-model="exchangeDay.name"
          required
          minlength="3"
          maxlength="100"
        />
      </div>
      <div class="form-group">
        <label for="description">Beschreibung:</label>
        <textarea
          id="description"
          v-model="exchangeDay.description"
          maxlength="255"
        ></textarea>
      </div>
      <div class="form-group">
        <label for="location">Location:</label>
        <select id="location" v-model="selectedLocation">
          <option
            v-for="location in locations"
            :key="location.id"
            :value="location"
          >
            {{ location.street }} {{ location.houseNumber }},
            {{ location.city }}, {{ location.country }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <label for="startDate">Startdatum:</label>
        <input
          type="date"
          id="startDate"
          v-model="exchangeDay.startDate"
          required
        />
      </div>
      <div class="form-group">
        <label for="endDate">Enddatum:</label>
        <input
          type="date"
          id="endDate"
          v-model="exchangeDay.endDate"
          :min="exchangeDay.startDate"
          required
        />
      </div>
      <div class="button-group">
        <button type="button" class="back-button" @click="goBack">
          Zurück
        </button>
        <button type="button" class="delete-button" @click="deleteExchangeDay">
          Löschen
        </button>
        <button type="submit" class="submit-button">Speichern</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import config from "../../config";

const router = useRouter();
const route = useRoute();
const locations = ref<any[]>([]);
const selectedLocation = ref<any | null>(null);

const exchangeDay = ref({
  name: "",
  description: "",
  location: {
    street: "",
    houseNumber: "",
    city: "",
    country: "",
  },
  startDate: "",
  endDate: "",
});

/**
 * Fetches all locations from the API and stores them in the `locations` ref.
 */
const fetchLocations = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/locations`);
    if (!response.ok) {
      throw new Error("Fehler beim Abrufen der Locations.");
    }
    const data = await response.json();
    locations.value = data;
    if (locations.value.length > 0) {
      selectedLocation.value = locations.value[0];
    }
  } catch (error) {
    console.error("Fehler beim Laden der Locations:", error);
  }
};

/**
 * Fetches the details of the selected Exchange Day from the API.
 */
const fetchExchangeDayDetails = async () => {
  const exchangeDayId = route.params.exchangeDayId;
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/exchange-days/${exchangeDayId}`
    );
    if (!response.ok) {
      throw new Error("Fehler beim Abrufen der Exchange Day-Daten.");
    }
    const data = await response.json();
    exchangeDay.value = data;
  } catch (error) {
    console.error("Error fetching exchange day details:", error);
    alert("Der Exchange Day konnte nicht geladen werden.");
  }
};

/**
 * Automatically updates the end date to one day after the start date.
 */
const updateEndDate = () => {
  const startDate = new Date(exchangeDay.value.startDate);
  startDate.setDate(startDate.getDate() + 1); // Set to the next day
  const formattedDate = startDate.toISOString().split("T")[0];
  exchangeDay.value.endDate = formattedDate;
};

// Watch for changes in the start date and update the end date
watch(() => exchangeDay.value.startDate, updateEndDate);

/**
 * Updates the Exchange Day in the database.
 */
const updateExchangeDay = async () => {
  const exchangeDayId = route.params.exchangeDayId;

  const cleanExchangeDayData = {
    name: exchangeDay.value.name,
    startDate: exchangeDay.value.startDate,
    endDate: exchangeDay.value.endDate,
    description: exchangeDay.value.description,
    locationId: selectedLocation.value ? selectedLocation.value.id : null,
  };

  try {
    const response = await fetch(
      `${config.apiBaseUrl}/exchange-days/${exchangeDayId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(cleanExchangeDayData),
      }
    );

    if (!response.ok) {
      throw new Error("Fehler beim Aktualisieren des Exchange Days.");
    }

    alert("Der Exchange Day wurde erfolgreich aktualisiert!");
    router.push({ name: "home" });
  } catch (error) {
    console.error("Error updating exchange day:", error);
    alert("Der Exchange Day konnte nicht aktualisiert werden.");
  }
};

/**
 * Deletes the Exchange Day from the database.
 */
const deleteExchangeDay = async () => {
  const exchangeDayId = route.params.exchangeDayId;
  if (
    !confirm("Sind Sie sicher, dass Sie diesen Exchange Day löschen möchten?")
  ) {
    return;
  }

  try {
    const response = await fetch(
      `${config.apiBaseUrl}/exchange-days/${exchangeDayId}`,
      {
        method: "DELETE",
      }
    );

    if (!response.ok) {
      throw new Error("Fehler beim Löschen des Exchange Days.");
    }

    alert("Der Exchange Day wurde erfolgreich gelöscht.");
    router.push({ name: "home" });
  } catch (error) {
    console.error("Error deleting exchange day:", error);
    alert("Der Exchange Day konnte nicht gelöscht werden.");
  }
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  fetchExchangeDayDetails();
  fetchLocations();
});
</script>

<style scoped>
.updateExchangeDay {
  max-width: 600px;
  margin: 40px auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  font-size: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  font-size: 16px;
  color: #555;
}

input,
textarea,
select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
  min-height: 100px;
  resize: none;
}

select {
  appearance: none;
  background-color: white;
  background-image: url('data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="none"%3E%3Cpath fill="%23000" d="M10 13l5-5H5l5 5z"%3E%3C/path%3E%3C/svg%3E');
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 12px;
  padding-right: 30px;
}

select:focus {
  border-color: #007bff;
  outline: none;
}

button {
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.back-button {
  background-color: #007bff;
  color: white;
  border: none;
}

.back-button:hover {
  background-color: #0056b3;
}

.submit-button {
  background-color: #28a745;
  color: white;
  border: none;
}

.submit-button:hover {
  background-color: #218838;
}

.delete-button {
  background-color: #dc3545;
  color: white;
  border: none;
}

.delete-button:hover {
  background-color: #c82333;
}

select option {
  padding: 10px;
  font-size: 16px;
}

select:hover {
  background-color: #f0f0f0;
}
</style>
