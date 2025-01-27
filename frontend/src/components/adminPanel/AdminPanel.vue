<template>
  <div class="admin-container">
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
          <button
            @click="toggleEventListBox"
            class="action-button"
            type="button"
          >
            <img 
            src="@/images/plus.png"
            alt="List" 
            class="plus-icon" 
            width="35" 
            height="35" />
            Event Feedbacks
          </button>

          <button
            @click="toggleEventInviteOnlyListBox"
            class="action-button"
            type="button"
          >
            <img 
            src="@/images/plus.png"
            alt="List" 
            class="plus-icon" 
            width="35" 
            height="35" />
            Events nur auf Einladung
          </button>

        </div>
      </div>

      <div class="form-container">
        <!-- exchange day form -->
        <transition name="roll">
          <ExchangeDayForm
            v-model:showExchangeDayBox="showExchangeDayBox"
            :exchange-days="exchangeDays"
            :users="users"
            v-show="showExchangeDayBox"
          />
        </transition>

        <!-- workshop form -->
        <transition name="roll">
          <WorkshopForm
            v-model:showWorkshopBox="showWorkshopBox"
            :exchange-days="exchangeDays"
            :experience-levels="experienceLevels"
            :all-tags="allTags"
            v-show="showWorkshopBox"
          />
        </transition>

        <!-- resource form -->
        <transition name="roll">
          <ResourceForm
            v-model:showResourceBox="showResourceBox"
            v-if="showResourceBox"
          />
        </transition>

        <!-- location form -->
        <transition name="roll">
          <LocationForm
            v-model:showLocationBox="showLocationBox"
            v-if="showLocationBox"
          />
        </transition>

        <!-- update Resource form -->
        <transition name="roll">
          <UpdateResource
            v-model:showUpdateResourceBox="showUpdateResourceBox"
            v-if="showUpdateResourceBox"
          />
        </transition>

        <!-- update Event form -->
        <transition name="roll">
          <UpdateEvent
            v-model:selectEventToUpdate="SelectEventToUpdate"
            v-if="selectEventToUpdate"
          />
        </transition>

        <!-- update Exchange Day form -->
        <transition name="roll">
          <UpdateExchangeDay
            v-model:selectExchangeDayToUpdate="selectExchangeDayToUpdate"
            v-if="selectExchangeDayToUpdate"
          />
        </transition>

        <!-- event feedbacks -->
        <transition name="roll">
          <EventList
            v-model:showEventListBox="showEventListBox"
            v-if="showEventListBox"
          />
        </transition>

        <!-- events invite ony -->
        <transition name="roll">
          <EventInviteOnlyList
            v-model:showEventInvityOnlyListBox="showEventInviteOnlyListBox"
            v-if="showEventInviteOnlyListBox"
          />
        </transition>

      </div>
      <div class="csv-actions">
        <button  class="csv-button" @click="triggerFileUpload" v-if="areAllBoxesHidden()">
          Ressourcen aus CSV importieren
        </button>
        <input
          ref="fileInput"
          type="file"
          accept=".csv"a
          @change="handleFileUpload"
          style="display: none"
        />
        <button class="csv-button" @click="downloadCsvOfResources"  v-if="areAllBoxesHidden()">
          Ressourcen als CSV downloaden
        </button>
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
import EventInviteOnlyList from "./EventInviteOnlyList.vue";
import EventList from "./EventList.vue";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

const showWorkshopBox = ref(false);
const showExchangeDayBox = ref(false);
const showResourceBox = ref(false);
const showLocationBox = ref(false);
const showUpdateResourceBox = ref(false);
const showEventInviteOnlyListBox = ref(false);
const showEventListBox = ref(false);
const selectEventToUpdate = ref(false);
const selectExchangeDayToUpdate = ref(false);

const exchangeDays = ref([]);
const users = ref([]);
const experienceLevels = ref([]);
const allTags = ref([]);
const filteredTags = ref([]);
const fileInput = ref<HTMLInputElement | null>(null);
const allForms = {
  showExchangeDayBox,
  showWorkshopBox,
  showResourceBox,
  showLocationBox,
  showUpdateResourceBox,
  selectEventToUpdate,
  selectExchangeDayToUpdate,
  showEventListBox,
  showEventInviteOnlyListBox,
};

onMounted(async () => {
  await fetchData();
});

/**
 * Fetches all exchange days, users and experience levels from the backend
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
      showToast(
        new Toast("Error", "Fehler beim Laden der Exchange Days", "error")
      );
    }
    if (!usersResponse.ok) {
      showToast(new Toast("Error", "Fehler beim Laden der Benutzer", "error"));
    }
    if (!levelsResponse.ok) {
      showToast(
        new Toast("Error", "Fehler beim Laden der Erfahrungslevel", "error")
      );
    }
    fetchTags();

    exchangeDays.value = await exchangeDaysResponse.json();
    users.value = await usersResponse.json();
    experienceLevels.value = await levelsResponse.json();
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Laden der Daten", "error"));
  }
};

/**
 * Fetches all tags from the backend
 */
const fetchTags = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/tags`);
    if (response.ok) {
      const tags = await response.json();
      allTags.value = tags.map((tag) => tag.name);
      filteredTags.value = [...allTags.value];
    } else {
      showToast(new Toast("Error", "Fehler beim Abrufen der Tags", "error"));
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Abrufen der Tags", "error"));
  }
};

const toggleWorkshopBox = () => {
  resetForms(showWorkshopBox);
  showWorkshopBox.value = !showWorkshopBox.value;
};

const toggleExchangeDayBox = () => {
  resetForms(showExchangeDayBox);
  showExchangeDayBox.value = !showExchangeDayBox.value;
};

const toggleResourceBox = () => {
  resetForms(showResourceBox);
  showResourceBox.value = !showResourceBox.value;
};

const toggleLocationBox = () => {
  resetForms(showLocationBox);
  showLocationBox.value = !showLocationBox.value;
};

const toggleUpdateResourceBox = () => {
  resetForms(showUpdateResourceBox);
  showUpdateResourceBox.value = !showUpdateResourceBox.value;
};

const toggleEventListBox = () => {
  resetForms(showEventListBox);
  showEventListBox.value = !showEventListBox.value;
};

const toggleEventInviteOnlyListBox = () => {
  resetForms(showEventInviteOnlyListBox);
  showEventInviteOnlyListBox.value = !showEventInviteOnlyListBox.value;
};

const SelectEventToUpdate = () => {
  resetForms(selectEventToUpdate);
  selectEventToUpdate.value = !selectEventToUpdate.value;
};

const SelectExchangeDayToUpdate = () => {
  resetForms(selectExchangeDayToUpdate);
  selectExchangeDayToUpdate.value = !selectExchangeDayToUpdate.value;
};

const areAllBoxesHidden = () => {
  return Object.values(allForms).every((box) => !box.value);
};


/**
 * Resets all forms
 */
const resetForms = (currentForm) => {
  for (const form in allForms) {
    if (allForms[form] === currentForm) continue;
    allForms[form].value = false;
  }
};

/**
 * download CSV file of todos
 */
async function downloadCsvOfResources() {
  try {
    const response = await fetch(
      `${config.apiBaseUrl}/resources/csv-downloads`
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
      new Toast("Error", "CSV Download fehlgeschlagen!", "error", faXmark, 10)
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
    await fetchData();
  } catch (error) {
    showToast(new Toast("Error", "Import fehlgeschlagen", "error"));
  } finally {
    target.value = "";
  }
};
</script>

<style scoped>
.action-button {
  margin: 5px;
}

.create-buttons-group {
  background-color: #e0e0e0;
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

.csv-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-top: 20px;
}

.csv-button {
  background-color: #003e81;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
  font-size: 14px;
}

.csv-button:hover {
  background-color: #013368;
}
</style>
