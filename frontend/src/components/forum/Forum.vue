<template>
  <div class="forum-container">
    <!-- Threads -->
    <div v-if="!selectedThreadId" class="threads-view">
      <h1>Diskussionsforum</h1>

      <button
        class="btn-primary create-thread-btn"
        @click="prepareCreateThread"
      >
        + Neuer Thread
      </button>

      <div class="thread-list">
        <div
          v-for="thread in threads"
          :key="thread.threadId"
          class="thread-item"
        >
          <!-- Thread Inhalt -->
          <div class="thread-content" @click="selectThread(thread.threadId)">
            <h3>{{ thread.title }}</h3>
            <p class="thread-description">
              {{ truncate(thread.description, 100) }}
            </p>
            <div class="thread-meta">
              <span>{{ thread.forumPosts?.length || 0 }} Antworten</span>
            </div>
          </div>

          <!-- Buttons für Admin -->
          <div v-if="isAdmin" class="thread-actions" @click.stop>
            <button
              class="btn-secondary edit-btn"
              @click.stop="prepareEditThread(thread)"
            >
              Bearbeiten
            </button>
            <button
              class="btn-secondary delete-btn"
              @click.stop="deleteThread(thread.threadId)"
            >
              Löschen
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Thread Detail View -->
    <div v-else class="thread-detail">
      <button class="btn-secondary back-btn" @click="selectedThreadId = null">
        ← Zurück
      </button>

      <div v-if="selectedThread" class="thread-content">
        <h2>{{ selectedThread.title }}</h2>
        <p class="thread-description">{{ selectedThread.description }}</p>

        <div v-if="editingPost" class="edit-post-modal">
          <h3>Post bearbeiten</h3>
          <form @submit.prevent="savePostEdits">
            <textarea
              v-model="editingPost.content"
              rows="4"
              required
              class="input-field no-resize"
            ></textarea>
            <button type="submit" class="btn-primary">Speichern</button>
            <button type="button" class="btn-secondary" @click="cancelEdit">
              Abbrechen
            </button>
          </form>
        </div>

        <!-- Posts Section -->
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

              <div class="post-actions">
                <button
                  v-if="post.author.id == userId"
                  class="btn-secondary edit-btn"
                  @click="editPost(post)"
                >
                  Bearbeiten
                </button>

                <button
                  v-if="post.author.id == userId || isAdmin"
                  class="btn-secondary delete-btn"
                  @click="deletePost(post.id)"
                >
                  Löschen
                </button>
              </div>
            </div>
          </div>

          <p v-else class="no-posts">Noch keine Antworten.</p>
        </div>

        <!-- Post Creation Form -->
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

    <!-- Thread Creation/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <h2>
          {{ isEditing ? "Thread bearbeiten" : "Neuen Thread erstellen" }}
        </h2>
        <form
          @submit.prevent="isEditing ? updateThread() : createThread()"
          class="thread-form"
        >
          <div class="form-group">
            <label for="title">Titel:</label>
            <input
              id="title"
              v-model="currentThread.title"
              placeholder="Titel des Threads"
              required
              class="input-field"
            />
          </div>
          <div class="form-group">
            <label for="description">Beschreibung:</label>
            <textarea
              id="description"
              v-model="currentThread.description"
              placeholder="Beschreibung hinzufügen"
              rows="3"
              required
              class="input-field no-resize"
            ></textarea>
          </div>
          <div class="modal-actions">
            <button type="submit" class="btn-primary">
              {{ isEditing ? "Speichern" : "Thread erstellen" }}
            </button>
            <button class="close-button" @click="showModal = false">×</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";
import { useAuth } from "@/util/auth";

export default {
  props: {
    threads: Array,
    focusedThreadId: {
      type: Number,
      default: null,
    },
  },
  watch: {
    focusedThreadId(newThreadId) {
      if (newThreadId) {
        this.selectThread(newThreadId);
      }
    },
  },
  data() {
    return {
      threads: [],
      selectedThreadId: null,
      selectedThread: null,
      newThread: {
        title: "",
        description: "",
      },
      currentThread: {
        title: "",
        description: "",
        id: null,
      },
      newPost: {
        content: "",
        isAnonymous: false,
      },
      showModal: false,
      editingPost: null,
      isAdmin: useAuth().isAdmin.value,
      isEditing: false,
      userId: useAuth().getUserId(),
    };
  },
  methods: {
    /**
     * Deletes a post
     * @param {number} postId
     */
    async deletePost(postId) {
      if (!confirm("Sind Sie sicher, dass Sie diesen Post löschen möchten?")) {
        return;
      }
      try {
        const response = await api.delete(`/forumposts/${postId}`);
        this.fetchThreadDetail();
        showToast(
          new Toast(
            "Erfolg",
            `Post wurde erfolgreich gelöscht`,
            "success",
            faCheck,
            5,
          ),
        );
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            `Der Beitrag konnte nicht gelöscht werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

    editPost(post) {
      this.editingPost = { ...post };
    },

    /**
     * Saves the edited post
     */
    async savePostEdits() {
      try {
        const response = await api.put(`/forumposts/${this.editingPost.id}`, {
          content: this.editingPost.content,
          authorId: this.editingPost.author.id,
          forumThreadId: this.selectedThreadId,
        });
        this.fetchThreadDetail();
        this.editingPost = null;
        if (response.status === 200) {
          showToast(
            new Toast(
              "Erfolg",
              `Post wurde erfolgreich bearbeitet`,
              "success",
              faCheck,
              5,
            ),
          );
        }
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            `Der Beitrag konnte nicht bearbeitet werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

    cancelEdit() {
      this.editingPost = null;
    },

    prepareCreateThread() {
      this.isEditing = false;
      this.currentThread = { title: "", description: "" };
      this.showModal = true;
    },

    prepareEditThread(thread) {
      this.isEditing = true;
      this.currentThread = {
        id: thread.threadId,
        title: thread.title,
        description: thread.description,
      };
      this.showModal = true;
    },

    /**
     * Deletes a thread
     * @param {number} threadId
     */
    async deleteThread(threadId) {
      if (
        !confirm("Sind Sie sicher, dass Sie diesen Thread löschen möchten?")
      ) {
        return;
      }
      try {
        const response = await api.delete(`/forumthreads/${threadId}`);
        this.fetchThreads();

        showToast(
          new Toast(
            "Erfolg",
            `Thread wurde erfolgreich gelöscht`,
            "success",
            faCheck,
            5,
          ),
        );
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            `Der Thread konnte nicht gelöscht werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

    /**
     * Updates a thread
     */
    async updateThread() {
      const eventId = this.$route.params.eventId;
      try {
        const response = await api.put(
          `/forumthreads/${this.currentThread.id}`,
          {
            title: this.currentThread.title,
            description: this.currentThread.description,
            eventId: eventId,
          },
        );
        this.fetchThreads();
        this.showModal = false;

        if (response.status === 200) {
          showToast(
            new Toast(
              "Erfolg",
              `Thread wurde erfolgreich bearbeitet`,
              "success",
              faCheck,
              5,
            ),
          );
        }
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            `Thread konnte nicht bearbeitet werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

    /**
     * Fetches all threads
     */
    async fetchThreads() {
      const eventId = this.$route.params.eventId;
      try {
        const response = await api.get(`/events/${eventId}/forum`);
        this.threads = response.data;
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            `Threads konnten nicht geladen werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

    /**
     * Creates a new thread
     */
    async createThread() {
      const eventId = this.$route.params.eventId;
      try {
        const newThreadData = {
          title: this.currentThread.title,
          description: this.currentThread.description,
          eventId: eventId,
        };

        const response = await api.post(`/forumthreads`, newThreadData);
        this.fetchThreads();
        this.currentThread.title = "";
        this.currentThread.description = "";
        this.showModal = false;
        if (response.status === 200) {
          showToast(
            new Toast(
              "Erfolg",
              `Thread wurde erfolgreich erstellt`,
              "success",
              faCheck,
              5,
            ),
          );
        }
      } catch (error) {
        console.error(error);
        showToast(
          new Toast(
            "Fehler",
            `Thread konnte nicht erstellt werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

    /**
     * Selects a thread
     * @param {number} threadId
     */
    async selectThread(threadId) {
      this.selectedThreadId = threadId;
      this.fetchThreadDetail();
    },

    /**
     * Fetches the details of a thread
     */
    async fetchThreadDetail() {
      try {
        const response = await api.get(
          `/forumthreads/${this.selectedThreadId}`,
        );
        this.selectedThread = response.data;
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            `Threads konnten nicht geladen werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

    /**
     * Creates a new post
     */
    async createPost() {
      try {
        const userId = useAuth().getUserId();

        const newPostData = {
          content: this.newPost.content,
          forumThreadId: this.selectedThreadId,
          authorId: userId,
          anonymous: this.newPost.isAnonymous,
        };

        const response = await api.post(`/forumposts`, newPostData);

        this.fetchThreadDetail();
        this.newPost.content = "";
        this.newPost.isAnonymous = false;
        if (response.status === 200) {
          showToast(
            new Toast(
              "Erfolg",
              `Post wurde erfolgreich erstellt`,
              "success",
              faCheck,
              5,
            ),
          );
        }
      } catch (error) {
        console.error(error);
        showToast(
          new Toast(
            "Fehler",
            `Beitrag konnte nicht erstellt werden.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

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
    this.userId = useAuth().getUserId();
    this.isAdmin = useAuth().isAdmin.value;
  },
};
</script>

<style scoped>
.forum-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem;
  overflow-y: auto;
  font-family: "Arial", sans-serif;
}

.thread-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.thread-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #fefefe;
  border: 1px solid #ddd;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition:
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.no-resize {
  resize: none;
}

.thread-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.thread-content {
  flex: 1;
  margin-right: 1.5rem;
}

.thread-meta {
  font-size: 0.9em;
  color: #6c757d;
}

.thread-actions {
  display: flex;
  gap: 10px;
}

.btn-primary,
.btn-secondary {
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 0.95em;
  cursor: pointer;
  border: none;
  transition:
    background-color 0.2s ease,
    transform 0.2s ease;
}

.btn-primary {
  background-color: #009ee2;
  color: #fff;
  margin-right: 1%;
}

.btn-primary:hover {
  background-color: #0180b6;
}

.btn-secondary {
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 0.95em;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s ease;
  white-space: nowrap;
}

.btn-secondary:hover {
  background-color: #bbc4cb;
}

.delete-btn {
  background-color: #dc3545;
  color: white;
}

.delete-btn:hover {
  background-color: #b02a37;
}

.edit-btn {
  background-color: #28a745;
  color: white;
}

.edit-btn:hover {
  background-color: #218838;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 20px;
}

.posts-section {
  margin-top: 2rem;
}

.post-item {
  display: flex;
  flex-direction: column;
  padding: 1.5rem;
  background-color: #fefefe;
  border: 1px solid #ddd;
  border-radius: 10px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
}

.post-content-wrapper {
  flex: 1;
  margin-right: 1.5rem;
  text-align: left;
}

.post-header {
  font-size: 0.9em;
  color: #6c757d;
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.post-content {
  font-size: 1em;
  color: #333;
}

.post-actions {
  display: flex;
  flex-direction: row;
  gap: 10px;
  min-width: fit-content;
  align-self: flex-end;
}

.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 1em;
  margin-bottom: 10px;
  box-sizing: border-box;
  transition: border-color 0.2s ease;
}

.input-field:focus {
  border-color: #007bff;
  outline: none;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1rem;
}

.close-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
}

@media (max-width: 768px) {
  .thread-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .thread-actions {
    margin-top: 10px;
  }

  .modal-content {
    width: 95%;
  }
  .post-item {
    flex-direction: column;
  }
  .post-content-wrapper {
    margin-right: 0;
    margin-bottom: 1rem;
  }

  .post-actions {
    flex-direction: row;
    width: 100%;
  }

  .btn-secondary {
    flex: 1;
    text-align: center;
  }
}
</style>
