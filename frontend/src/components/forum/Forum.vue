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
import "@/assets/forum/forum.css";

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
