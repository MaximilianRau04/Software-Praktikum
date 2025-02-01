<template>
  <div class="admin-container">

    <div class="admin-sidebar">
      <div class="sidebar-section">
        <h3 class="sidebar-title">Erstellen</h3>
        <button @click="toggleLocationBox" class="sidebar-btn">
          <span>Location</span>
        </button>
        <button @click="toggleResourceBox" class="sidebar-btn">
          <span>Ressource</span>
        </button>
        <button @click="toggleExchangeDayBox" class="sidebar-btn">
          <span>Exchange Day</span>
        </button>
        <button @click="toggleWorkshopBox" class="sidebar-btn">
          <span>Event</span>
        </button>
      </div>

      <div class="sidebar-section">
        <h3 class="sidebar-title">Bearbeiten</h3>
        <button @click="toggleUpdateResourceBox" class="sidebar-btn">
          <span>Ressource</span>
        </button>
        <button @click="SelectEventToUpdate" class="sidebar-btn">
          <span>Event</span>
        </button>
        <button @click="SelectExchangeDayToUpdate" class="sidebar-btn">
          <span>Exchange Day</span>
        </button>
      </div>

      <div class="sidebar-section">
        <h3 class="sidebar-title">Events</h3>
        <button @click="toggleEventListBox" class="sidebar-btn">
          <span>Feedbacks</span>
        </button>

        <button @click="toggleEventInviteOnlyListBox" class="sidebar-btn">
          <span>Einladungs-Events</span>
        </button>
      </div>

      <div class="sidebar-section">
        <h3 class="sidebar-title">CSV</h3>
        
        <button class="sidebar-btn" @click="importExampleResources" >
        Beispiel-Import von Resourcen
        </button>
        <button class="sidebar-btn" @click="triggerFileUpload">
        CSV Import von Resourcen
      </button>
      <input
        ref="fileInput"
        type="file"
        accept=".csv"
        @change="handleFileUpload"
        style="display: none"
      />
      <button class="sidebar-btn" @click="downloadCsvOfResources" >
        CSV Export von Resourcen
      </button>
      
      </div>
    </div>

    <div class="admin-content">
      <transition name="slide-fade">
        <div class="form-wrapper" v-if="showExchangeDayBox">
          <ExchangeDayForm v-model:showExchangeDayBox="showExchangeDayBox" :exchange-days="exchangeDays" :users="users"/>
        </div>
      </transition>

      <transition name="slide-fade">
        <div class="form-wrapper" v-if="showWorkshopBox">
          <WorkshopForm v-model:showWorkshopBox="showWorkshopBox" :exchange-days="exchangeDays" :experience-levels="experienceLevels" :all-tags="allTags"/>
        </div>
      </transition>

      <transition name="slide-fade">
        <div class="form-wrapper" v-if="showResourceBox">
          <ResourceForm v-model:showResourceBox="showResourceBox"/>
        </div>
      </transition>

      <transition name="slide-fade">
        <div class="form-wrapper" v-if="showLocationBox">
          <LocationForm v-model:showLocationBox="showLocationBox"/>
        </div>
      </transition>

      <transition name="slide-fade">
        <div class="form-wrapper" v-if="showUpdateResourceBox">
          <UpdateResource v-model:showUpdateResourceBox="showUpdateResourceBox"/>
        </div>
      </transition>

      <transition name="slide-fade">
        <div class="form-wrapper" v-if="selectEventToUpdate">
          <UpdateEvent v-model:selectEventToUpdate="selectEventToUpdate"/>
        </div>
      </transition>

      <transition name="slide-fade">
        <div class="form-wrapper" v-if="selectExchangeDayToUpdate">
          <UpdateExchangeDay v-model:selectExchangeDayToUpdate="selectExchangeDayToUpdate"/>
        </div>
      </transition>

      <transition name="slide-fade">
        <div class="form-wrapper" v-if="showEventListBox">
          <EventList v-model:showEventListBox="showEventListBox"/>
        </div>
      </transition>

      <transition name="slide-fade">
        <div class="form-wrapper" v-if="showEventInviteOnlyListBox">
          <EventInviteOnlyList v-model:showEventInviteOnlyListBox="showEventInviteOnlyListBox"/>
        </div>
      </transition>
    </div>
    
    <input
      ref="fileInput"
      type="file"
      accept=".csv"
      @change="handleFileUpload"
      style="display: none"
    />
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
import api from "@/util/api";


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
        api.get(`/exchange-days`),
        api.get(`/users`),
        api.get(`/events/experience-levels`),
      ]);
    if (exchangeDaysResponse.status !== 200) {
      showToast(
        new Toast("Error", "Fehler beim Laden der Exchange Days", "error")
      );
    }
    if (usersResponse.status !== 200) {
      showToast(new Toast("Error", "Fehler beim Laden der Benutzer", "error"));
    }
    if (levelsResponse.status !== 200) {
      showToast(
        new Toast("Error", "Fehler beim Laden der Erfahrungslevel", "error")
      );
    }
    fetchTags();

    exchangeDays.value = await exchangeDaysResponse.data;
    users.value = await usersResponse.data;
    experienceLevels.value = await levelsResponse.data;
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Laden der Daten", "error"));
  }
};

/**
 * Fetches all tags from the backend
 */
const fetchTags = async () => {
  try {
    const response = await api.get(`/tags`);
    if (response.status === 200) {
      const tags = await response.data;
      allTags.value = tags;
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
 * Download CSV file of resources
 */
 async function downloadCsvOfResources() {
  try {
    const response = await api.get('/resources/csv-downloads', {
      responseType: 'blob'
    });

    const blob = response.data;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    showToast(new Toast("Error", "CSV Download fehlgeschlagen!", "error", faXmark, 10));
  }
};

/**
 * Import example resources from CSV
 */
const importExampleResources = async () => {
  try {
    const response = await fetch("/csv/example.csv");
    const text = await response.text();
    
    const UTF8_BOM = new Uint8Array([0xEF, 0xBB, 0xBF]);
    
    const encoder = new TextEncoder();
    const textBytes = encoder.encode(text);
    
    const combinedArray = new Uint8Array(UTF8_BOM.length + textBytes.length);
    combinedArray.set(UTF8_BOM);
    combinedArray.set(textBytes, UTF8_BOM.length);
    
    const blob = new Blob([combinedArray], {
      type: "text/csv;charset=utf-8"
    });
 
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "example.csv";
    
    document.body.appendChild(link);
    link.click();
    
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Fehler beim Download:", error);
    alert("Der Download konnte nicht durchgeführt werden.");
  }
};



const triggerFileUpload = () => {
  fileInput.value?.click();
};

/**
 * Handle file upload and CSV import
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
    await api.post('/resources/csv-import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      responseType: 'text'
    });

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
.admin-container {
  display: flex;
  background-color: white;
  padding-left: 0px;
  padding-top: 0px;
}

.admin-sidebar {
  width: 250px;
  background: #022750;
  padding: 20px;
  color: white;
  position: fixed;
  height: calc(100vh - 60px);
}

.admin-content {
  flex: 1;
  margin-left: 300px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.sidebar-section {
  margin-bottom: 2rem;
}

.sidebar-title {
  color: #95a5a6;
  font-size: 0.9rem;
  padding-left: 10px;
  text-transform: uppercase;
  margin: 1rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #2f4862;
}

.sidebar-btn {
  width: 100%;
  padding: 7px 20px;
  text-align: left;
  color: #ecf0f1;
  background: transparent;
  border: none;
  border-radius: 5px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.sidebar-btn:hover {
  background: #34495e;
  transform: translateX(5px);
}

.form-wrapper {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 100%;
  max-width: 600px;
  margin: 20px auto;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>