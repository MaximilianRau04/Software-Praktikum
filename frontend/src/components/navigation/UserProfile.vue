<template>
  <div class="profile-container" v-if="userData">
    <div class="profile-header">
      <h1>{{ userData.firstname }} {{ userData.lastname }}</h1>
      <div class="username-container">
        <p class="username">@{{ userData.username }}</p>
        <button v-if="!isEditing" @click="enableEdit" class="edit-btn">
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
          <p>
            <strong>Datum:</strong> {{ formatDate(event.date) }}
          </p>
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

<script >
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import router from "@/router";
import config from "@/config";

const apiUrl = "http://localhost:8080/api/";

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

    const sortedEvents = computed(() =>
      registeredEvents.value.sort(
        (a, b) => new Date(b.startTime) - new Date(a.startTime),
      ),
    );

    const sortedPosts = computed(() =>
      forumPosts.value.sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt),
      ),
    );

    /**
     * Renders a star rating based on a numerical rating.
     * @param {number} rating The numerical rating to render stars for.
     * @returns {string} The rendered star rating.
     */
    const renderStars = (rating) => {
      const maxStars = 5;
      const fullStar = "★";
      const halfStar = "½";
      const emptyStar = "☆";

      const fullStars = Math.floor(rating);
      const halfStars = Math.round(rating - fullStars) >= 0.5 ? 1 : 0;
      const emptyStars = maxStars - fullStars - halfStars;

      return (
        fullStar.repeat(fullStars) +
        halfStar.repeat(halfStars) +
        emptyStar.repeat(emptyStars)
      );
    };

    /**
     * Fetches the user data from the API.s
     */
    const fetchUserData = async () => {
      try {
        const userResponse = await axios.get(
          `${config.apiBaseUrl}/users/search?username=${props.username}`,
        );
        userData.value = userResponse.data;

        const userId = userResponse.data.id;
        const [eventsRes, profileRes, postsRes] = await Promise.all([
          axios.get(`${apiUrl}users/${userId}/registeredEvents`),
          axios.get(`${apiUrl}users/${userId}/trainerProfile`),
          axios.get(`${apiUrl}users/${userId}/forumPosts`),
        ]);

        registeredEvents.value = eventsRes.data;
        trainerProfile.value = profileRes.data;
        forumPosts.value = postsRes.data;

        fetchThreadTitles(postsRes.data);

        if (profileRes.data && profileRes.data.id) {
          await fetchPinnedComments(profileRes.data.id);
        }
      } catch (error) {
        console.error("Fehler beim Abrufen der Benutzerdaten:", error);
      }
    };

    /**
     * Fetches the titles of the threads for the given posts.
     * @param {Array} posts The forum posts to fetch thread titles for.
     */
    const fetchThreadTitles = async (posts) => {
      try {
        const threadRequests = posts.map((post) =>
          axios.get(`${apiUrl}forumthreads/${post.threadId}`),
        );
        const threadResponses = await Promise.all(threadRequests);

        threadResponses.forEach((response, index) => {
          const threadId = posts[index].threadId;
          threadTitles.value[threadId] = response.data.title;
        });
      } catch (error) {
        console.error("Failed to fetch thread titles:", error);
      }
    };

    /**
     * Fetches the pinned comments for the given trainer profile.
     * @param {number} trainerProfileId The ID of the trainer profile to fetch pinned comments for.
     */
    const fetchPinnedComments = async (trainerProfileId) => {
      if (!trainerProfileId) return;

      try {
        const response = await axios.get(
          `${apiUrl}trainerProfiles/${trainerProfileId}/pinned-comments`,
        );

        console.log("Antwort von der API:", response.data);

        if (response.data && Array.isArray(response.data)) {
          pinnedComments.value = response.data.map((comment) => ({
            category: comment.category,
            content: comment.content,
            feedbackId: comment.feedbackId,
          }));
        } else {
          console.warn(
            "Die API hat keine gültigen `pinnedComments` zurückgegeben:",
            response.data,
          );
        }
      } catch (error) {
        console.error("Fehler beim Abrufen der `pinnedComments`:", error);
      }
    };

    /**
     * Unpins a comment for the given category and feedback ID.
     * @param {string} category The category of the comment to unpin.
     * @param {number} feedbackId The ID of the feedback to unpin.
     */
    const unpinComment = async (category, feedbackId) => {
      try {
        if (!trainerProfile.value || !trainerProfile.value.id) {
          console.error("Trainerprofil ist nicht verfügbar oder hat keine ID.");
          return;
        }

        const trainerProfileId = trainerProfile.value.id;

        await axios.post(
          `${apiUrl}trainerProfiles/${trainerProfileId}/${feedbackId}/unpin?commentType=${category}`,
        );

        pinnedComments.value = pinnedComments.value.filter(
          (comment) => comment.feedbackId !== feedbackId,
        );
      } catch (error) {
        console.error("Fehler beim Entpinnen des Kommentars:", error);
      }
    };

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
          await axios.put(`${apiUrl}users/${userId}`, updatedData);

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

    const formatDate = (timestamp) => {
      const date = new Date(timestamp);
      return date.toLocaleDateString('de-DE');
    };

    onMounted(fetchUserData);

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
    };
  },
};
</script>

<style scoped>
.profile-container {
  padding: 30px;
  max-width: 1000%;
  margin: 0 auto;
  background-color: #f8f9fa;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  height: 90%;
  overflow-y: auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
  border-bottom: 2px solid #dee2e6;
  padding-bottom: 20px;
}

.profile-header h1 {
  font-size: 2.4rem;
  color: #2c3e50;
  margin: 1%;
}

.profile-header .username {
  font-size: 1.2rem;
  color: #6c757d;
  display: inline-block;
}

.profile-header .role {
  color: #01172f;
  font-weight: bold;
  font-size: 1.1rem;
  margin-top: 1%;
}

.username-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-btn {
  background-color: #009ee2;
  color: white;
  padding: 1% 1%;
  font-size: 0.8rem;
  margin-left: 10px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.edit-btn i {
  font-size: 1.2rem;
}

.profile-section {
  margin-top: 1%;
}

.profile-section h2 {
  font-size: 1.8rem;
  color: #495057;
  margin: 1%;
  border-bottom: 2px solid #dee2e6;
  padding-bottom: 5px;
}

.scrollable-section {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
}

.event-card,
.forum-post,
.pinned-comment {
  background-color: #ffffff;
  margin: 15px 0;
  padding: 20px;
  border-left: 5px solid #009ee2;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.event-card:hover,
.forum-post:hover,
.pinned-comment:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.event-card h3 {
  margin: 0;
  font-size: 1.5rem;
  color: #343a40;
}

.event-card p,
.forum-post p,
.pinned-comment p {
  font-size: 1rem;
  color: #6c757d;
  margin: 5px 0;
}

.expertise-tag {
  display: inline-block;
  background-color: #ffffff;
  color: #007bff;
  border-radius: 5px;
  padding: 5px 10px;
  margin: 5px;
  font-size: 0.9rem;
  font-weight: bold;
}

.unpin-btn {
  background-color: #e74c3c;
  color: white;
  padding: 0.5% 1%;
  font-size: 0.8rem;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  margin-left: 10px;
}

.unpin-btn:hover {
  background-color: #c0392b;
}

.trainer-profile {
  font-family: Arial, sans-serif;
  line-height: 1.5;
  background-color: #f9f9f9;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  max-width: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.trainer-profile p {
  margin: 10px 0;
}

.rating {
  color: #ffd700;
  font-size: 18px;
}

.expertise {
  font-style: italic;
  color: #555;
}
</style>
