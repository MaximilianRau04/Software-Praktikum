<template>
  <div class="event-container">
    <!-- Back Button -->
    <button class="back-button" @click="goToEventRegistrations">
      ← Zurück zu Events
    </button>

    <!-- Event Header -->
    <div class="event-header">
      <h1 class="event-title">
        {{ event.name || "Eventname wird geladen..." }}
      </h1>
      <div class="event-meta">
        <span class="event-date"
          >{{ event.exchangeDayName }} - Exchange Day</span
        >
        <span class="event-date">{{ formatDate(event.date) }}</span>
        <span class="event-date"
          >{{ event.startTime.slice(0, 5) }} - {{ event.endTime.slice(0, 5) }}</span
        >
      </div>
    </div>

    <!-- Navigation Tabs -->
    <nav class="tabs-container">
      <button
        class="tab-button"
        :class="{ 'active-tab': view === 'details' }"
        @click="showDetails"
      >
        Details
      </button>
      <button
        v-if="isAlreadyRegistered || isAdmin"
        class="tab-button"
        :class="{ 'active-tab': view === 'forum' }"
        @click="showForum"
      >
        Forum
      </button>
      <button
        class="tab-button"
        :class="{ 'active-tab': view === 'users' }"
        @click="showUsers"
      >
        Teilnehmer
      </button>
      <button
        v-if="isAdmin"
        class="tab-button"
        :class="{ 'active-tab': view === 'feedback' }"
        @click="openFeedback"
      >
        Feedback
      </button>
      <button
        v-if="isAdmin"
        class="tab-button"
        :class="{ 'active-tab': view === 'qr' }"
        @click="openQRCode"
      >
        QR-Code
      </button>
      <button
        v-if="isAlreadyRegistered"
        class="tab-button danger"
        @click="unregisterFromEvent"
      >
        Abmelden
      </button>
      <button v-else class="tab-button primary" @click="registerForEvent">
        Registrieren
      </button>
    </nav>

    <!-- Main Content -->
    <div class="content-container">
      <!-- Details Section -->
      <div v-if="view === 'details'" class="details-card">
        <div class="organizer-section">
          <h3 class="section-title">Veranstalter</h3>
          <div class="organizer-card" @click="goToUser(organizer.username)">
            <div
              class="organizer-avatar"
              :style="{ backgroundColor: generateColor(organizer.username) }"
            >
              {{ getInitials(organizer.firstname, organizer.lastname) }}
            </div>
            <div class="organizer-info">
              <span class="organizer-name"
                >{{ organizer.firstname }} {{ organizer.lastname }}</span
              >
              <span class="organizer-username">@{{ organizer.username }}</span>
            </div>
          </div>
        </div>

        <div class="details-grid">
            <div class="description-section">
            <h3 class="section-title">Beschreibung</h3>
            <p v-if="event.description" class="description-text">{{ event.description }}</p>
            <p v-else class="description-text">Keine Beschreibung verfügbar.</p>
            </div>

            <div class="tags-section">
            <h3 class="section-title">Tags</h3>
            <div class="tags-container">
              <span
              v-if="eventTags.length > 0"
              v-for="tag in eventTags.slice(0, 5)"
              :key="tag.id"
              class="tag-chip"
              >
              {{ tag.name }}
              </span>
              <p v-else class="no-tags-text">Keine Tags verfügbar.</p>
            </div>
            </div>
        </div>

        <div class="info-card">
          <i class="location-icon icon-location"></i>
          <div class="info-content">
            <span class="info-label">Ort:</span>
            <span class="info-value">{{ event.room.location.street }} {{ event.room.location.houseNumber }}</span>
            <span class="info-value">{{ event.room.location.postalCode }} {{ event.room.location.city }}, {{ event.room.location.country }}</span>
            <span class="info-value">{{ event.room.name }}</span>
          </div>
        </div>
      </div>

      <!-- Forum Section -->
      <div v-if="view === 'forum'">
        <Forum
          :threads="event.forumThreads"
          :focused-thread-id.sync="focusedThreadId"
          class="forum-container"
        />
      </div>

      <!-- Users Sections -->
      <div v-if="view === 'users'" class="event-user-list">
        <div class="event-user-items">
          <div
            v-for="user in sortedUsers"
            :key="user.username"
            class="event-user-card"
            @click="goToUser(user.username)"
          >
            <div
              class="event-user-avatar"
              :style="{ backgroundColor: generateColor(user.username) }"
            >
              {{ getInitials(user.firstname, user.lastname) }}
            </div>
            <div class="event-user-info">
              <p class="event-user-name">
                {{ user.firstname }} {{ user.lastname }}
              </p>
              <p class="event-user-username">@{{ user.username }}</p>
              <p class="event-user-role">
                {{ user.role === "ADMIN" ? "Administrator" : "Benutzer" }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- QR Code Modal -->
      <div v-if="showQRCodeModal" class="modal-overlay">
        <div class="modal-content">
          <button class="close-button" @click="closeQRCodeModal">×</button>
          <h3>QR-Code für die Anwesenheit</h3>
          <img :src="qrCodeUrl" alt="QR Code" class="qr-image" />
          <div class="modal-actions">
            <button
              class="action-button"
              @click="copyToClipboard"
              :disabled="!qrCodeLink"
            >
              Link kopieren
            </button>
            <a :href="qrCodeUrl" :download="`event-${eventId}-qr-code.png`">
              <button class="action-button">Download</button>
            </a>
          </div>
          <p v-if="copySuccess" class="success-message">Link kopiert!</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import config from "@/config";
import { Event } from "@/types/Event";
import Forum from "@/components/forum/Forum.vue";
import "@/assets/event-page.css";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";
import { useAuth } from "@/util/auth";

const event = ref<Event>({
  id: 0,
  name: "",
  startTime: "",
  endTime: "",
  exchangeDayName: "",
  room: {
    name: "",
    id: 0,
    type: "",
    location: {
      postalCode: 0,
      id: 0,
      street: "",
      houseNumber: 0,
      city: "",
      country: "",
    },
    capacity: 0,
    description: "",
    availability: 0,
  },
  description: "",
  date: "",
  forumThreads: [],
  inviteOnly: false,
});

const organizer = ref({ username: "", id: 0, firstname: "", lastname: "" });
const registeredUsers = ref([]);
const sortedUsers = ref([]);
const view = ref("details");
const route = useRoute();
const router = useRouter();
const eventId = Number(route.params?.eventId);
const showQRCodeModal = ref(false);
const eventTags = ref([]);
const qrCodeUrl = ref("");
const qrCodeLink = ref("");
const copySuccess = ref(false);

const focusedThreadId = ref<number | null>(null);
const isAlreadyRegistered = ref(false);

const goToUser = (username: string) => {
  router.push({ name: "Profile", params: { username } });
};

const showForum = () => {
  view.value = "forum";
  focusedThreadId.value = null;
};

const isAdmin = computed(() => {
  const auth = useAuth();
  return auth.isAdmin;
});

const goToEventRegistrations = () => {
  router.push({ name: "eventRegistrations" });
};

/**
 * Fetch Event Details
 */
const fetchEventDetails = async () => {
  try {
    const response = await api.get(`/events/${eventId}`);
    const eventData = await response.data;
    event.value = eventData;

    const organizerResponse = await api.get(`/events/${eventId}/organizer`);
    const organizerData = await organizerResponse.data;
    organizer.value = organizerData;

    await fetchRegisteredUsers();
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `Exchange Days konnten nicht geladen werden: ${error.message}`,
        "error",
        faXmark,
        5
      )
    );
  }
};

/**
 * Fetch Registered Users
 */
const fetchRegisteredUsers = async () => {
  try {
    const response = await api.get(`/events/${eventId}/registeredUsers`);
    const userData = await response.data;
    registeredUsers.value = userData;

    sortedUsers.value = [...userData].sort((a, b) =>
      a.username.localeCompare(b.username)
    );
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `Registrierte Nutzer konnten nicht geladen werden.`,
        "error",
        faXmark,
        5
      )
    );
  }
};

/*
 * Fetches the tags for a given event
 */
const fetchTagsForEvent = async () => {
  try {
    const response = await api.get(`/events/${eventId}/tags`);
    const tagData = await response.data;
    eventTags.value = tagData;
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `Event-Tags für Event: ${eventId} konnten nicht geladen werden.`,
        "error",
        faXmark,
        5
      )
    );
    eventTags.value = [];
  }
};

/**
 * Formats a timestamp into a human-readable date string.
 *
 * @param {string} timestamp - The date in milliseconds.
 * @returns {string} - The formatted date string in 'DD.MM.YYYY' format.
 */
function formatDate(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE");
}

const getInitials = (firstname: string, lastname: string) => {
  return `${firstname.charAt(0).toUpperCase()}${lastname.charAt(0).toUpperCase()}`;
};

/**
 * Generates a color based on the username.
 * @param {string} username - The username.
 * @returns {string} The color.
 */
const generateColor = (username: string) => {
  const colors = ["#8B0000", "#00008B", "#006400", "#8B4500", "#4B0082"];
  const index = username.charCodeAt(0) % colors.length;
  return colors[index];
};

/**
 * Opens the QR code modal for a specific event.
 * @param {number} id - The ID of the event.
 */
const openQRCode = async () => {
  try {
    qrCodeUrl.value = `${config.apiBaseUrl}/events/${eventId}/qr-code`;

    const response = await api.get(`/events/${eventId}/attendance-token`);
    if (response.status !== 200) {
      showToast(
        new Toast(
          "Fehler",
          `Anwesenheitstoken für Event: ${eventId} konnte nicht geladen werden.`,
          "error",
          faXmark,
          5
        )
      );
    }

    const responseData = await response.data;
    const attendanceToken = responseData.attendanceToken;

    qrCodeLink.value = `http://193.196.54.172:8000/events/${eventId}/attendance?token=${attendanceToken}`;
    showQRCodeModal.value = true;
  } catch (error) {
    showToast(
      new Toast(
        "Error",
        `Fehler beim Öffnen des QR-Codes.`,
        "error",
        faXmark,
        10
      )
    );
  }
};

/**
 * Copies the QR code link to the clipboard.
 */
const copyToClipboard = async () => {
  if (qrCodeLink.value) {
    try {
      await navigator.clipboard.writeText(qrCodeLink.value);
      copySuccess.value = true;
    } catch (error) {
      const textArea = document.createElement("textarea");
      textArea.value = qrCodeLink.value;
      document.body.appendChild(textArea);
      textArea.select();
      document.execCommand("copy");
      document.body.removeChild(textArea);
      copySuccess.value = true;
    }

    setTimeout(() => {
      copySuccess.value = false;
    }, 2000);
  }
};

const closeQRCodeModal = () => {
  showQRCodeModal.value = false;
};

const openFeedback = () => {
  router.push({
    name: "feedbackSummary",
    params: { eventId: eventId.toString() },
  });
};

/**
 * Unregisters the user from the event.
 */
const unregisterFromEvent = async () => {
  try {
    const userId = useAuth().getUserId();
    const response = await api.delete(
      `/users/${userId}/eventRegistration?eventId=${eventId}`,
      {
        headers: { "Content-Type": "application/json" },
      }
    );
    if (response.status !== 204) {
      showToast(
        new Toast(
          "Fehler",
          `Abmeldung fehlgeschlagen. Versuchen Sie es erneut.`,
          "error",
          faXmark,
          5
        )
      );
      return;
    }
    showToast(
      new Toast(
        "Erfolg",
        `Sie wurden erfolgreich von ${event.value.name} abgemeldet!`,
        "success",
        faCheck,
        5
      )
    );
    router.push("/home");
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `Sie konnten nicht vom Event abgemeldet werden.`,
        "error",
        faXmark,
        5
      )
    );
  }
};

/**
 * Checks if the user is already registered for the event.
 */
const checkRegistrationStatus = async () => {
  const userId = useAuth().getUserId();
  if (!userId) {
    useAuth().clearToken();
    router.push("/login");
    return;
  }
  try {
    const response = await api.get(`/users/${userId}/registeredEvents`);
    if (response.status !== 200) throw new Error("Failed to fetch user data.");

    const registeredEvents = await response.data;

    isAlreadyRegistered.value = registeredEvents.some(
      (event: { id: number }) => event.id === eventId
    );
  } catch (error) {
    showToast(
      new Toast("Fehler", `Fehler bei der Registrierung`, "error", faXmark, 5)
    );
    isAlreadyRegistered.value = false;
  }
};

/**
 * Registers the user for the event.
 * @param eventId The ID of the event to register for.
 */
const registerForEvent = async () => {
  try {
    const userId = useAuth().getUserId();

    const auth = useAuth();

    if (!auth.isAuthenticated) {
      router.push({
        name: "login",
        query: { returnUrl: `/events/${eventId}` },
      });
      return;
    }

    const response = await api.post(
      `/users/${userId}/eventRegistration?eventId=${eventId}`,
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    if (response.status === 404) {
      showToast(
        new Toast(
          "Fehler",
          `Registrierung fehlgeschlagen. Bitte versuchen sie es erneut`,
          "error",
          faXmark,
          5
        )
      );
    } else if (response.status === 409) {
      showToast(
        new Toast(
          "Fehler",
          `Sie sind bereits für dieses Event registriert`,
          "error",
          faXmark,
          5
        )
      );
    } else if (response.status === 201) {
      showToast(
        new Toast(
          "Erfolg",
          `Sie wurden erfolgreich zu ${event.value.name} angemeldet!`,
          "success",
          faCheck,
          5
        )
      );
      isAlreadyRegistered.value = true;
    }
  } catch (error) {
    showToast(
      new Toast(
        "Fehler",
        `Sie konnten nicht zum Event registriert werden.`,
        "error",
        faXmark,
        5
      )
    );
  }
};

const showDetails = () => {
  view.value = "details";
};
const showUsers = () => {
  view.value = "users";
};

/**
 * Handles the thread navigation.
 */
const handleThreadNavigation = () => {
  const threadId = Number(route.query.threadId);
  if (threadId) {
    view.value = "forum";
    setFocusedThread(threadId);
  }
};

const setFocusedThread = (threadId) => {
  focusedThreadId.value = threadId;
};

onMounted(() => {
  checkRegistrationStatus();
  fetchEventDetails();
  fetchTagsForEvent();
  handleThreadNavigation();
});
</script>
