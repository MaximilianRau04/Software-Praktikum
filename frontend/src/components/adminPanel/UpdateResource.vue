<template>
  <div class="create-box">
    <h2 class="login-header">Ressource aktualisieren</h2>
    <form @submit.prevent="updateResource">
      <div class="input-group">
        <label for="resourceLocation">Ort</label>
        <select
          id="resourceLocation"
          v-model="selectedLocation"
          @change="fetchResourcesByLocation"
          required
        >
          <option value="" disabled>Wähle einen Ort</option>
          <option v-for="loc in locations" :key="loc.id" :value="loc.id">
            {{ loc.street }} {{ loc.houseNumber }}, {{ loc.city }},
            {{ loc.country }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="resourceSelect">Ressource auswählen</label>
        <select
          id="resourceSelect"
          v-model="selectedResource"
          @change="fetchResourceDetails"
          :disabled="!selectedLocation"
          required
        >
          <option value="" disabled>Wähle eine Ressource</option>
          <option
            v-for="resource in resources"
            :key="resource.id"
            :value="resource.id"
          >
            {{ resource.name }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="resourceName">Name</label>
        <input type="text" id="resourceName" v-model="name" required />
      </div>

      <div class="input-group">
        <label for="resourceType">Ressourcentyp</label>
        <select id="resourceType" v-model="type" required>
          <option
            v-for="resourceType in resourceTypes"
            :key="resourceType"
            :value="resourceType"
          >
            {{ resourceType }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="resourceDescription">Beschreibung</label>
        <input type="text" id="resourceDescription" v-model="description" />
      </div>

      <div class="input-group">
        <label for="resourceCapacity">Kapazität</label>
        <input type="number" id="resourceCapacity" v-model="capacity" min="0" />
      </div>

      <div class="input-group checkbox-group">
        <label for="resourceAvailability">
          <input
            type="checkbox"
            id="resourceAvailability"
            v-model="availability"
          />
          Verfügbarkeit
        </label>
      </div>

      <div class="button-group">
        <button type="submit" class="update-button">
          Ressource aktualisieren
        </button>
        <button
          type="button"
          class="delete-button"
          @click="deleteResource"
          :disabled="!selectedResource"
        >
          Ressource löschen
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";

const selectedLocation = ref<number | null>(null);
const selectedResource = ref<number | null>(null);
const name = ref("");
const type = ref("");
const description = ref("");
const capacity = ref<number | null>(null);
const availability = ref(true);
const locations = ref<any[]>([]);
const resources = ref<any[]>([]);

const resourceTypes = ["ROOM", "EQUIPMENT", "MATERIAL"];

const apiUrl = `${config.apiBaseUrl}/resources`;
const locationsApiUrl = `${config.apiBaseUrl}/locations`;
const emit = defineEmits(["update:showUpdateResourceBox"]);

/**
 * Fetches locations from the backend
 */
const fetchLocations = async () => {
  try {
    const response = await fetch(locationsApiUrl);
    if (response.ok) {
      const data = await response.json();
      locations.value = data;
    } else {
      showToast(
        new Toast("Error", "Fehler beim Abrufen der Locations.", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Abrufen der Locations.", "error"),
    );
  }
};

/**
 * Fetch resources based on the selected location
 */
const fetchResourcesByLocation = async () => {
  if (!selectedLocation.value) {
    resources.value = [];
    return;
  }

  try {
    const response = await fetch(
      `${apiUrl}/location/${selectedLocation.value}`,
    );
    if (response.ok) {
      const data = await response.json();
      resources.value = data;
    } else {
      showToast(
        new Toast("Error", "Fehler beim Abrufen der Ressourcen.", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Abrufen der Ressourcen.", "error"),
    );
  }
};

/**
 * Fetch resource details when a resource is selected
 */
const fetchResourceDetails = async () => {
  if (!selectedResource.value) return;

  try {
    const response = await fetch(`${apiUrl}/${selectedResource.value}`);
    if (response.ok) {
      const data = await response.json();
      name.value = data.name;
      type.value = data.type;
      description.value = data.description;
      capacity.value = data.capacity;
      availability.value = data.availability;
    } else {
      showToast(
        new Toast(
          "Error",
          "Fehler beim Abrufen der Ressourcendetails.",
          "error",
        ),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Abrufen der Ressourcendetails.", "error"),
    );
  }
};

/**
 * Updates the selected resource with the new details
 */
const updateResource = async () => {
  if (!selectedResource.value) {
    showToast(new Toast("Warning", "Keine Ressource ausgewählt.", "warning"));
    return;
  }

  const updatedResource = {
    name: name.value,
    type: type.value,
    description: description.value,
    capacity: capacity.value,
    availability: availability.value,
    locationId: selectedLocation.value,
  };

  try {
    const response = await fetch(`${apiUrl}/${selectedResource.value}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updatedResource),
    });

    if (response.ok) {
      const data = await response.json();
      showToast(
        new Toast("Success", `Ressource aktualisiert: ${data.name}`, "success"),
      );
      resetForm();
    } else {
      showToast(
        new Toast("Error", "Fehler beim Aktualisieren der Ressource.", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Aktualisieren der Ressource.", "error"),
    );
  }
};

/**
 * Deletes the selected resource
 */
const deleteResource = async () => {
  if (!selectedResource.value) {
    showToast(new Toast("Warning", "Keine Ressource ausgewählt.", "warning"));
    return;
  }

  const confirmDelete = confirm(
    "Möchten Sie diese Ressource wirklich löschen?",
  );
  if (!confirmDelete) return;

  try {
    const response = await fetch(`${apiUrl}/${selectedResource.value}`, {
      method: "DELETE",
    });

    if (response.ok) {
      showToast(
        new Toast("Success", "Ressource erfolgreich gelöscht.", "success"),
      );
      resetForm();
    } else {
      showToast(
        new Toast("Error", "Fehler beim Löschen der Ressource.", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Löschen der Ressource.", "error"),
    );
  }
};

/**
 * Resets the form
 */
const resetForm = () => {
  name.value = "";
  type.value = "";
  description.value = "";
  capacity.value = null;
  availability.value = true;
  selectedLocation.value = null;
  selectedResource.value = null;
  emit("update:showUpdateResourceBox", false);
};

/**
 * Fetch locations on mount
 */
fetchLocations();
</script>

<style scoped>
input[type="checkbox"] {
  appearance: none;
  width: 16px;
  height: 16px;
  border: 2px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  cursor: pointer;
  transition: all 0.2s ease;
}

input[type="checkbox"]:checked {
  background-color: #4aa3ff;
  border-color: #4aa3ff;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='white'%3E%3Cpath d='M13.78 4.22a.75.75 0 0 1 0 1.06l-7 7a.75.75 0 0 1-1.06 0l-3-3a.75.75 0 0 1 1.06-1.06L6 10.94l6.72-6.72a.75.75 0 0 1 1.06 0z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: center;
  background-size: 70%;
}

input[type="checkbox"]:hover {
  border-color: #007bff;
}

.button-group {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.update-button {
  background-color: #009ee2;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 4px;
}

.delete-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 4px;
}

.delete-button:disabled {
  background-color: #e0e0e0;
  color: #6c757d;
  cursor: not-allowed;
}
</style>
