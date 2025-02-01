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
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";

const name = ref("");
const type = ref("");
const description = ref("");
const capacity = ref<number | null>(null);
const location = ref<any>(null);
const availability = ref(true);
const locations = ref<any[]>([]);

const resourceTypes = ["ROOM", "EQUIPMENT", "MATERIAL"];
const fileInput = ref<HTMLInputElement | null>(null);

const emit = defineEmits(["update:showResourceBox"]);

/**
 * Creates a new resource using the form data
 */
const createResource = async () => {
  const resourceData = {
        name: name.value,
        type: type.value,
        capacity: capacity.value,
        description: description.value,
        locationId: location.value.id,
        availability: availability.value,
      }

  try {
    const response = await api.post(`/resources`, resourceData);

    if (response.status === 201) {
      const data = await response.data;
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
 * Fetch locations from the backend
 */
onMounted(async () => {
  try {
    const response = await api.get(`/locations`);
    if (response.status === 200) {
      const data = await response.data;
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

