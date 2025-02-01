<template>
  <div class="create-box">
    <h2 class="form-header">Neue Location</h2>
    <form @submit.prevent="createLocation">
      <div class="input-group">
        <label for="country">Land</label>
        <select id="country" v-model="country" @change="updateCities" required>
          <option value="" disabled selected>Wählen Sie ein Land</option>
          <option v-for="(name, index) in countries" :key="index" :value="name">
            {{ name }}
          </option>
          <option value="custom">Anderes Land (bitte eingeben)</option>
        </select>
        <input
          v-if="country === 'custom'"
          type="text"
          v-model="customCountry"
          placeholder="Bitte Land eingeben"
        />
      </div>

      <div class="input-group" v-if="country">
        <label for="city">Stadt</label>
        <select id="city" v-model="city" required>
          <option value="" disabled selected>Wählen Sie eine Stadt</option>
          <option v-for="(name, index) in cities" :key="index" :value="name">
            {{ name }}
          </option>
          <option value="custom">Andere Stadt (bitte eingeben)</option>
        </select>
        <input
          v-if="city === 'custom'"
          type="text"
          v-model="customCity"
          placeholder="Bitte Stadt eingeben"
        />
      </div>

      <div class="input-group">
        <label for="postalCode">Postleitzahl</label>
        <input type="text" id="postalCode" v-model="postalCode" required />
      </div>

      <div class="input-group">
        <label for="street">Straße</label>
        <input type="text" id="street" v-model="street" required />
      </div>

      <div class="input-group">
        <label for="houseNumber">Hausnummer</label>
        <input type="text" id="houseNumber" v-model="houseNumber" required />
      </div>

      <button type="submit" class="login-button">Erstelle Location</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import api from "@/util/api";

const countries = ref<string[]>([]);
const cities = ref<string[]>([]);

const country = ref("");
const city = ref("");
const customCountry = ref("");
const customCity = ref("");

const postalCode = ref("");
const street = ref("");
const houseNumber = ref("");

const apiUrl = `${config.apiBaseUrl}/locations`;

const emit = defineEmits(["update:showLocationBox"]);

/**
 * Fetch the list of countries from the API
 */
const fetchCountries = async () => {
  try {
    const response = await api.get(`/locations/countries`);
    if (response.status === 200) {
      countries.value = await response.data;
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Abrufen der Länder", "error"));
  }
};

/**
 * Update the cities based on the selected country
 */
const updateCities = async () => {
  if (country.value !== "custom") {
    try {
      const response = await api.get(`/locations/cities/${country.value}`);
      if (response.status === 200) {
        cities.value = await response.data;
      }
    } catch (error) {
      showToast(new Toast("Error", "Fehler beim Abrufen der Städte", "error"));
    }
  } else {
    cities.value = [];
  }
};

/**
 * create a new location
 */
const createLocation = async () => {
  const selectedCountry =
    country.value === "custom" ? customCountry.value : country.value;
  const selectedCity = city.value === "custom" ? customCity.value : city.value;

  const locationData = {
        city: selectedCity,
        postalCode: postalCode.value,
        street: street.value,
        houseNumber: houseNumber.value,
        country: selectedCountry,
      }

  try {
    const response = await api.post(`/locations`, locationData);

    if (response.status === 200) {
      const data = await response.data;
      showToast(
        new Toast("Success", "Location erfolgreich erstellt", "success"),
      );
      resetForm();
    } else {
      showToast(
        new Toast("Error", "Fehler beim Erstellen der Location", "error"),
      );
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Erstellen der Location", "error"),
    );
  }
};

/**
 * reset the form fields
 */
const resetForm = () => {
  city.value = "";
  postalCode.value = "";
  street.value = "";
  houseNumber.value = "";
  country.value = "";
  customCountry.value = "";
  customCity.value = "";
  emit("update:showLocationBox", false);
};

onMounted(() => {
  fetchCountries();
});
</script>
