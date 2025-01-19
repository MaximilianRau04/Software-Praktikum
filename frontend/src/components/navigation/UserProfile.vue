<template>
  <div class="profile-container" v-if="userData">
    <div class="profile-header">
      <h1>{{ userData.firstname }} {{ userData.lastname }}</h1>
      <div class="username-container">
        <p class="username">@{{ userData.username }}</p>
        <button
          v-if="isCurrentUser && !isEditing"
          @click="enableEdit"
          class="edit-btn"
        >
          <i class="fas fa-edit"></i> Bearbeiten
        </button>
        <div v-if="isEditing" class="edit-container">
          <input
            v-model="newUsername"
            @blur="updateUsername"
            @keyup.enter="updateUsername"
            type="text"
            class="edit-input"
            placeholder="Neuen Username eingeben"
          />
          <button @click="updateUsername" class="save-btn">Speichern</button>
        </div>
      </div>
      <p class="role">{{ userData.role }}</p>
    </div>

    <div class="profile-section" v-if="trainerProfile">
      <h2>Trainer Profil</h2>
      <p>
        <strong>Bio:</strong> {{ trainerProfile.bio || "Keine Bio verfügbar." }}
      </p>
      <p>
        <strong>Durchschnittliche Bewertung:</strong>
        <span
          class="rating"
          v-html="renderStars(trainerProfile.averageRating || 0)"
        ></span>
      </p>
      <div v-if="trainerProfile.expertiseTags.length">
        <strong>Expertise:</strong>
        <ul>
          <li v-for="tag in trainerProfile.expertiseTags" :key="tag">
            {{ tag }}
          </li>
        </ul>
      </div>
      <p v-else><strong>Expertise:</strong> Keine Tags verfügbar.</p>
    </div>

    <div class="profile-section" v-if="pinnedComments.length > 0">
      <h2>Gepinnte Kommentare</h2>
      <ul class="scrollable-section">
        <li
          v-for="comment in pinnedComments"
          :key="comment.category"
          class="pinned-comment"
        >
          <p>
            <strong>{{ comment.category }}:</strong>
            {{ comment.content || "Kein Kommentar verfügbar." }}
          </p>
          <button
            @click="unpinComment(comment.category, comment.feedbackId)"
            class="unpin-btn"
          >
            Entpinnen
          </button>
        </li>
      </ul>
    </div>

    <div class="profile-section" v-if="registeredEvents.length">
      <h2>Angemeldete Veranstaltungen</h2>
      <ul class="scrollable-section">
        <li v-for="event in sortedEvents" :key="event.id" class="event-card">
          <h3>{{ event.name }}</h3>
          <p><strong>Datum:</strong> {{ formatDate(event.date) }}</p>
          <p>
            <strong>Zeit:</strong> {{ event.startTime }} Uhr -
            {{ event.endTime }} Uhr
          </p>
          <p><strong>Raum:</strong> {{ event.room }}</p>
          <p><strong>Beschreibung:</strong> {{ event.description }}</p>
        </li>
      </ul>
    </div>

    <div class="profile-section" v-if="forumPosts.length">
      <h2>Forum Beiträge</h2>
      <ul class="scrollable-section">
        <li v-for="post in sortedPosts" :key="post.id" class="forum-post">
          <p>
            <strong>Thread:</strong>
            {{ threadTitles[post.threadId] || "Thread wird geladen..." }}
          </p>
          <p><strong>Datum:</strong> {{ formatDateTime(post.createdAt) }}</p>
          <p><strong>Inhalt:</strong> {{ post.content }}</p>
        </li>
      </ul>
    </div>
  </div>

  <div v-else>
    <p>Profil wird geladen...</p>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from "vue";
import axios from "axios";
import router from "@/router";
import config from "@/config";
import Cookies from "js-cookie";
import "@/assets/user-profile.css";

export default {
  props: {
    username: String,
  },
  setup(props) {
    const userData = ref(null);
    const registeredEvents = ref([]);
    const trainerProfile = ref(null);
    const forumPosts = ref([]);
    const threadTitles = ref({});
    const pinnedComments = ref([]);

    const isEditing = ref(false);
    const newUsername = ref("");

    /**
     * Sorts the registered events by their start time.
     */
    const sortedEvents = computed(() =>
      registeredEvents.value.sort(
        (a, b) => new Date(b.startTime) - new Date(a.startTime),
      ),
    );

    const loggedInUserId = parseInt(Cookies.get("userId"), 10);

    const isCurrentUser = computed(() => {
      return userData.value && userData.value.id === loggedInUserId;
    });

    /**
     * Sorts the forum posts by their creation date.
     */
    const sortedPosts = computed(() =>
      forumPosts.value.sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt),
      ),
    );

    /**
     * Renders a star rating based on the given rating.
     * @param {number} rating - The rating to render stars for.
     * @returns {string} The rendered stars.
     */
    const renderStars = (rating) => {
      const maxStars = 5;
      const fullStar = "★";
      const emptyStar = "☆";

      const fullStars = Math.floor(rating);
      const emptyStars = maxStars - fullStars;

      return fullStar.repeat(fullStars) + emptyStar.repeat(emptyStars);
    };

    /**
     * Fetches the user data, registered events, forum posts, and trainer profile.
     */
    const fetchUserData = async () => {
      try {
        const userResponse = await axios.get(
          `${config.apiBaseUrl}/users/search?username=${props.username}`,
        );
        userData.value = userResponse.data;

        const userId = userResponse.data.id;
        const [eventsRes, postsRes] = await Promise.all([
          axios.get(`${config.apiBaseUrl}/users/${userId}/registeredEvents`),
          axios.get(`${config.apiBaseUrl}/users/${userId}/forumPosts`),
        ]);

        if (userResponse.data.role === "ADMIN") {
          const profileRes = await axios.get(
            `${config.apiBaseUrl}/users/${userId}/trainerProfile`,
          );
          trainerProfile.value = profileRes.data;
          if (profileRes.data && profileRes.data.id) {
            await fetchPinnedComments(profileRes.data.id);
          }
        }

        registeredEvents.value = eventsRes.data;
        forumPosts.value = postsRes.data;

        fetchThreadTitles(postsRes.data);
      } catch (error) {
        console.error("Fehler beim Abrufen der Benutzerdaten:", error);
      }
    };

    /**
     * Fetches the titles of the threads the given posts belong to.
     * @param {Array} posts - The posts to fetch the thread titles for.
     */
    const fetchThreadTitles = async (posts) => {
      try {
        const threadRequests = posts.map((post) =>
          axios.get(`${config.apiBaseUrl}/forumthreads/${post.threadId}`),
        );
        const threadResponses = await Promise.all(threadRequests);

        threadResponses.forEach((response, index) => {
          const threadId = posts[index].threadId;
          threadTitles.value[threadId] = response.data.title;
        });
      } catch (error) {
        console.error("Fehler beim Abrufen der Thread-Titel:", error);
      }
    };

    /**
     * Fetches the pinned comments of the given trainer profile.
     * @param {number} trainerProfileId - The ID of the trainer profile to fetch the pinned comments for.
     */
    const fetchPinnedComments = async (trainerProfileId) => {
      if (!trainerProfileId) return;

      try {
        const response = await axios.get(
          `${config.apiBaseUrl}/trainerProfiles/${trainerProfileId}/pinned-comments`,
        );
        pinnedComments.value = response.data.map((comment) => ({
          category: comment.category,
          content: comment.content,
          feedbackId: comment.feedbackId,
        }));
      } catch (error) {
        console.error("Fehler beim Abrufen der gepinnten Kommentare:", error);
      }
    };

    /**
     * Unpins the comment with the given feedback ID from the given category.
     * @param {string} category - The category of the comment to unpin.
     * @param {number} feedbackId - The ID of the feedback to unpin.
     */
    const unpinComment = async (category, feedbackId) => {
      try {
        const trainerProfileId = trainerProfile.value.id;
        await axios.post(
          `${config.apiBaseUrl}/trainerProfiles/${trainerProfileId}/${feedbackId}/unpin?commentType=${category}`,
        );

        pinnedComments.value = pinnedComments.value.filter(
          (comment) => comment.feedbackId !== feedbackId,
        );
      } catch (error) {
        console.error("Fehler beim Entpinnen des Kommentars:", error);
      }
    };

    /**
     * Enables the editing mode for the username.
     */
    const enableEdit = () => {
      isEditing.value = true;
      newUsername.value = userData.value.username;
    };

    /**
     * Updates the username of the user.
     */
    const updateUsername = async () => {
      if (newUsername.value !== userData.value.username) {
        try {
          const updatedData = {
            username: newUsername.value,
            firstname: userData.value.firstname,
            lastname: userData.value.lastname,
            role: userData.value.role,
          };

          const userId = userData.value.id;
          await axios.put(`${config.apiBaseUrl}/users/${userId}`, updatedData);

          userData.value.username = newUsername.value;
          isEditing.value = false;

          router.push(`/user/${newUsername.value}`);
        } catch (error) {
          console.error("Fehler beim Aktualisieren des Benutzernamens:", error);
        }
      } else {
        isEditing.value = false;
      }
    };

    /**
     * Formats the given timestamp to a date string.
     * @param {number} timestamp - The timestamp to format.
     * @returns {string} The formatted date string.
     */
    const formatDate = (timestamp) => {
      const date = new Date(timestamp);
      return date.toLocaleDateString("de-DE");
    };

    onMounted(fetchUserData);

    watch(
      () => props.username,
      (newUsername) => {
        userData.value = null;
        fetchUserData();
      },
    );

    return {
      userData,
      registeredEvents,
      trainerProfile,
      forumPosts,
      threadTitles,
      pinnedComments,
      sortedEvents,
      sortedPosts,
      isEditing,
      newUsername,
      enableEdit,
      updateUsername,
      unpinComment,
      renderStars,
      formatDate,
      isCurrentUser,
      fetchUserData,
    };
  },
};
</script>
