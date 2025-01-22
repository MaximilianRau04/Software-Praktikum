<template>
  <div class="container">
    <div class="box">
      <div class="action-buttons">
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
        <button @click="toggleWorkshopBox" class="action-button" type="button">
          <img
            src="@/images/plus.png"
            alt="Plus"
            class="plus-icon"
            width="35"
            height="35"
          />
          Neuer Workshop
        </button>
        <button @click="toggleResourceBox" class="action-button" type="button">
          <img
            src="@/images/plus.png"
            alt="Plus"
            class="plus-icon"
            width="35"
            height="35"
          />
          Neue Resource
        </button>
      </div>

      <div class="form-container">
        <!-- Exchange Day form -->
        <transition name="roll">
          <ExchangeDayForm
            v-model:showExchangeDayBox="showExchangeDayBox"
            :exchange-days="exchangeDays"
            :users="users"
            @create-exchange-day="createExchangeDay"
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
            @create-workshop="createWorkshop"
            v-show="showWorkshopBox"
          />
        </transition>

        <!-- Resource form -->
        <transition name="roll">
          <ResourceForm
            v-model:showResourceBox="showResourceBox"
            @create-Resource="createResource"
            v-if="showResourceBox"
          />
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import "@/assets/event-planning.css";
import { ref, onMounted, computed, watch } from "vue";
import config from "@/config";
import WorkshopForm from "@/components/adminPanel/WorkshopForm.vue";
import ExchangeDayForm from "@/components/adminPanel/ExchangeDayForm.vue";
import ResourceForm from "@/components/adminPanel/ResourceForm.vue";

const showWorkshopBox = ref(false);
const showExchangeDayBox = ref(false);
const showResourceBox = ref(false);

const filteredTags = ref([]);

const exchangeDays = ref([]);
const users = ref([]);
const experienceLevels = ref([]);
const germanExperienceLevels = {
  ALL_LEVELS: "Alle Ebenen",
  JUNIOR: "Junior",
  SENIOR: "Senior",
  EXPERT: "Experte",
};

const allTags = ref([]);
const selectedTags = ref([]);

onMounted(async () => {
  await fetchData();
});

/**
 * Fetches exchange days, users and experience levels from the API.
 */
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

/**
 * Fetches all tags from the API.
 */
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

/**
 * Toggles the visibility of the workshop form.
 */
const toggleWorkshopBox = () => {
  showWorkshopBox.value = !showWorkshopBox.value;
  if (showWorkshopBox.value) showExchangeDayBox.value = false;
  showResourceBox.value = false;
};

/**
 * Toggles the visibility of the exchange day form.
 */
const toggleExchangeDayBox = () => {
  showExchangeDayBox.value = !showExchangeDayBox.value;
  if (showExchangeDayBox.value) showWorkshopBox.value = false;
  showResourceBox.value = false;
};

/**
 * Toggles the visibility of the resource form.
 */
const toggleResourceBox = () => {
  showResourceBox.value = !showResourceBox.value;
  if (showResourceBox.value) showWorkshopBox.value = false;
  showExchangeDayBox.value = false;
};

const createWorkshop = () => {
  console.log("Workshop wird erstellt...");
};

const createExchangeDay = () => {
  console.log("Exchange Day wird erstellt...");
};

const createResource = () => {
  console.log("Resource wird erstellt...");
};
</script>
