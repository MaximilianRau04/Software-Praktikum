<template>
  <div class="container">
    <div class="box">
      <div class="action-buttons">
        <!-- Create Buttons -->
        <div class="create-buttons-group">
          <button
            @click="toggleExchangeDayBox"
            class="action-button"
            type="button"
          >
            <img
              src="@/images/plus.png"
              alt="Plus"
              class="plus-icon"
              width="35"
              height="35"
            />
            Neuer Exchange Day
          </button>
          <button
            @click="toggleWorkshopBox"
            class="action-button"
            type="button"
          >
            <img
              src="@/images/plus.png"
              alt="Plus"
              class="plus-icon"
              width="35"
              height="35"
            />
            Neuer Workshop
          </button>
          <button
            @click="toggleResourceBox"
            class="action-button"
            type="button"
          >
            <img
              src="@/images/plus.png"
              alt="Plus"
              class="plus-icon"
              width="35"
              height="35"
            />
            Neue Resource
          </button>
          <button
            @click="toggleLocationBox"
            class="action-button"
            type="button"
          >
            <img
              src="@/images/plus.png"
              alt="Plus"
              class="plus-icon"
              width="35"
              height="35"
            />
            Neue Location
          </button>
        </div>

        <div class="update-buttons-group">
          <button
            @click="toggleUpdateResourceBox"
            class="action-button"
            type="button"
          >
            <img
              src="@/images/plus.png"
              alt="Plus"
              class="plus-icon"
              width="35"
              height="35"
            />
            Ressource bearbeiten
          </button>
          <button
            @click="SelectEventToUpdate"
            class="action-button"
            type="button"
          >
            <img
              src="@/images/plus.png"
              alt="Plus"
              class="plus-icon"
              width="35"
              height="35"
            />
            Event bearbeiten
          </button>
          <button
            @click="SelectExchangeDayToUpdate"
            class="action-button"
            type="button"
          >
            <img
              src="@/images/plus.png"
              alt="Plus"
              class="plus-icon"
              width="35"
              height="35"
            />
            ExchangeDay bearbeiten
          </button>
        </div>
      </div>

      <div class="form-container">
        <!-- Exchange Day form -->
        <transition name="roll">
          <ExchangeDayForm
            v-model:showExchangeDayBox="showExchangeDayBox"
            :exchange-days="exchangeDays"
            :users="users"
            v-show="showExchangeDayBox"
          />
        </transition>

        <!-- Workshop form -->
        <transition name="roll">
          <WorkshopForm
            v-model:showWorkshopBox="showWorkshopBox"
            :exchange-days="exchangeDays"
            :experience-levels="experienceLevels"
            :all-tags="allTags"
            v-show="showWorkshopBox"
          />
        </transition>

        <!-- Resource form -->
        <transition name="roll">
          <ResourceForm
            v-model:showResourceBox="showResourceBox"
            v-if="showResourceBox"
          />
        </transition>

        <!-- Location form -->
        <transition name="roll">
          <LocationForm
            v-model:showLocationBox="showLocationBox"
            v-if="showLocationBox"
          />
        </transition>

        <!-- Update Resource form -->
        <transition name="roll">
          <UpdateResource
            v-model:showUpdateResourceBox="showUpdateResourceBox"
            v-if="showUpdateResourceBox"
          />
        </transition>

        <!-- Update Event form -->
        <transition name="roll">
          <UpdateEvent
            v-model:selectEventToUpdate="SelectEventToUpdate"
            v-if="selectEventToUpdate"
          />
        </transition>

        <!-- Update Exchange Day form -->
        <transition name="roll">
          <UpdateExchangeDay
            v-model:selectExchangeDayToUpdate="selectExchangeDayToUpdate"
            v-if="selectExchangeDayToUpdate"
          />
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import "@/assets/event-planning.css";
import { ref, onMounted } from "vue";
import config from "@/config";
import WorkshopForm from "@/components/adminPanel/WorkshopForm.vue";
import ExchangeDayForm from "@/components/adminPanel/ExchangeDayForm.vue";
import ResourceForm from "@/components/adminPanel/ResourceForm.vue";
import LocationForm from "@/components/adminPanel/LocationForm.vue";
import UpdateResource from "./UpdateResource.vue";
import UpdateEvent from "./UpdateEvent.vue";
import UpdateExchangeDay from "./UpdateExchangeDay.vue";

const showWorkshopBox = ref(false);
const showExchangeDayBox = ref(false);
const showResourceBox = ref(false);
const showLocationBox = ref(false);
const showUpdateResourceBox = ref(false);
const selectEventToUpdate = ref(false);
const selectExchangeDayToUpdate = ref(false);

const exchangeDays = ref([]);
const users = ref([]);
const experienceLevels = ref([]);
const allTags = ref([]);
const filteredTags = ref([]);

onMounted(async () => {
  await fetchData();
});

const fetchData = async () => {
  try {
    const [exchangeDaysResponse, usersResponse, levelsResponse] =
      await Promise.all([
        fetch(`${config.apiBaseUrl}/exchange-days`),
        fetch(`${config.apiBaseUrl}/users`),
        fetch(`${config.apiBaseUrl}/events/experience-levels`),
      ]);
    if (!exchangeDaysResponse.ok) {
      throw new Error("Fehler beim Laden der Exchange Days");
    }
    if (!usersResponse.ok) {
      throw new Error("Fehler beim Laden der Benutzer");
    }
    if (!levelsResponse.ok) {
      throw new Error("Fehler beim Laden der Erfahrungslevel.");
    }
    fetchTags();

    exchangeDays.value = await exchangeDaysResponse.json();
    users.value = await usersResponse.json();
    experienceLevels.value = await levelsResponse.json();
  } catch (error) {
    console.error(error.message, error);
  }
};

const fetchTags = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/tags`);
    if (response.ok) {
      const tags = await response.json();
      allTags.value = tags.map((tag) => tag.name);
      filteredTags.value = [...allTags.value];
    } else {
      console.error("Fehler beim Abrufen der Tags.");
    }
  } catch (error) {
    console.error("Fehler beim Abrufen der Tags:", error);
  }
};

const toggleWorkshopBox = () => {
  resetForms();
  showWorkshopBox.value = !showWorkshopBox.value;
};

const toggleExchangeDayBox = () => {
  resetForms();
  showExchangeDayBox.value = !showExchangeDayBox.value;
};

const toggleResourceBox = () => {
  resetForms();
  showResourceBox.value = !showResourceBox.value;
};

const toggleLocationBox = () => {
  resetForms();
  showLocationBox.value = !showLocationBox.value;
};

const toggleUpdateResourceBox = () => {
  resetForms();
  showUpdateResourceBox.value = !showUpdateResourceBox.value;
};

const SelectEventToUpdate = () => {
  resetForms();
  selectEventToUpdate.value = !selectEventToUpdate.value;
};

const SelectExchangeDayToUpdate = () => {
  resetForms();
  selectExchangeDayToUpdate.value = !selectExchangeDayToUpdate.value;
};

const resetForms = () => {
  showWorkshopBox.value = false;
  showExchangeDayBox.value = false;
  showResourceBox.value = false;
  showLocationBox.value = false;
  showUpdateResourceBox.value = false;
  selectEventToUpdate.value = false;
  selectExchangeDayToUpdate.value = false;
};
</script>

<style scoped>
.create-buttons-group {
  background-color: #f0f0f0;
  padding: 10px;
  border-radius: 5px;
  margin: 0px;
}

.update-buttons-group {
  background-color: #e0e0e0;
  padding: 10px;
  border-radius: 5px;
  margin-top: 10px;
}

.action-button {
  margin: 5px;
}
</style>
