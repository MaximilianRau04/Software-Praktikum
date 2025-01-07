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
      <p>{{ trainerProfile.bio }}</p>
    </div>

    <div class="profile-section" v-if="registeredEvents.length">
      <h2>Angemeldete Veranstaltungen</h2>
      <ul class="scrollable-section">
        <li v-for="event in sortedEvents" :key="event.id" class="event-card">
          <h3>{{ event.name }}</h3>
          <p>
            <strong>Datum:</strong> {{ formatDateTime(event.startTime) }} -
            {{ formatDateTime(event.endTime) }}
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
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import router from "@/router";

const apiUrl = "http://193.196.54.172:8000/api/";

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

    const fetchUserData = async () => {
      try {
        const userResponse = await axios.get(
          `${apiUrl}users/search?username=${props.username}`,
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
      } catch (error) {
        console.error("Failed to fetch user data:", error);
      }
    };

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

    const formatDateTime = (dateString) => {
      const options = {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      };
      return new Date(dateString).toLocaleDateString("de-DE", options);
    };

    const enableEdit = () => {
      isEditing.value = true;
      newUsername.value = userData.value.username;
    };

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

    onMounted(fetchUserData);

    return {
      userData,
      registeredEvents,
      trainerProfile,
      forumPosts,
      threadTitles,
      formatDateTime,
      sortedEvents,
      sortedPosts,
      isEditing,
      newUsername,
      enableEdit,
      updateUsername,
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
.forum-post {
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
.forum-post:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.event-card h3 {
  margin: 0;
  font-size: 1.5rem;
  color: #343a40;
}

.event-card p,
.forum-post p {
  font-size: 1rem;
  color: #6c757d;
  margin: 5px 0;
}

.edit-container {
  display: flex;
  align-items: center;
}

.edit-input {
  padding: 10px;
  font-size: 1rem;
  border: 1px solid #ced4da;
  border-radius: 5px;
  margin-right: 10px;
  width: 200px;
}

.save-btn {
  padding: 10px 15px;
  font-size: 1rem;
  background-color: #009ee2;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
