<template>
  <div class="create-box">
    <h2 class="login-header">Neuer Workshop</h2>
    <form @submit.prevent="onSubmit">
      <div class="input-group">
        <label for="name">Name</label>
        <input type="text" id="name" v-model="name" required />
      </div>

      <div class="input-group">
        <label for="exchangeDaySelect">Exchange Day</label>
        <select id="exchangeDaySelect" v-model="exchangeDaySelect" @change="updateSelectedExchangeDay" required>
          <option value="" disabled>
            Bitte wählen Sie einen Exchange Day aus
          </option>
          <option v-for="exchangeDay in exchangeDays" :key="exchangeDay.id" :value="exchangeDay.id">
            {{ exchangeDay.name }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="date">Datum</label>
        <input type="date" id="date" v-model="date" :min="selectedExchangeDay?.startDate || ''"
          :max="selectedExchangeDay?.endDate || ''" :disabled="!exchangeDaySelect" required />
      </div>

      <div class="input-group">
        <label for="startTime">Startzeit</label>
        <input type="time" id="startTime" v-model="startTime" required />
      </div>
      <div class="input-group">
        <label for="endTime">Endzeit</label>
        <input type="time" id="endTime" v-model="endTime" required />
      </div>

      <div class="input-group">
        <label for="room">Raum</label>
        <select id="room" v-model="room" :disabled="!filteredRooms.length">
          <option value="" disabled>Bitte wählen Sie einen Raum</option>
          <option v-for="availableRoom in filteredRooms" :key="availableRoom.id" :value="availableRoom.id">
            {{ availableRoom.name }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="description">Beschreibung</label>
        <input type="text" id="description" v-model="description" />
      </div>

      <div class="input-group">
        <label for="experienceLevel">Erfahrungslevel</label>
        <select id="experienceLevel" v-model="selectedExperienceLevel" required>
          <option value="" disabled>
            Bitte wählen Sie ein Erfahrungslevel
          </option>
          <option v-for="level in experienceLevels" :key="level" :value="level">
            {{ germanExperienceLevels[level] }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="tags">Event Tags</label>
        <p>Bitte wählen Sie bis zu 5 Event Tags für Ihr Event aus:</p>

        <input type="text" id="tags" v-model="tagInput" placeholder="Tags eingeben und durch Komma trennen"
          @input="filterTags" @keyup="handleKeyup" :disabled="selectedTags.length >= 5" />

        <div class="tag-chips">
          <span v-for="(tag, index) in selectedTags" :key="index" class="chip">
            {{ tag }}
            <button type="button" class="remove-tag" @click="removeTag(index)">
              &times;
            </button>
          </span>
        </div>

        <div class="tag-list">
          <button v-for="tag in filteredTags" :key="tag" type="button" @click="addTag(tag.name)"
            :disabled="selectedTags.includes(tag)">
            {{ tag.name }}
          </button>
        </div>
      </div>

      <div class="checkbox-container">
        <label for="inviteOnly">Nur über Einladungen?</label>
        <input type="checkbox" id="inviteOnly" v-model="inviteOnly" />
      </div>

      <button type="submit" class="login-button" @click="createWorkshop">
        Workshop erstellen
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import config from "@/config";
import Cookies from "js-cookie";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

const userId = Cookies.get("userId");
const name = ref("");
const exchangeDaySelect = ref("");
const date = ref("");
const startTime = ref("00:00");
const endTime = ref("01:00");
const room= ref<any>(null);
const description = ref("");
const selectedExperienceLevel = ref("");
const selectedTags = ref([]);
const tagInput = ref("");
const filteredTags = ref([]);
const exchangeDays = ref([]);

const selectedResources = ref([]);
const allTags = ref([]);
const availableRooms = ref<any[]>([]);
const filteredRooms = ref<any[]>([]);
const inviteOnly = ref(false);

defineProps({
  exchangeDays: {
    type: Array,
    required: true,
  },
  experienceLevels: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(["update:showWorkshopBox"]);
const apiUrl = `${config.apiBaseUrl}/events`;

const experienceLevels = ref([]);
const germanExperienceLevels = {
  ALL_LEVELS: "Für alle geeignet",
  JUNIOR: "Junior",
  SENIOR: "Senior",
  EXPERT: "Experte",
};

/**
 * Creates a new Workshop using the form data.
 */
const createWorkshop = async () => {
  if (!selectedExchangeDay.value) {
    showToast(
      new Toast(
        "Info",
        `Bitte wählens sie einen gültigen Exchange day aus`,
        "info",
        faXmark,
        10,
      ),
    );
    return;
  }
  const selectedDate = new Date(date.value);
  const start = new Date(selectedExchangeDay.value.startDate);
  const end = new Date(selectedExchangeDay.value.endDate);

  if (selectedDate < start || selectedDate > end) {
    showToast(
      new Toast(
        "Error",
        `Das Datum muss zwischen dem Start- und Enddatum des ausgewählten Exchange Days liegen.`,
        "error",
        faXmark,
        10,
      ),
    );
    return;
  }
  try {
    const response = await fetch(apiUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: name.value,
        date: date.value,
        startTime: startTime.value,
        endTime: endTime.value,
        roomId: room.value,
        description: description.value,
        exchangeDayId: exchangeDaySelect.value,
        organizerId: userId,
        recommendedExperience: selectedExperienceLevel.value,
        tags: selectedTags.value,
        resources: selectedResources.value,
        inviteOnly: inviteOnly.value,
      }),
    });
   console.log(room.value);
    if (response.ok) {
      const data = await response.json();
      showToast(
        new Toast(
          "Success",
          `Workshop erstellt: ${data.name}`,
          "success",
          faCheck,
          5,
        ),
      );
      resetWorkshopForm();
    } else {
      showToast(
        new Toast(
          "Error",
          `Fehler beim Erstellen des Workshops.`,
          "error",
          faXmark,
          10,
        ),
      );
    }
  } catch (error) {
    showToast(
      new Toast(
        "Error",
        `Fehler beim Erstellen des Workshops.`,
        "error",
        faXmark,
        10,
      ),
    );
  }
};

const selectedExchangeDay = computed(() =>
  exchangeDays.value.find((day) => day.id === exchangeDaySelect.value),
);

/**
 * Updates the available rooms based on the location of the selected Exchange Day.
 */
const updateFilteredRooms = async () => {
  if (selectedExchangeDay.value) {
    const exchangeDayLocationId = selectedExchangeDay.value.location.id;

    try {
      const response = await fetch(
        `${config.apiBaseUrl}/resources/location/${exchangeDayLocationId}`,
      );

      if (response.ok) {
        const rooms = await response.json();
        filteredRooms.value = rooms;
      } else {
        showToast(
          new Toast(
            "Error",
            `Fehler beim Laden der Ressourcen für die Location.`,
            "error",
            faXmark,
            10,
          ),
        );
        filteredRooms.value = [];
      }
    } catch (error) {
      showToast(
        new Toast(
          "Error",
          `Fehler beim Abrufen der Räume.`,
          "error",
          faXmark,
          10,
        ),
      );
      filteredRooms.value = [];
    }
  } else {
    filteredRooms.value = [];
  }
};

/**
 * Called when a new Exchange Day is selected.
 */
const updateSelectedExchangeDay = async () => {
  await updateFilteredRooms();
  date.value = "";
  room.value = "";
};

const handleKeyup = (event) => {
  if (event.key === ",") {
    addTagFromInput();
  }
};

/**
 * Adds a tag from the input field to the selected tags.
 */
const addTagFromInput = () => {
  const trimmedInput = tagInput.value.trim().slice(0, -1);

  if (!trimmedInput) return;

  if (
    trimmedInput &&
    !selectedTags.value.includes(trimmedInput) &&
    selectedTags.value.length < 5
  ) {
    selectedTags.value.push(trimmedInput);
  }
  tagInput.value = "";
  filteredTags.value = [...allTags.value];
};

const addTag = (tag) => {
  if (!selectedTags.value.includes(tag) && selectedTags.value.length < 5) {
    selectedTags.value.push(tag);
  }
};

const removeTag = (index) => {
  selectedTags.value.splice(index, 1);
};

/**
 * Filters the tags based on the user input.
 */
const filterTags = () => {
  const query = tagInput.value.toLowerCase();
  filteredTags.value = allTags.value.filter((tag) =>
    tag.name.toLowerCase().includes(query),
  );
};

/**
 * Sets the start time to the current hour.
 */
const setStartTimeToCurrentHour = () => {
  const currentHour = new Date().getHours();
  const formattedHour = currentHour < 10 ? `0${currentHour}` : currentHour;
  startTime.value = `${formattedHour}:00`;
};

/*
 * Updates the end time based on the selected start time.
 */
const updateEndTime = (startTime) => {
  const [startHour, startMinute] = startTime.split(":").map(Number);

  const newEndHour = (startHour + 1) % 24;
  const formattedEndHour = newEndHour < 10 ? `0${newEndHour}` : newEndHour;
  const formattedEndTime = `${formattedEndHour}:${startMinute < 10 ? "0" + startMinute : startMinute}`;

  endTime.value = formattedEndTime;
};

const onSubmit = () => {
  const workshopData = {
    name: name.value,
    exchangeDayId: exchangeDaySelect.value,
    date: date.value,
    startTime: startTime.value,
    endTime: endTime.value,
    room: room.value,
    description: description.value,
    experienceLevel: selectedExperienceLevel.value,
    tags: selectedTags.value,
    selectedResources: selectedResources.value,
    inviteOnly: inviteOnly.value,
  };
};

/**
 * Resets the Workshop form fields.
 */
const resetWorkshopForm = () => {
  name.value = "";
  date.value = "";
  startTime.value = "";
  endTime.value = "";
  room.value = null;
  description.value = "";
  exchangeDaySelect.value = "";
  selectedExperienceLevel.value = "";
  tagInput.value = "";
  selectedTags.value = [];
  filteredTags.value = [...allTags.value];
  selectedResources.value = [];
  inviteOnly.value = false;
  emit("update:showWorkshopBox", false);
};

/**
 * Fetches the available rooms and exchange days when the component is mounted.
 */
 onMounted(async () => {
  try {
    const daysResponse = await fetch(`${config.apiBaseUrl}/exchange-days`);
    const roomsResponse = await fetch(
      `${config.apiBaseUrl}/resources/type/ROOM`,
    );
    if (!roomsResponse.ok)
      showToast(
        new Toast(
          "Error",
          `Fehler beim Abrufen der Räume.`,
          "error",
          faXmark,
          10,
        ),
      );
    availableRooms.value = await roomsResponse.json();
    if (!daysResponse.ok)
      showToast(
        new Toast(
          "Error",
          `Fehler beim Abrufen der Exchange days.`,
          "error",
          faXmark,
          10,
        ),
      );
    exchangeDays.value = await daysResponse.json();

    const tagsResponse = await fetch(`${config.apiBaseUrl}/tags`);
    if (!tagsResponse.ok)
      showToast(
        new Toast(
          "Error",
          `Fehler beim Abrufen der Tags.`,
          "error",
          faXmark,
          10,
        ),
      );
    allTags.value = await tagsResponse.json();
    filteredTags.value = [...allTags.value];

    const levelsResponse = await fetch(
      `${config.apiBaseUrl}/events/experience-levels`,
    );
    if (levelsResponse.ok)
    experienceLevels.value = await levelsResponse.json();
  } catch (error) {
    showToast(
      new Toast("Error", `Fehler beim Abrufen der Daten`, "error", faXmark, 10),
    );
  }

  setStartTimeToCurrentHour();
});

watch(startTime, (newStartTime) => {
  updateEndTime(newStartTime);
});
</script>

<style scoped>
.tag-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.chip {
  background-color: #e0e0e0;
  padding: 5px 10px;
  border-radius: 15px;
  display: flex;
  align-items: center;
}

.remove-tag {
  background: none;
  border: none;
  color: red;
  cursor: pointer;
  margin-left: 5px;
}

.tag-list button {
  background-color: #f0f0f0;
  border: none;
  padding: 5px 10px;
  margin: 5px;
  border-radius: 5px;
  cursor: pointer;
}

.tag-list button:disabled {
  background-color: #ccc;
}

.tag-list {
  margin-bottom: 20px;
}

.checkbox-container {
  display: flex;
  align-items: center;
}

.checkbox-container label {
  margin-right: 10px;
}

.checkbox-container input[type="checkbox"] {
  transform: scale(1.5);
  margin-left: 10px;
  accent-color: #009EE2;
}

button.login-button {
  margin-top: 20px;
}
</style>
