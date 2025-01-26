<template>
  <div class="create-box">
    <h2 class="login-header">Neue Ressource</h2>
    <form @submit.prevent="createResource">
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
      <div class="input-group">
        <label for="resourceLocation">Ort</label>
        <!-- dropdown for location -->
        <select id="resourceLocation" v-model="location" required>
          <option value="" disabled>Wähle einen Ort</option>
          <option v-for="loc in locations" :key="loc.id" :value="loc">
            {{ loc.street }} {{ loc.houseNumber }}, {{ loc.city }},
            {{ loc.country }}
          </option>
        </select>
      </div>
      <button type="submit" class="login-button">Ressource erstellen</button>
    </form>
  </div>
  <div class="csv-actions">
    <button class="csv-button" @click="triggerFileUpload">
      Ressourcen aus CSV importieren
    </button>
    <input
      ref="fileInput"
      type="file"
      accept=".csv"
      @change="handleFileUpload"
      style="display: none"
    />
    <button class="csv-button" @click="downloadCsvOfResources">
      Ressourcen als CSV downloaden
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

const name = ref("");
const type = ref("");
const description = ref("");
const capacity = ref<number | null>(null);
const location = ref<any>(null);
const availability = ref(true);
const locations = ref<any[]>([]);

const resourceTypes = ["ROOM", "EQUIPMENT", "MATERIAL"];
const fileInput = ref<HTMLInputElement | null>(null);

const apiUrl = `${config.apiBaseUrl}/resources`;
const locationsApiUrl = `${config.apiBaseUrl}/locations`;
const emit = defineEmits(["update:showResourceBox"]);

/**
 * Creates a new resource using the form data
 */
const createResource = async () => {
  try {
    const response = await fetch(apiUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: name.value,
        type: type.value,
        capacity: capacity.value,
        description: description.value,
        locationId: location.value.id,
        availability: availability.value,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      showToast(
        new Toast("Success", `Resource erfolgreich erstellt`, "success"),
      );
      resetForm();
    } else {
      showToast(
        new Toast("Error", `Fehler beim Erstellen der Resource`, "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", `Fehler beim Erstellen der Resource`, "error"),
    );
  }
};

/**
 * Resets the form after submission
 */
const resetForm = () => {
  name.value = "";
  type.value = "";
  description.value = "";
  capacity.value = null;
  location.value = null;
  availability.value = true;
  emit("update:showResourceBox", false);
};

/**
 * download CSV file of todos
 */
async function downloadCsvOfResources() {
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/resources/csv-downloads`,
    );
    if (!response.ok) throw new Error("Download fehlgeschlagen!");

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "resources.csv";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    showToast(
      new Toast("Error", "CSV Download fehlgeschlagen!", "error", faXmark, 10),
    );
  }
}

/**
 * Trigger file input click
 */
const triggerFileUpload = () => {
  fileInput.value?.click();
};

/**
 * Handles the file upload event and sends the CSV to the backend
 */
const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files || target.files.length === 0) {
    showToast(new Toast("Error", "Keine Datei ausgewählt!", "error"));
    return;
  }

  const file = target.files[0];
  const formData = new FormData();
  formData.append("file", file);

  try {
    const response = await fetch(`${config.apiBaseUrl}/resources/csv-import`, {
      method: "POST",
      body: formData,
    });

    const responseText = await response.text();

    if (!response.ok) {
      throw new Error(responseText || "Import fehlgeschlagen!");
    }

    showToast(new Toast("Success", "CSV erfolgreich importiert!", "success"));
  } catch (error) {
    showToast(new Toast("Error", "Import fehlgeschlagen", "error"));
  } finally {
    target.value = "";
  }
};

/**
 * Fetch locations from the backend
 */
onMounted(async () => {
  try {
    const response = await fetch(locationsApiUrl);
    if (response.ok) {
      const data = await response.json();
      locations.value = data;
    } else {
      showToast(
        new Toast("Error", "Fehler beim Abrufen der Locations", "error"),
      );
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Abrufen der Locations", "error"));
  }
});
</script>
