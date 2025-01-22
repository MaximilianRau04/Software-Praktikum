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
        <input type="text" id="resourceLocation" v-model="location" required />
      </div>
      <button type="submit" class="login-button">Ressource erstellen</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import config from "@/config";

const name = ref("");
const type = ref("");
const description = ref("");
const capacity = ref<number | null>(null);
const location = ref("");
const availability = ref(true);

const resourceTypes = ["ROOM", "EQUIPMENT", "MATERIAL"];

const apiUrl = `${config.apiBaseUrl}/resources`;

const emit = defineEmits(["update:showResourceBox"]);

/**
 * create a new resource
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
        location: location.value,
        availability: availability.value,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      console.log("Resource erstellt:", data);
      alert(`Resource erstellt: ${data.name}`);
      resetForm();
    } else {
      alert("Fehler beim Erstellen der Resource.");
    }
  } catch (error) {
    console.error("Fehler beim Erstellen der Resource:", error);
    alert("Fehler beim Erstellen der Resource.");
  }
};

/**
 * reset the form
 */
const resetForm = () => {
  name.value = "";
  type.value = "";
  description.value = "";
  capacity.value = null;
  location.value = "";
  availability.value = true;
  emit("update:showResourceBox", false);
};
</script>
