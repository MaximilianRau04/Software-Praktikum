<template>
  <div class="event-page">
    <div class="search-bar-container">
      <input
        type="text"
        v-model="searchTerm"
        placeholder="Suche nach Events..."
        class="search-bar"
      />
    </div>
    <div class="input-group">
      <label for="exchangeDaySelect">ExchangeDay auswählen</label>
      <select
        id="exchangeDaySelect"
        v-model="selectedExchangeDay"
        @change="handleExchangeDayChange"
        class="exchange-day-select"
      >
        <option :value="null">Alle Events</option>
        <option
          v-for="exchangeDay in exchangeDays"
          :key="exchangeDay.id"
          :value="exchangeDay"
        >
          {{ exchangeDay.name }} ({{ formatDate(exchangeDay.startDate) }} -
          {{ formatDate(exchangeDay.endDate) }})
        </option>
      </select>
    </div>
    <div class="event-columns">
      <div class="event-column list-left">
        <h2>Events nur auf Einladung</h2>
        <div v-if="filteredEvents.length === 0" class="empty-state">
          Keine Events gefunden
        </div>
        <div v-else>
          <div
            v-for="event in filteredEvents"
            :key="event.id"
            class="event-details"
          >
            <h3>
              <strong>{{ event.name }}</strong>
            </h3>
            <p>{{ event.description }}</p>
            <p>{{ formatDate(event.date) }}</p>
            <div class="tags-container">
              <span
                v-if="event.tags.length > 0"
                v-for="tag in event.tags.slice(0, 5)"
                :key="tag.id"
                class="tag-chip"
              >
                <TagIcon class="icon-tag" />
                {{ tag.name }}
              </span>
            </div>
            <button @click="showDetails(event.id)" class="register-button">
              Details
            </button>
            <button @click="openUserModal(event.id)" class="add-users-button">
              Benutzer hinzufügen
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal für Benutzerauswahl -->
    <div v-if="isModalOpen" class="modal">
      <div class="modal-content">
        <button
          @click="closeModal"
          class="close-modal-icon"
          aria-label="Close User Selection Modal"
        >
          ✕
        </button>

        <h3>Benutzer auswählen</h3>
        <ul>
          <li
            v-for="user in filteredUsers"
            :key="user.id"
            @click="toggleUserSelection(user)"
            :class="{ selected: selectedUsers.includes(user) }"
          >
            {{ user.username }}
          </li>
        </ul>

        <button @click="addUsersToEvent" class="confirm-button">
          Benutzer hinzufügen
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { showToast, Toast } from "@/types/toasts";
import api from "@/util/api";
import { TagIcon } from "@heroicons/vue/24/outline";
import "@/assets/adminPanel/event-list.css";

const events = ref([]);
const users = ref([]);
const selectedUsers = ref([]);
const searchTerm = ref("");
const isModalOpen = ref(false);
const eventIdForUserSelection = ref(null);
const router = useRouter();
const registeredUsers = ref([]);
const filteredUsers = computed(() => {
  return users.value.filter(
    (user) => !registeredUsers.value.some((regUser) => regUser.id === user.id),
  );
});
const exchangeDays = ref([]);
const selectedExchangeDay = ref(null);

onMounted(async () => {
  await fetchEvents();
  await fetchExchangeDays();
});

/**
 * Fetches a list of all events.
 */
const fetchEvents = async () => {
  try {
    const response = await api.get(`/events`);
    if (response.status !== 200) {
      throw new Error("Fehler beim Laden der Events");
    }
    events.value = await response.data.map((event) => ({
      ...event,
      description: event.description,
      date: event.date,
    }));

    for (const event of events.value) {
      event.tags = await fetchTagsForEvent(event.id);
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Laden der Events", "error"));
  }
};

/**
 * Fetches a list of all ExchangeDays.
 */
const fetchExchangeDays = async () => {
  try {
    const response = await api.get(`/exchange-days`);
    if (response.status === 200) {
      exchangeDays.value = await response.data;
    } else {
      throw new Error("Fehler beim Abrufen der ExchangeDays");
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Abrufen der ExchangeDays.", "error"),
    );
  }
};

/**
 * Fetches the details of the selected ExchangeDay.
 */
const handleExchangeDayChange = async () => {
  if (selectedExchangeDay.value) {
    await fetchEventsByExchangeDay();
  } else {
    await fetchEvents();
  }
};

/**
 * Fetches events for the selected ExchangeDay.
 */
const fetchEventsByExchangeDay = async () => {
  try {
    const response = await api.get(
      `/exchange-days/${selectedExchangeDay.value.id}/events`,
    );
    if (response.status === 200) {
      events.value = await response.data;

      await Promise.all(
        events.value.map(async (event) => {
          event.tags = await fetchTagsForEvent(event.id);
        }),
      );
    } else {
      throw new Error("Fehler beim Abrufen der Events für den ExchangeDay");
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Abrufen der Events.", "error"));
  }
};

/**
 * Fetches tags for a specific event.
 * @param eventId The ID of the event.
 */
const fetchTagsForEvent = async (eventId) => {
  try {
    const res = await api.get(`/events/${eventId}/tags`);
    if (res.status !== 200) throw new Error("Failed to fetch tags");
    return await res.data;
  } catch (error) {
    showToast(
      new Toast(
        "Error",
        `Fehler beim Laden der Tags für Event ${eventId}`,
        "error",
      ),
    );
    return [];
  }
};

/**
 * Fetches a list of all users.
 */
const fetchUsers = async () => {
  try {
    const response = await api.get(`/users`);
    if (response.status !== 200) {
      throw new Error("Fehler beim Laden der Benutzer");
    }
    users.value = await response.data;
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Laden der Benutzer", "error"));
  }
};

/**
 * Fetches the registrations of all users for a specific event.
 * @param eventId The ID of the event.
 */
const fetchUserRegistrations = async (eventId) => {
  try {
    const registrations = await Promise.all(
      users.value.map(async (user) => {
        const response = await api.get(`/users/${user.id}/registeredEvents`);
        if (response.status !== 200) {
          showToast(
            new Toast(
              "Fehler",
              `Fehler bei der Überprüfung der Registrierung für ${user.username}`,
              "error",
            ),
          );
        }
        const userEvents = await response.data;
        return userEvents.some((event) => event.id === eventId);
      }),
    );

    registeredUsers.value = users.value.filter(
      (user, index) => registrations[index],
    );
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        "Fehler beim Überprüfen der Registrierungen",
        "error",
      ),
    );
  }
};

const openUserModal = async (eventId) => {
  eventIdForUserSelection.value = eventId;
  isModalOpen.value = true;
  await fetchUsers();
  await fetchUserRegistrations(eventId);
};

const closeModal = () => {
  isModalOpen.value = false;
  selectedUsers.value = [];
};

const toggleUserSelection = (user) => {
  if (selectedUsers.value.includes(user)) {
    selectedUsers.value = selectedUsers.value.filter((u) => u !== user);
  } else {
    selectedUsers.value.push(user);
  }
};

/**
 * Adds the selected users to the selected event.
 */
const addUsersToEvent = async () => {
  const usersData = { users: selectedUsers.value };
  try {
    for (const user of selectedUsers.value) {
      const response = await api.post(
        `/users/${user.id}/eventRegistration?eventId=${eventIdForUserSelection.value}`,
        usersData,
      );
      if (response.status === 201) {
        showToast(
          new Toast("Erfolg", "Benutzer erfolgreich hinzugefügt", "success"),
        );
        closeModal();
      } else {
        showToast(
          new Toast("Fehler", "Fehler beim Hinzufügen der Benutzer", "error"),
        );
      }
    }
  } catch (error) {
    showToast(
      new Toast("Fehler", "Fehler beim Hinzufügen der Benutzer", "error"),
    );
  }
};

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString("de-DE", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

const filteredEvents = computed(() => {
  return events.value.filter(
    (event) =>
      event.inviteOnly &&
      ((event.name &&
        event.name.toLowerCase().includes(searchTerm.value.toLowerCase())) ||
        (event.description &&
          event.description
            .toLowerCase()
            .includes(searchTerm.value.toLowerCase()))),
  );
});

const showDetails = (eventId) => {
  router.push(`/events/${eventId}`);
};
</script>

<style scoped></style>
