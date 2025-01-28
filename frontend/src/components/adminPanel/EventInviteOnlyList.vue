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
            <div class="tag-chips">
              <span
                v-for="(tag, index) in (event.tags || []).slice(0, 5)"
                :key="tag.id"
                class="chip"
              >
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
import config from "@/config";
import { useRouter } from "vue-router";
import { showToast, Toast } from "@/types/toasts";

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
    (user) => !registeredUsers.value.some(regUser => regUser.id === user.id)
  );
});

onMounted(async () => {
  await fetchEvents();
});

const fetchEvents = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/events`);
    if (!response.ok) {
      throw new Error("Fehler beim Laden der Events");
    }
    events.value = await response.json();

    for (const event of events.value) {
      event.tags = await fetchTagsForEvent(event.id);
    }
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Laden der Events", "error"));
  }
};

const fetchTagsForEvent = async (eventId) => {
  try {
    const res = await fetch(`${config.apiBaseUrl}/events/${eventId}/tags`);
    if (!res.ok) throw new Error("Failed to fetch tags");
    return await res.json();
  } catch (error) {
    showToast(
      new Toast(
        "Error",
        `Fehler beim Laden der Tags für Event ${eventId}`,
        "error"
      )
    );
    return [];
  }
};

const fetchUsers = async () => {
  try {
    const response = await fetch(`${config.apiBaseUrl}/users`);
    if (!response.ok) {
      throw new Error('Fehler beim Laden der Benutzer');
    }
    users.value = await response.json(); 
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Laden der Benutzer", "error"));
  }
};

const fetchUserRegistrations = async (eventId) => {
  try {
    const registrations = await Promise.all(
      users.value.map(async (user) => {
        const response = await fetch(
          `${config.apiBaseUrl}/users/${user.id}/registeredEvents`
        );
        if (!response.ok) {
          throw new Error(`Fehler bei der Überprüfung der Registrierung für ${user.username}`);
        }
        const userEvents = await response.json();
        return userEvents.some((event) => event.id === eventId);
      })
    );

    registeredUsers.value = users.value.filter((user, index) => registrations[index]);
  } catch (error) {
    showToast(new Toast("Error", "Fehler beim Überprüfen der Registrierungen", "error"));
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

const addUsersToEvent = async () => {
  try {
    for(const user of selectedUsers.value) {
      const response = await fetch(
        `${config.apiBaseUrl}/users/${user.id}/eventRegistration?eventId=${eventIdForUserSelection.value}`,
        {
          method: "POST",
          body: JSON.stringify({ users: selectedUsers.value }),
          headers: { "Content-Type": "application/json" },
        }
      );
      if (response.ok) {
        showToast(
          new Toast("Success", "Benutzer erfolgreich hinzugefügt", "success")
        );
        closeModal();
      } else {
        showToast(
          new Toast("Error", "Fehler beim Hinzufügen der Benutzer", "error")
        );
      }
    }
  } catch (error) {
    showToast(
      new Toast("Error", "Fehler beim Hinzufügen der Benutzer", "error")
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
            .includes(searchTerm.value.toLowerCase())))
  );
});

const showDetails = (eventId) => {
  router.push(`/events/${eventId}`);
};
</script>


<style scoped>
.event-page {
  padding: 20px;
  background: #f9f9f9;
  max-height: 90%;
}

.search-bar-container {
  margin-bottom: 20px;
  text-align: center;
}

.search-bar {
  width: 80%;
  max-width: 600px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.search-bar:focus {
  border-color: #007bff;
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.2);
  outline: none;
}

.event-page {
  padding: 20px;
  background: #f9f9f9;
  max-height: 90%;
}

.search-bar-container {
  margin-bottom: 20px;
  text-align: center;
}

.search-bar {
  width: 80%;
  max-width: 600px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.search-bar:focus {
  border-color: #007bff;
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.2);
  outline: none;
}

.event-columns {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  overflow: auto;
}

.event-column {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: auto;
}

.event-details {
  background-color: #EAEAEA;
  border: 1px solid #01172F;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.8rem;
  position: relative;
  overflow: auto;
  color: #333;
}

h2 h3 p {
  margin: 0.5rem;
}

.register-button {
  background-color: #009EE2;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  font-size: 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease;
  position: absolute;
  bottom: 0.5rem;
  right: 0.5rem;
}

.register-button:hover {
  background-color: #0056b3;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.register-button:disabled {
  background-color: #ccc;
}

.empty-state {
  text-align: center;
  font-size: 1.2rem;
  color: #777;
}

.tag-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-top: 5px;
}

.chip {
  background-color: #d9f2ff;
  padding: 6px 12px;
  border-radius: 50px;
  font-size: 0.9rem;
  color: #007bff;
  font-weight: bold;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease;
}

.chip:hover {
  background-color: #007bff;
  color: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content h3 {
  margin-bottom: 10px;
}

.modal-content ul {
  list-style-type: none;
  padding: 0;
}

.modal-content li {
  padding: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.modal-content li.selected {
  background-color: #f0f0f0;
  border: 2px solid #009EE2;
}

.modal-content li:hover {
  background-color: #d3d3d3;
}

.confirm-button, .cancel-button {
  padding: 10px 20px;
  margin-top: 10px;
  cursor: pointer;
  background-color: #009EE2;
  color: white;
  border: none;
  border-radius: 8px;
}

.add-users-button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #009EE2;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 8px;
}

.add-users-button:hover {
  background-color: #007bb5;
}

.modal-content {
  position: relative;
  background: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 400px;
  width: 100%;
}

.close-modal-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: black;
}

.close-modal-icon:hover {
  color: #ff0000;
}

</style>
