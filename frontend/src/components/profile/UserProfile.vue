<template>
  <div class="profile-container">
    <div class="profile-content">
      <!-- Left Side: User Info and Tags -->
      <div class="profile-left">
        <div v-if="user" class="profile-header">
          <!-- User Name and Bio -->
          <div class="profile-info">
            <div class="profile-name">
              <h1>{{ user.firstname }} {{ user.lastname }}</h1>
            </div>
            <p>@{{ user.username }}</p>

            <!-- Bio -->
            <p v-if="user.description">{{ user.description }}</p>
            <p v-else>Noch keine Biografie hinzugefügt.</p>

            <!-- Edit Bio Button -->
            <button
              v-if="isOwnProfile"
              class="edit-bio-btn"
              @click="openEditBioModal"
            >
              Bio bearbeiten
            </button>
          </div>

          <!-- Display User Tags -->
          <div>
            <div class="tag-chips" v-if="selectedTags.length > 0">
              <span
                v-for="(tag, index) in selectedTags"
                :key="index"
                class="chip"
              >
                {{ tag }}
              </span>
            </div>

            <!-- Edit Tags Button -->
            <button
              v-if="isOwnProfile"
              class="edit-tags-btn"
              @click="openEditTagsModal"
            >
              Tags bearbeiten
            </button>
          </div>

          <!-- Edit Tags Modal -->
          <div v-if="isEditTagsModalOpen" class="modal">
            <div class="modal-content">
              <span class="close-btn" @click="closeEditTagsModal">&times;</span>
              <h3>Bearbeite Tags</h3>

              <!-- Tags Display (No input, only selection and removal) -->
              <div class="tag-chips">
                <span
                  v-for="(tag, index) in selectedTags"
                  :key="index"
                  class="chip"
                >
                  {{ tag }}
                  <button type="button" @click="removeTag(index)">×</button>
                </span>
              </div>

              <div class="tag-list">
                <button
                  v-for="tag in allTags"
                  :key="tag"
                  :disabled="
                    selectedTags.includes(tag) || selectedTags.length >= 5
                  "
                  @click="addTag(tag)"
                >
                  {{ tag }}
                </button>
              </div>

              <button @click="updateTags" :disabled="selectedTags.length === 0">
                Speichern
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Side: Badges Section -->
      <div class="profile-right">
        <div v-if="badges.length > 0" class="badges-list">
          <div
            v-for="badge in badges"
            :key="badge.id"
            class="badge-item"
            @mouseover="showTooltip(badge.id)"
            @mouseleave="hideTooltip"
          >
            <img :src="badge.imageUrl" :alt="badge.type" class="badge-icon" />
            <!-- Tooltip -->
            <div v-if="tooltipBadgeId === badge.id" class="badge-tooltip">
              <h3>{{ translateBadgeType(badge.type) }}</h3>
              <p>Level: {{ badge.currentLevel }}</p>
              <p>
                {{ badge.points }} /
                {{ badge.points + badge.pointsToNextLevel }}
              </p>
              <p class="badge-description">{{ badge.description }}</p>
            </div>
          </div>
        </div>
        <div v-else>
          <p>Noch keine Abzeichen erhalten.</p>
        </div>
      </div>
    </div>

    <!-- Modal zum Bearbeiten der Bio -->
    <div v-if="showEditBioModal" class="modal">
      <div class="modal-content">
        <h2>Biografie bearbeiten</h2>
        <textarea
          v-model="newBio"
          placeholder="Geben Sie Ihre neue Bio ein..."
        ></textarea>
        <div class="modal-actions">
          <button @click="updateBio">Absenden</button>
          <button @click="closeEditBioModal">Abbrechen</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import config from "@/config";
import Cookies from "js-cookie";

export default {
  name: "UserProfile",
  props: {
    user: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      badges: [],
      tooltipBadgeId: null,
      showEditBioModal: false,
      newBio: "",
      isEditTagsModalOpen: false,
      selectedTags: [],
      allTags: [],
    };
  },
  computed: {
    isOwnProfile() {
      return Number(this.user.id) === Number(Cookies.get("userId"));
    },
  },
  methods: {
    async fetchProfile() {
      try {
        const badgesResponse = await fetch(
          `${config.apiBaseUrl}/users/${this.user.id}/rewards`,
        );
        if (!badgesResponse.ok) {
          throw new Error("Badges data fetch failed");
        }
        const badgeData = await badgesResponse.json();

        this.badges = await Promise.all(
          badgeData.map(async (badge) => {
            const badgeImageResponse = await fetch(
              `${config.apiBaseUrl}/rewards/badge?type=${badge.type}&currentLevel=${badge.currentLevel}`,
            );
            if (!badgeImageResponse.ok) {
              throw new Error("Badge image fetch failed");
            }
            const badgeImageBlob = await badgeImageResponse.blob();
            const imageUrl = URL.createObjectURL(badgeImageBlob);

            return {
              ...badge,
              imageUrl: imageUrl,
            };
          }),
        );
      } catch (error) {
        console.error("Error fetching profile:", error);
      }
    },

    async fetchTags() {
      try {
        const allTagsResponse = await fetch(`${config.apiBaseUrl}/tags`);
        const tagData = await allTagsResponse.json();
        this.allTags = tagData.map((tag) => tag.name);

        const response = await fetch(
          `${config.apiBaseUrl}/users/${this.user.id}/tags`,
        );
        const data = await response.json();
        this.selectedTags = data.map((tag) => tag.name);
      } catch (error) {
        console.error("Fehler beim Laden der Tags:", error);
      }
    },
    async openEditTagsModal() {
      this.isEditTagsModalOpen = true;
      await this.fetchTags();
    },

    closeEditTagsModal() {
      this.isEditTagsModalOpen = false;
    },

    addTag(tag) {
      if (this.selectedTags.length < 5 && !this.selectedTags.includes(tag)) {
        this.selectedTags.push(tag);
      }
    },

    removeTag(index) {
      this.selectedTags.splice(index, 1);
    },

    async updateTags() {
      try {
        const response = await fetch(
          `${config.apiBaseUrl}/users/${this.user.id}/tags`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(this.selectedTags),
          },
        );
        if (!response.ok) {
          throw new Error("Fehler beim Aktualisieren der Tags");
        }
        this.closeEditTagsModal();
      } catch (error) {
        console.error("Fehler beim Aktualisieren der Tags:", error);
      }
    },

    translateBadgeType(type) {
      const translations = {
        ATTENDER: "Teilnehmender",
        FEEDBACK_GIVER: "Feedbackgeber",
        CLEAN_SUBMITTER: "Keine Halben Sachen",
      };
      return translations[type] || type;
    },
    showTooltip(id) {
      this.tooltipBadgeId = id;
    },
    hideTooltip() {
      this.tooltipBadgeId = null;
    },

    openEditBioModal() {
      this.newBio = this.user.description || "";
      this.showEditBioModal = true;
    },

    closeEditBioModal() {
      this.showEditBioModal = false;
    },

    async updateBio() {
      try {
        const userBio = {
          role: this.user.role,
          firstname: null,
          lastname: null,
          username: this.user.username,
          description: this.newBio,
        };

        const response = await fetch(
          `${config.apiBaseUrl}/users/${this.user.id}`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(userBio),
          },
        );

        if (!response.ok) {
          throw new Error("Biographie konnte nicht gesendet werden.");
        }
        this.user.description = this.newBio;
        this.closeEditBioModal();
        alert("Die Beschreibung wurde erfolgreich aktualisiert.");
      } catch (error) {
        console.error("Fehler beim Setzen der Biography:", error);
        alert("Die Biography konnte nicht aktualisiert werden.");
      }
    },
  },
  watch: {
    user: {
      immediate: true,
      handler(newValue) {
        if (newValue && newValue.id) {
          this.fetchProfile();
          this.fetchTags();
        }
      },
    },
  },
};
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  display: flex;
  justify-content: space-between;
  gap: 2rem;
}

.profile-content {
  display: flex;
  gap: 2rem;
  width: 100%;
}

.profile-left {
  flex: 0 0 50%;
}

.profile-right {
  flex: 0 0 40%;
}

.profile-header {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.profile-name h1 {
  justify-content: center;
  font-size: 2rem;
  margin: 0;
}

.profile-info p {
  font-size: 1.1rem;
  color: #555;
}

.edit-bio-btn,
.edit-tags-btn {
  margin-left: 1rem;
  padding: 0.5rem 1rem;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.edit-bio-btn:hover,
.edit-tags-btn:hover {
  background-color: #0056b3;
}

.tag-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 15px 0;
}

.chip {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  background-color: #e0e0e0;
  border-radius: 20px;
  font-size: 14px;
}

.chip button {
  margin-left: 8px;
  background: none;
  border: none;
  cursor: pointer;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tag-list button {
  padding: 8px 16px;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.tag-list button:disabled {
  background-color: #d3d3d3;
  cursor: not-allowed;
}

.badges-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: left;
  padding-top: 7rem;
}

.badge-item {
  position: relative;
  text-align: center;
  transition: transform 0.3s ease-in-out;
  z-index: 1;
}

.badge-item:hover {
  transform: scale(1.1);
  z-index: 2;
}

.badge-icon {
  width: 90px;
  height: 90px;
  object-fit: contain;
}

.badge-tooltip {
  position: absolute;
  bottom: 80%;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(8, 56, 95, 0.8);
  color: #fff;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  white-space: normal;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  z-index: 10;
  opacity: 1;
  z-index: 9999;
  animation: fadeIn 0.3s ease-in-out;
  max-width: 300px;
}

.badge-tooltip h3 {
  font-size: 1.5rem;
  margin: 0;
  font-weight: bold;
}

.badge-tooltip p {
  margin: 0.5rem 0 0;
  font-size: 0.875rem;
}

.badge-description {
  font-style: italic;
  color: #ccc;
  margin-top: 0.5rem;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  width: 400px;
}

textarea {
  width: 100%;
  height: 100px;
  margin-bottom: 1rem;
}

.modal-actions button {
  padding: 0.5rem 1rem;
  margin-right: 0.5rem;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.modal-actions button:hover {
  background-color: #0056b3;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}
</style>
