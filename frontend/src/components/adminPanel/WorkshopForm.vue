<template>
  <div class="create-box">
    <h2 class="login-header">Neues Event</h2>
    <form @submit.prevent="onSubmit">
      <div class="input-group">
        <label for="name">Name</label>
        <input type="text" id="name" v-model="name" required />
      </div>

      <div class="input-group">
        <label for="exchangeDaySelect">Exchange Day</label>
        <select
          id="exchangeDaySelect"
          v-model="exchangeDaySelect"
          @change="updateSelectedExchangeDay"
          required
        >
          <option value="" disabled>
            Bitte wählen Sie einen Exchange Day aus
          </option>
          <option
            v-for="exchangeDay in exchangeDays"
            :key="exchangeDay.id"
            :value="exchangeDay.id"
          >
            {{ exchangeDay.name }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <label for="date">Datum</label>
        <input
          type="date"
          id="date"
          v-model="date"
          :min="selectedExchangeDay?.startDate || ''"
          :max="selectedExchangeDay?.endDate || ''"
          :disabled="!exchangeDaySelect"
          required
        />
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
          <option
            v-for="availableRoom in filteredRooms"
            :key="availableRoom.id"
            :value="availableRoom.id"
          >
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
        <TagInput
          v-if="selectedTags && allTags"
          v-model="selectedTags"
          :available-tags="allTags"
          :tagSelect="false"
          @new-tag="handleNewTag"
          @keydown.enter.prevent
        />
      </div>

      <div class="checkbox-container">
        <label for="inviteOnly">Nur über Einladungen?</label>
        <input type="checkbox" id="inviteOnly" v-model="inviteOnly" />
      </div>

      <button type="submit" class="action-button" @click="createWorkshop">
        Event erstellen
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";
import TagInput from "../profile/TagInput.vue";
import { useAuth } from "@/util/auth";

const userId = useAuth().getUserId();
const name = ref("");
const exchangeDaySelect = ref("");
const date = ref("");
const startTime = ref("00:00");
const endTime = ref("01:00");
const room = ref<any>(null);
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
        `Bitte wählen Sie einen gültigen Exchange day aus`,
        "info",
        faXmark,
        5,
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
        "Fehler",
        `Wählen Sie ein Datum zwischen dem Start- und Enddatum des ausgewählten Exchange Days.`,
        "error",
        faXmark,
        5,
      ),
    );
    return;
  }
  try {
    const workshopData = {
      name: name.value,
      date: date.value,
      startTime: startTime.value,
      endTime: endTime.value,
      roomId: room.value,
      description: description.value,
      exchangeDayId: exchangeDaySelect.value,
      organizerId: userId,
      recommendedExperience: selectedExperienceLevel.value,
      tags: selectedTags.value.map((t) => t.name),
      resources: selectedResources.value,
      inviteOnly: inviteOnly.value,
    };

    const response = await api.post(`/events`, workshopData);

    if (response.status === 201) {
      const data = await response.data;
      showToast(
        new Toast(
          "Erfolg",
          `Der Workshop ${data.name} wurde erfolgreich angelegt.`,
          "success",
          faCheck,
          5,
        ),
      );
      resetWorkshopForm();
    } else {
      showToast(
        new Toast(
          "Fehler",
          `Das Event konnte nicht angelegt werden.`,
          "error",
          faXmark,
          5,
        ),
      );
    }
  } catch (error) {
    console.error(error);
    showToast(
      new Toast(
        "Fehler",
        `Das Event konnte nicht angelegt werden.`,
        "error",
        faXmark,
        5,
      ),
    );
  }
};

const selectedExchangeDay = computed(() =>
  exchangeDays.value.find((day) => day.id === exchangeDaySelect.value),
);

const handleNewTag = (newTag) => {
  if (newTag.key === "Enter") {
    newTag.preventDefault();
  }
  api.post(`/tags`, newTag).then((response) => {
    allTags.value.push(response.data);
  });
};

/**
 * Updates the available rooms based on the location of the selected Exchange Day.
 */
const updateFilteredRooms = async () => {
  if (selectedExchangeDay.value) {
    const exchangeDayLocationId = selectedExchangeDay.value.location.id;

    try {
      const response = await api.get(
        `/resources/location/${exchangeDayLocationId}`,
      );

      if (response.status === 200) {
        const rooms = await response.data;
        filteredRooms.value = rooms;
      } else {
        showToast(
          new Toast(
            "Fehler",
            `Ressourcen für den Standort konnten nicht geladen werden.`,
            "error",
            faXmark,
            5,
          ),
        );
        filteredRooms.value = [];
      }
    } catch (error) {
      showToast(
        new Toast(
          "Fehler",
          `Ressourcen für den Standort konnten nicht geladen werden.`,
          "error",
          faXmark,
          5,
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
    const daysResponse = await api.get(`/exchange-days`);
    const roomsResponse = await api.get(`/resources/type/ROOM`);
    if (roomsResponse.status !== 200)
      showToast(
        new Toast(
          "Fehler",
          `Räumlichkeiten konnten nicht geladen werden.`,
          "error",
          faXmark,
          5,
        ),
      );
    availableRooms.value = await roomsResponse.data;
    if (daysResponse.status !== 200)
      showToast(
        new Toast(
          "Fehler",
          `Exchange Days konnten nicht geladen werden.`,
          "error",
          faXmark,
          5,
        ),
      );
    exchangeDays.value = await daysResponse.data;

    const tagsResponse = await api.get(`/tags`);
    if (tagsResponse.status !== 200)
      showToast(
        new Toast(
          "Fehler",
          `Event-Tags konnten nicht geladen werden.`,
          "error",
          faXmark,
          5,
        ),
      );
    allTags.value = await tagsResponse.data;
    filteredTags.value = [...allTags.value];

    const levelsResponse = await api.get(`/events/experience-levels`);
    if (levelsResponse.status === 200)
      experienceLevels.value = await levelsResponse.data;
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `Erfahrungslevel konnten nicht geladen werden.`,
        "error",
        faXmark,
        5,
      ),
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
  accent-color: #009ee2;
}

button.login-button {
  margin-top: 20px;
}
</style>
