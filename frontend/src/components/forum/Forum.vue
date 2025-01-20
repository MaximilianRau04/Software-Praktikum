<template>
  <div class="forum-container">
    <!-- Threads -->
    <div v-if="!selectedThreadId" class="threads-view">
      <h1>Diskussionsforum</h1>

      <button class="btn-primary create-thread-btn" @click="showModal = true">
        + Neuer Thread
      </button>

      <!-- list of threads -->
      <div class="thread-list">
        <div
          v-for="thread in threads"
          :key="thread.threadId"
          class="thread-item"
          @click="selectThread(thread.threadId)"
        >
          <h3>{{ thread.title }}</h3>
          <p class="thread-description">
            {{ truncate(thread.description, 100) }}
          </p>
          <div class="thread-meta">
            <span>{{ thread.forumPosts?.length || 0 }} Antworten</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="thread-detail">
      <button class="btn-secondary back-btn" @click="selectedThreadId = null">
        ← Zurück
      </button>

      <div v-if="selectedThread" class="thread-content">
        <h2>{{ selectedThread.title }}</h2>
        <p class="thread-description">{{ selectedThread.description }}</p>

        <!-- display all posts -->
        <div class="posts-section">
          <div v-if="selectedThread.forumPosts?.length" class="post-list">
            <div
              v-for="post in selectedThread.forumPosts"
              :key="post.id"
              class="post-item"
            >
              <div class="post-header">
                <span class="author">{{
                  post.anonymous ? "anonym" : post.author.username
                }}</span>
                <span class="date">{{ formatDate(post.createdAt) }}</span>
              </div>
              <div class="post-body">
                <p class="post-content">{{ post.content }}</p>
              </div>
            </div>
          </div>

          <p v-else class="no-posts">Noch keine Antworten.</p>
        </div>

        <!-- form for answers -->
        <form @submit.prevent="createPost" class="post-form">
          <textarea
            v-model="newPost.content"
            placeholder="Schreiben Sie Ihre Antwort..."
            rows="4"
            required
            class="input-field no-resize"
          ></textarea>

          <div class="checkbox-container">
            <label for="isAnonymous">Anonym posten</label>
            <input
              type="checkbox"
              id="isAnonymous"
              v-model="newPost.isAnonymous"
            />
          </div>

          <button type="submit" class="btn-primary">Antwort posten</button>
        </form>
      </div>
    </div>

    <!-- form for create Thread -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <h2>Neuen Thread erstellen</h2>
        <form @submit.prevent="createThread" class="thread-form">
          <div class="form-group">
            <label for="title">Titel:</label>
            <input
              id="title"
              v-model="newThread.title"
              placeholder="Titel des Threads"
              required
              class="input-field"
            />
          </div>
          <div class="form-group">
            <label for="description">Beschreibung:</label>
            <textarea
              id="description"
              v-model="newThread.description"
              placeholder="Beschreibung hinzufügen"
              rows="3"
              required
              class="input-field no-resize"
            ></textarea>
          </div>
          <div class="modal-actions">
            <button type="submit" class="btn-primary">Thread erstellen</button>
            <button
              type="button"
              class="btn-secondary cancel-btn"
              @click="showModal = false"
            >
              Abbrechen
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Cookies from "js-cookie";
import config from "@/config";

export default {
  data() {
    return {
      threads: [],
      selectedThreadId: null,
      selectedThread: null,
      newThread: {
        title: "",
        description: "",
      },
      newPost: {
        content: "",
        isAnonymous: false,
      },
      showModal: false,
    };
  },
  methods: {
    /**
     * Fetches all threads from the backend
     */
    async fetchThreads() {
      const eventId = this.$route.params.eventId;
      try {
        const response = await axios.get(
          `${config.apiBaseUrl}/events/${eventId}/forum`,
        );
        this.threads = response.data;
        console.log(this.threads.threadId)
      } catch (error) {
        console.error("Fehler beim Abrufen der Threads:", error);
      }
    },
    /**
     * Creates a new thread
     */
    async createThread() {
      const eventId = this.$route.params.eventId;
      console.log(eventId)
      try {
        const newThreadData = {
          title: this.newThread.title,
          description: this.newThread.description,
          eventId: eventId,
        };

        await axios.post(`${config.apiBaseUrl}/forumthreads`, newThreadData);
        this.fetchThreads();
        this.newThread.title = "";
        this.newThread.description = "";
        this.showModal = false;
      } catch (error) {
        console.error("Fehler beim Erstellen des Threads:", error);
      }
    },

    /**
     * Selects a thread and fetches its details
     * @param {number} threadId - The id of the thread
     */
    async selectThread(threadId) {
      console.log(threadId)
      this.selectedThreadId = threadId;
      this.fetchThreadDetail();
    },

    /**
     * Fetches the details of a thread
     */
    async fetchThreadDetail() {
      try {
        const response = await axios.get(
          `${config.apiBaseUrl}/forumthreads/${this.selectedThreadId}`,
        );
        this.selectedThread = response.data;
      } catch (error) {
        console.error("Fehler beim Abrufen der Thread-Details:", error);
      }
    },

    /**
     * Creates a new post
     */
    async createPost() {
      try {
        const userId = Cookies.get("userId");

        const newPostData = {
          content: this.newPost.content,
          forumThreadId: this.selectedThreadId,
          authorId: userId,
          anonymous: this.newPost.isAnonymous,
        };

        await axios.post(`${config.apiBaseUrl}/forumposts`, newPostData);

        this.fetchThreadDetail();
        this.newPost.content = "";
        this.newPost.isAnonymous = false;
      } catch (error) {
        console.error("Fehler beim Erstellen des Posts:", error);
      }
    },

    /**
     * Formats a date to a human-readable format
     * @param {string} date - The date to format
     * @returns {string} The formatted date
     */
    formatDate(date) {
      return new Date(date).toLocaleDateString("de-DE", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    },
    truncate(text, length) {
      return text.length > length ? text.substring(0, length) + "..." : text;
    },
  },

  mounted() {
    this.fetchThreads();
  },
};
</script>

<style scoped>
.forum-container {
  max-height: 90%;
  margin: 0 auto;
  padding: 0.5rem;
  padding-left: 2rem;
  overflow-y: auto;
}

.thread-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.thread-item .thread-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.9em;
  color: #000000;
}

.thread-item .thread-description h3 {
  margin: 0.5rem;
}

p.thread-description {
  padding-bottom: 2%;
  border-bottom: 2px solid #ddd;
}

h3 {
  margin: 0.5rem;
}

.thread-item {
  padding: 0.2rem;
  background-color: #f9f9f9;
  border: 1px solid #000000;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
  margin: 0.3rem;
}

.thread-item:hover {
  transform: translateY(-1px);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn {
  background-color: #0288d1;
  color: #fff;
  border: 1px solid #0288d1;
  padding: 12px 18px;
  border-radius: 6px;
  cursor: pointer;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
}

.cancel-btn:hover {
  background-color: #006bb3;
  color: #fff;
}

.post-item {
  padding: 0.2rem;
  margin-bottom: 0.5rem;
  background-color: #f9f9f9;
  border: 1px solid #000000;
  border-radius: 8px;
  max-height: 300px;
  overflow-y: auto;
}

.post-header {
  font-size: 0.9em;
  color: #6c757d;
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
}

.post-content {
  font-size: 1em;
  color: #333;
}

.btn-primary,
.btn-secondary {
  background-color: #0288d1;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 1em;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    transform 0.3s ease;
  margin-top: 10px;
  width: auto;
  display: block;
}

.btn-primary:hover,
.btn-secondary:hover {
  background-color: #6095b1;
  transform: translateY(-2px);
}

.post-form {
  margin-top: 20px;
}

.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #e1e1e1;
  border-radius: 8px;
  margin-bottom: 10px;
  box-sizing: border-box;
  font-size: 1em;
}

.input-field:focus {
  border-color: #6095b1;
  outline: none;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-top: 20px;
}

.modal-actions button {
  padding: 12px 20px;
  font-size: 1em;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.modal-actions .btn-primary {
  background-color: #0288d1;
  color: white;
  border: none;
}

.modal-actions .btn-primary:hover {
  background-color: #6095b1;
}

.modal-actions .cancel-btn {
  background-color: #e0e0e0;
  color: #333;
}

.modal-actions .cancel-btn:hover {
  background-color: #d1d1d1;
}

.no-resize {
  resize: none;
}

p.thread-description {
  margin: 1rem;
}

.checkbox-container {
  display: inline-flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}
</style>
