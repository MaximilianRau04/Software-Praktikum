<template>
  <div class="event-container">
    <!-- Event Header -->
    <div class="event-header">
      <h1 class="event-title">
        {{ exchangeDay.name || "Exchange Day Name wird geladen..." }}
      </h1>
      <div class="event-meta">
        <span class="event-date"
          >{{ formatDate(exchangeDay.startDate) }} bis
          {{ formatDate(exchangeDay.endDate) }}</span
        >
      </div>
    </div>

    <nav class="tabs-container">
      <button
        class="tab-button"
        :class="{ 'active-tab': view === 'details' }"
        @click="showDetails"
      >
        Details
      </button>
      <button
        class="tab-button"
        :class="{ 'active-tab': view === 'events' }"
        @click="showEvents"
      >
        Events
      </button>
    </nav>

    <div class="main-content">
      <div class="content-wrapper" v-if="view === 'events'">
        <!-- left side: resources -->
        <div class="resources-sidebar">
          <h3>Verfügbare Ressourcen</h3>
          <div class="search-container">
            <input
              type="text"
              class="search-bar"
              placeholder="Ressourcen suchen..."
              v-model="resourceSearchQuery"
            />
          </div>
          <ul class="resources-list" v-if="filteredResources.length > 0">
            <li
              v-for="resource in filteredResources"
              :key="resource.id"
              class="resource-item"
              draggable="true"
              @dragstart="startDrag(resource)"
            >
              <div class="resource-info">
                <span>{{ resource.name }} ({{ resource.type }})</span>
                <span class="resource-capacity">
                  Verfügbar:{{ resource.capacity }}
                </span>
              </div>
              <div class="quantity-controls">
                <button
                  @click.stop="decreaseQuantity(resource)"
                  :disabled="(resourceQuantities[resource.id] || 1) <= 1"
                >
                  −
                </button>
                <input
                  type="number"
                  :value="resourceQuantities[resource.id] || 1"
                  @input="updateQuantity(resource, $event)"
                  min="1"
                  :max="resource.capacity"
                  class="quantity-input"
                />
                <button
                  @click.stop="increaseQuantity(resource)"
                  :disabled="
                    (resourceQuantities[resource.id] || 1) >= resource.capacity
                  "
                >
                  +
                </button>
              </div>
            </li>
          </ul>
          <p v-else>Keine Ressourcen verfügbar.</p>
        </div>

        <!-- right side: events -->
        <div class="events-container">
          <h2>Events an diesem Exchange Day</h2>
          <div
            v-for="event in filteredEvents"
            :key="event.id"
            class="event-item"
            @drop="dropResource(event.id)"
            @dragover.prevent
          >
            <div class="event-data">
              <h3>{{ event.name }}</h3>
              <p>{{ formatDate(event.date) }}</p>
              <p>{{ event.description }}</p>
            </div>

            <!-- assigned resources -->
            <div class="assigned-resources">
              <h4
                @click="toggleResourceDropdown(event.id)"
                class="dropdown-header"
              >
                Zugewiesene Ressourcen
                <span>{{ openDropdowns[event.id] ? "▲" : "▼" }}</span>
              </h4>
              <ul v-if="openDropdowns[event.id]" class="dropdown-content">
                <li v-if="!event.resources || event.resources.length === 0">
                  Keine Ressourcen zugewiesen.
                </li>
                <li v-for="resource in event.resources" :key="resource.id">
                  {{ resource.name }} ({{ resource.type }}), Menge:
                  {{ resource.quantity }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="content-container">
        <!-- details of the exchange day -->
        <div v-if="view === 'details'" class="details-card">
          <div class="details-grid">
            <div class="description-section">
              <h3 class="section-title">Beschreibung</h3>
              <p v-if="exchangeDay.description" class="description-text">
                {{ exchangeDay.description }}
              </p>
              <p v-else class="description-text">
                Keine Beschreibung verfügbar.
              </p>
            </div>
          </div>

          <div class="info-card">
            <i class="location-icon icon-location"></i>
            <div class="info-content">
              <span class="info-label">Ort:</span>
              <span class="info-value"
                >{{ exchangeDay.location.country }},
                {{ exchangeDay.location.postalCode }},
                {{ exchangeDay.location.city }},
                {{ exchangeDay.location.street }},
                {{ exchangeDay.location.houseNumber }}</span
              >
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import Cookies from "js-cookie";
import { showToast, Toast } from "@/types/toasts";
import { faXmark, faCheck } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";
import "@/assets/event-page.css";

// exchange day details
const exchangeDay = ref({
  name: "",
  description: "",
  startDate: "",
  endDate: "",
  location: {
    street: "",
    houseNumber: "",
    postalCode: "",
    city: "",
    country: "",
  },
});
const events = ref([]);
const availableResources = ref([]);
const draggedResource = ref(null);

const view = ref("details");
const userRole = Cookies.get("role");
const route = useRoute();
const router = useRouter();
const exchangeDayId = Number(route.params?.exchangeDayId);

const openDropdowns = ref<Record<number, boolean>>({});
const resourceSearchQuery = ref("");
const eventSearchQuery = ref("");
const resourceQuantities = ref<Record<number, number>>({});

const goToEvent = (eventId) => {
  router.push({ name: "EventPage", params: { eventId } });
};

/**
 * Filters the available resources based on the search query.
 */
const filteredResources = computed(() =>
  availableResources.value.filter((resource) => {
    const isValidType =
      resource.type.toLowerCase() === "material" ||
      resource.type.toLowerCase() === "equipment";
    const matchesSearch = resource.name
      .toLowerCase()
      .includes(resourceSearchQuery.value.toLowerCase());
    const sameLocation =
      exchangeDay.value.location &&
      resource.location &&
      resource.location.city === exchangeDay.value.location.city;

    return isValidType && matchesSearch && sameLocation;
  }),
);

/**
 * Filters the events based on the search query.
 */
const filteredEvents = computed(() =>
  events.value.filter((event) =>
    event.name.toLowerCase().includes(eventSearchQuery.value.toLowerCase()),
  ),
);

/**
 * Fetches the details of the selected Exchange Day from the API.
 */
const fetchExchangeDayDetails = async () => {
  const response = await api.get(`/exchange-days/${exchangeDayId}`);
  const data = response.data;

  if (response.status !== 200)
    [
      showToast(
        new Toast(
          "Fehler",
          `Exchange-Days konnten nicht geladen werden`,
          "error",
          faXmark,
          5,
        ),
      ),
    ];
  exchangeDay.value = data;

  const eventsResponse = await api.get(
    `/exchange-days/${exchangeDayId}/events`,
  );
  const eventsData = await eventsResponse.data;

  if (eventsResponse.status !== 200)
    [
      showToast(
        new Toast(
          "Fehler",
          `Exchange-Days konnten nicht geladen werden`,
          "error",
          faXmark,
          5,
        ),
      ),
    ];
  events.value = eventsData;
};

/**
 * Fetches all available resources from the API.
 */
const fetchResources = async () => {
  const response = await api.get(`/resources`);
  if (response.status !== 200)
    [
      showToast(
        new Toast(
          "Fehler",
          `Ressourcen konnten nicht geladen werden.`,
          "error",
          faXmark,
          5,
        ),
      ),
    ];

  const data = await response.data;

  availableResources.value = data;
  availableResources.value = data.map((resource) => ({
    ...resource,
    assignedQuantity: resource.assignedQuantity || 0,
  }));
};

/**
 * Updates the quantity of a resource.
 */
const updateQuantity = (resource, event) => {
  const newValue = parseInt(event.target.value);
  if (!isNaN(newValue) && newValue >= 1 && newValue <= resource.capacity) {
    resourceQuantities.value[resource.id] = newValue;
  }
};

/**
 * Increases the quantity of a resource.
 */
const increaseQuantity = (resource) => {
  if (!resourceQuantities.value[resource.id]) {
    resourceQuantities.value[resource.id] = 1;
  }

  if (resourceQuantities.value[resource.id] < resource.capacity) {
    resourceQuantities.value[resource.id]++;
  }
};

/**
 * Decreases the quantity of a resource.
 */
const decreaseQuantity = (resource) => {
  if (
    resourceQuantities.value[resource.id] &&
    resourceQuantities.value[resource.id] > 1
  ) {
    resourceQuantities.value[resource.id]--;
  }
};

/**
 * Starts dragging a resource.
 */
const startDrag = (resource) => {
  draggedResource.value = resource;
  if (!resourceQuantities.value[resource.id]) {
    resourceQuantities.value[resource.id] = 1;
  }
};

/**
 * fetches the resources for a specific event
 */
const fetchEventResources = async (eventId) => {
  try {
    const response = await api.get(`/events/${eventId}/resources`);
    const resourcesData = await response.data;

    if (response.status !== 200) {
      showToast(
        new Toast(
          "Fehler",
          `Ressourcen konnten nicht geladen werden`,
          "error",
          faXmark,
          5,
        ),
      );
    }

    const updatedEventIndex = events.value.findIndex(
      (event) => event.id === eventId,
    );
    if (updatedEventIndex !== -1) {
      events.value[updatedEventIndex].resources = resourcesData.map((res) => ({
        id: res.id,
        name: res.resource?.name || "Unbekannt",
        type: res.resource?.type || "Unbekannt",
        quantity: res.quantity || 0,
      }));
    }
  } catch (error) {
    showToast(
      new Toast("Error", `Fehler Fetchen der ressourcen`, "error", faXmark, 10),
    );
  }
};

/**
 * Drops a resource on an event and refreshes its assigned resources.
 */
const dropResource = async (eventId) => {
  if (!draggedResource.value) {
    showToast(
      new Toast(
        "Fehler",
        `Es wurde keine Ressource ausgewählt`,
        "error",
        faXmark,
        5,
      ),
    );
    return;
  }
  const quantity = resourceQuantities.value[draggedResource.value.id] || 1;
  if (quantity <= 0) {
    showToast(new Toast("Fehler", `Ungültige Anzahl`, "error", faXmark, 5));
    return;
  }
  if (quantity > draggedResource.value.capacity) {
    showToast(
      new Toast(
        "Fehler",
        `Maximale Kapazität überschritten: ${draggedResource.value.capacity}`,
        "error",
        faXmark,
        5,
      ),
    );
    return;
  }
  try {
    const response = await api.post(
      `/assignments`,
      {
        resourceId: draggedResource.value.id,
        eventId,
        quantity: quantity,
      },
      {
        headers: { "Content-Type": "application/json" },
      },
    );
    if (response.status !== 200) {
      showToast(
        new Toast(
          "Fehler",
          `Zuweisung konnte nicht getroffen werden`,
          "error",
          faXmark,
          5,
        ),
      );
      return;
    }

    showToast(
      new Toast(
        "Erfolg",
        `Sie haben ${quantity} mal ${draggedResource.value.name} zugewiesen`,
        "success",
        faCheck,
        5,
      ),
    );
    await fetchEventResources(eventId);

    await fetchEventResources(eventId);

    const resourceId = draggedResource.value.id;
    draggedResource.value = null;
    resourceQuantities.value[resourceId] = 1;

    await fetchResources();
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `Zuweisung konnte nicht durchgeführt werden`,
        "error",
        faXmark,
        5,
      ),
    );
  }
};

/**
 * Formats a timestamp to a human-readable date.
 */
function formatDate(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE");
}

const showDetails = () => {
  view.value = "details";
};

const showEvents = () => {
  view.value = "events";
};

const toggleResourceDropdown = async (eventId) => {
  if (!openDropdowns.value[eventId]) {
    await fetchEventResources(eventId);
  }
  openDropdowns.value[eventId] = !openDropdowns.value[eventId];
};

onMounted(async () => {
  await fetchExchangeDayDetails();
  await fetchResources();
});
</script>

<style scoped>
.content-wrapper {
  display: flex;
  flex-direction: row;
}

.resources-sidebar {
  width: 30%;
  padding: 1rem;
  background-color: #f5f5f5;
  border-right: 1px solid #ddd;
}

.events-container {
  width: 70%;
  padding: 1rem;
}

.search-container {
  margin-bottom: 1.5rem;
  position: relative;
}

.search-bar {
  width: 90%;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  border: 1px solid #2196f3;
  border-radius: 8px;
  background-color: #fff;
  transition:
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.search-bar:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 8px rgba(33, 150, 243, 0.4);
}

.resource-item {
  background: linear-gradient(90deg, #e3f2fd, #bbdefb);
  margin: 0.75rem 0;
  padding: 1rem;
  cursor: grab;
  border: 1px solid #2196f3;
  border-radius: 8px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    background-color 0.3s ease,
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.resource-item:hover {
  background-color: #bbdefb;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.resource-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.resource-capacity {
  font-size: 0.85rem;
  color: #555;
}

.event-item {
  background-color: #f3f2f2;
  border: 1px solid #2196f3;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  display: flex;
  justify-content: space-between;
  position: relative;
  height: auto;
}

.event-item:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.edit-event-button {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background-color: #000000;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  border-radius: 8px;
  cursor: pointer;
  width: auto;
  max-width: 100px;
}

.edit-event-button:hover {
  background-color: #373737;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.event-data {
  width: 60%;
}

.event-item h3 {
  color: black;
  font-weight: bold;
}

.event-item p {
  margin: 0.5rem 0;
}

.edit-event-button:hover {
  background-color: #373737;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.assigned-resources .dropdown-header {
  cursor: pointer;
  font-weight: bold;
  margin-top: 10px;
}

.assigned-resources .dropdown-content {
  margin-top: 10px;
  list-style: none;
  padding-left: 0;
}

.resources-list {
  display: flex;
  flex-direction: column;
  align-items: center; /* Zentriert die Items horizontal */
  gap: 0.75rem; /* Abstand zwischen den Items */
  padding: 0;
  list-style: none;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-controls button {
  width: 32px;
  height: 32px;
  background-color: #2196f3;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1.2rem;
  line-height: 1;
  transition: background-color 0.2s ease;
}

.quantity-controls button:hover:not(:disabled) {
  background-color: #1976d2;
}

.quantity-controls button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.quantity-input {
  width: 50px;
  text-align: center;
  padding: 0.4rem;
  border: 1px solid #2196f3;
  border-radius: 6px;
  font-size: 1rem;
}
</style>
