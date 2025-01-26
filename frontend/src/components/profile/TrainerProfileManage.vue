<template>
  <div>
    <button class="back-button" @click="goBack">Zurück</button>
  </div>

  <div class="manage-page">
    <h1>Trainerprofil verwalten</h1>
    <div class="manage-section">
      <h2>Expertise Tags</h2>
      <div>
        <label for="tags">Expertise Tags</label>
        <p>
          Bitte wählen Sie bis zu 5 Expertise Tags für Ihr Trainerprofil aus:
        </p>

        <!-- Input field for tags -->
        <input
          type="text"
          v-model="tagInput"
          placeholder="Tags eingeben und durch Komma trennen"
          @input="filterTags"
          @keyup="handleKeyup"
          :disabled="selectedTags.length >= 5"
        />

        <!-- Display selected tags -->
        <div class="tag-chips">
          <span v-for="(tag, index) in selectedTags" :key="index" class="chip">
            {{ tag }}
            <button type="button" class="remove-tag" @click="removeTag(index)">
              &times;
            </button>
          </span>
        </div>

        <!-- Display filtered tags to choose from -->
        <div class="tag-list">
          <button
            v-for="tag in filteredTags"
            :key="tag"
            type="button"
            @click="addTag(tag)"
            :disabled="selectedTags.includes(tag)"
          >
            {{ tag }}
          </button>
        </div>
      </div>
    </div>

    <div class="manage-section">
      <h2>Empfohlene Kommentare</h2>
      <div v-for="event in events" :key="event.id" class="event-section">
        <button @click="toggleDropdown(event.id)" class="event-toggle">
          {{ event.name }}
        </button>
        <div v-if="activeEvent === event.id" class="dropdown">
          <div class="comments-grid">
            <div
              v-for="comment in event.comments"
              :key="comment.id"
              class="comment-card"
            >
              <div class="card-header">
                <span class="author">{{ comment.author }}</span>
                <span class="rating">⭐ {{ comment.rating.toFixed(1) }}</span>
              </div>
              <div class="card-body">
                <p>{{ comment.comment }}</p>
                <input
                  type="checkbox"
                  :value="comment"
                  v-model="pinnedComments"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <button @click="saveChanges" class="save-button">Save Changes</button>
  </div>
</template>

<script>
import axios from "axios";
import config from "@/config";
import Cookies from "js-cookie";
import { useRoute, useRouter } from "vue-router";

export default {
  props: {
    trainerId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      router: useRouter(),
      username: Cookies.get("username"),
      allTags: [],
      selectedTags: [],
      tagInput: "",
      filteredTags: [],
      events: [],
      pinnedComments: [],
      activeEvent: null,
    };
  },
  methods: {
    /**
     * Fetch data for selected tags and pinned comments
     */
    async fetchData() {
      try {
        const trainerTagsResponse = await axios.get(
          `${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/expertiseTags`,
        );
        this.selectedTags = trainerTagsResponse.data.map((tag) => tag.name);
        const eventsResponse = await axios.get(
          `${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/comments-by-event`,
        );
        this.events = eventsResponse.data.events;

        const tagsResponse = await axios.get(`${config.apiBaseUrl}/tags`);
        this.allTags = tagsResponse.data.map((tag) => tag.name);
        this.filteredTags = this.allTags.filter(
          (tag) =>
            !this.selectedTags.some((selectedTag) => selectedTag === tag),
        );
      } catch (error) {
        showToast(
          new Toast(
            "Error",
            `Fehler beim Laden der expertise tage und Kommentare`,
            "error",
            faXmark,
            10,
          ),
        );
      }
    },
    toggleDropdown(eventId) {
      this.activeEvent = this.activeEvent === eventId ? null : eventId;
    },
    /**
     * Save changes to selected tags and pinned comments
     */
    async saveChanges() {
      try {
        await axios.post(
          `${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/expertiseTags`,
          this.selectedTags,
        );

        const pinnedIds =
          this.pinnedComments.length > 0
            ? this.pinnedComments.map((comment) => comment.feedbackId)
            : [];
        await axios.post(
          `${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/pinned-comments`,
          pinnedIds,
        );

        showToast(
          new Toast(
            "Info",
            `änderungen wurden erfolgreich gespeichert!`,
            "info",
            faCheck,
            5,
          ),
        );
      } catch (error) {
        showToast(
          new Toast(
            "Error",
            `Fehler Fetchen der exchange days: ${error.message}`,
            "error",
            faXmark,
            10,
          ),
        );
      }
    },
    handleKeyup(event) {
      if (event.key === ",") {
        this.addTagFromInput();
      }
    },

    /**
     * Add tag from input field to selected tags
     */
    addTagFromInput() {
      const trimmedInput = this.tagInput.trim().slice(0, -1);

      if (!trimmedInput) return;

      if (
        trimmedInput &&
        !this.selectedTags.includes(trimmedInput) &&
        this.selectedTags.length < 5
      ) {
        this.selectedTags.push(trimmedInput);
      }

      this.tagInput = "";
      this.filteredTags = [...this.allTags];
    },

    addTag(tag) {
      if (!this.selectedTags.includes(tag) && this.selectedTags.length < 5) {
        this.selectedTags.push(tag);
      }
    },

    removeTag(index) {
      this.selectedTags.splice(index, 1);
      this.filteredTags = this.allTags.filter(
        (tag) =>
          !this.selectedTags.some((selectedTag) => selectedTag.id === tag.id),
      );
    },

    filterTags() {
      const query = this.tagInput.toLowerCase();
      this.filteredTags = this.allTags.filter((tagName) =>
        tagName.toLowerCase().includes(query),
      );
    },

    goBack() {
      this.router.push({
        name: "Profile",
        params: { username: Cookies.get("username") },
      });
    },
  },
  created() {
    this.fetchData();
  },
};
</script>

<style scoped>
.back-button {
    position: fixed;
    top: 6rem;
    left: 6rem;
    background-color: #009ee2;
    color: white;
    border: none;
    border-radius: 5px;
    padding: 0.5rem 1rem;
    font-size: 1rem;
    cursor: pointer;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: background-color 0.3s;
}

.back-button:hover {
  background-color: #007bb5;
}

.page-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 0 auto;
    max-width: 800px;
    padding: 2rem;
}

.page-header {
    text-align: center;
    font-size: 2rem;
    font-weight: bold;
}

.section-header {
    text-align: center;
    margin-bottom: 0.5rem;
}

.section-description {
    text-align: justify;
    font-size: 1rem;
    color: #555;
    margin-bottom: 1.5rem;
}

textarea {
    width: 70%;
    max-width: 600px;
    min-height: 50px;
    max-height: 50px;
    padding: 10px;
    margin: 0 auto;
    display: block;
    border-radius: 5px;
    border: 1px solid #ccc;
    font-size: 16px;
    line-height: 1.5;
}

textarea:focus {
    border-color: #009EE2;
    outline: none;
}

.manage-page {
    padding: 1rem;
    display: flex;
    flex-direction: column;
}

.manage-section {
    border: 1px solid #ddd;
    padding: 1.5rem;
    border-radius: 10px;
    background-color: #f9f9f9;
}

.event-section {
  margin-bottom: 1rem;
}

.event-toggle {
  background-color: #009ee2;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 0.5rem;
}

.event-toggle:hover {
  background-color: #007bb8;
}

.dropdown {
  margin-top: 0.5rem;
  padding-left: 1rem;
  border-left: 2px solid #ddd;
}

.comments-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.comment-card {
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #fff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.card-body {
    position: relative;
    padding: 1rem;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.card-body p {
    flex-grow: 1;
    margin-bottom: 2rem;
    overflow-wrap: break-word;
}

.save-button {
  background-color: #4caf50;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
}

.save-button:hover {
  background-color: #3e8e41;
}

.input-group {
  margin-bottom: 1rem;
}

.input-group input {
  padding: 0.5rem;
  margin-right: 1rem;
}

.tag-chips {
  display: flex;
  flex-wrap: wrap;
  margin: 0.5rem 0;
}

.chip {
    background-color: #009ee2;
    color: white;
    padding-top: 0.3rem;
    padding-left: 0.7rem;
    border-radius: 20px;
    margin-right: 0.5rem;
}

.remove-tag {
  background: transparent;
  border: none;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  margin-left: 0.5rem;
}

.tag-list button {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  padding: 0.5rem;
  border-radius: 5px;
  margin-right: 0.5rem;
  cursor: pointer;
}

.tag-list button:disabled {
  background-color: #e0e0e0;
  cursor: not-allowed;
}

.pinned-comments-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
}

.pinned-comment-card {
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pin-button {
    position: absolute;
    bottom: 1rem;
    right: 1rem;
    background-color: #009ee2;
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.pin-button:hover {
    background-color: #007bb5;
}

.pin-button.disabled {
    background-color: #e0e0e0;
    color: #b0b0b0;
    cursor: not-allowed;
}

.pin-button.disabled:hover {
    background-color: #e0e0e0;
}

.unpin-button {
    position: absolute;
    bottom: 1rem;
    right: 1rem;
    background-color: #ff4d4d;
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.unpin-button:hover {
    background-color: #e63e3e;
}
</style>
