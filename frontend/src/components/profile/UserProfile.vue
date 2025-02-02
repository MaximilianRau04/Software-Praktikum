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
            <button v-if="isOwnProfile" class="edit-bio-btn" @click="openEditBioModal">
              Bio bearbeiten
            </button>
          </div>

          <!-- Display User Tags -->
          <div>
            <div class="tag-chips" v-if="selectedTags.length > 0">
              <span v-for="(tag, index) in selectedTags" :key="index" class="chip">
                {{ tag.name }}
              </span>
            </div>

            <!-- Edit Tags Button -->
            <button v-if="isOwnProfile" class="edit-tags-btn" @click="openEditTagsModal">
              Tags bearbeiten
            </button>
          </div>

          <!-- Edit Tags Modal -->
          <div v-if="isEditTagsModalOpen" class="modal">
            <div class="modal-content">
              <span class="close-btn" @click="closeEditTagsModal">&times;</span>
              <h3>Bearbeite Tags</h3>

              <TagInput v-model="selectedTags" :available-tags="allTags" :tagSelect="true" />

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
          <div v-for="badge in badges" :key="badge.id" class="badge-item" @mouseover="showTooltip(badge.id)"
            @mouseleave="hideTooltip">
            <img :src="badge.imageUrl" :alt="badge.type" class="badge-icon" />
            <div v-if="tooltipBadgeId === badge.id" class="badge-tooltip">
              <h3>{{ translateBadgeType(badge.type) }}</h3>

              <!-- Level-based content -->
              <template v-if="badge.levelBased">
                <p>Level: {{ badge.currentLevel }}</p>
                <p>
                  {{ badge.points }} /
                  {{ badge.points + badge.pointsToNextLevel }}
                </p>
              </template>

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
        <textarea v-model="newBio" placeholder="Geben Sie Ihre neue Bio ein..."></textarea>
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
import { useAuth } from "@/util/auth";
import api from "@/util/api";

import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import TagInput from "./TagInput.vue";

export default {
  name: "UserProfile",
  components: {
    TagInput,
  },
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
      const auth = useAuth();
      return this.user.id === auth.getUserId();
    },
  },
  methods: {
    async fetchProfile() {
      if (this.user.role === 'ADMIN') return;

      try {
        const badgesResponse = await api.get(`/users/${this.user.id}/rewards`);
        if (badgesResponse.status !== 200) {
          throw new Error("Badges data fetch failed");
        }
        const badgeData = await badgesResponse.data;

        this.badges = await Promise.all(
          badgeData.map(async (badge) => {
            const badgeImageResponse = await api.get(`/rewards/badge?type=${badge.type}&currentLevel=${badge.currentLevel}`, {
              responseType: 'blob'
            });
            if (badgeImageResponse.status !== 200) {
              throw new Error("Badge image fetch failed");
            }
            const imageUrl = URL.createObjectURL(badgeImageResponse.data);

            return {
              ...badge,
              imageUrl: imageUrl,
            };
          }),
        );
      } catch (error) {
        console.error(error)
        showToast(
          new Toast(
            "Fehler",
            "Errungenschaften konnten nicht geladen werden.",
            "error",
            faXmark,
            5
          )
        );
      }
    },

    async fetchTags() {
      try {
        const allTagsResponse = await api.get(`/tags`);
        const tagData = await allTagsResponse.data;
        this.allTags = tagData;

        const response = await api.get(`/users/${this.user.id}/tags`);
        const data = await response.data;
        this.selectedTags = data;
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            "User-Tags konnten nicht geladen werden.",
            "error",
            faXmark,
            5
          )
        );
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
        const response = await api.put(`/users/${this.user.id}/tags`, this.selectedTags.map(tag => tag.name));
        if (response.status !== 200) {
          throw new Error("Fehler beim Aktualisieren der Tags");
        }
        this.closeEditTagsModal();
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            "User-Tags konnten nicht aktualisiert werden.",
            "error",
            faXmark,
            5
          )
        );
      }
    },

    translateBadgeType(type) {
      const translations = {
        ATTENDER: "Teilnehmender",
        FEEDBACK_GIVER: "Feedbackgeber",
        CLEAN_SUBMITTER: "Keine Halben Sachen",
        ALLROUNDER: "Vielseitig",
        SOCIAL_BUTTERFLY: "Schmetterling",
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

        const response = await api.put(`/users/${this.user.id}`, userBio);

        if (response.status !== 200) {
          throw new Error("Biographie konnte nicht gesendet werden.");
        }
        this.user.description = this.newBio;
        this.closeEditBioModal();
        showToast(
          new Toast(
            "Erfolg",
            "Ihre Bio wurde erfolgreich aktualisiert.",
            "success",
            faXmark,
            5
          )
        );
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            "Ihre Bio konnte nicht aktualisiert werden.",
            "error",
            faXmark,
            5
          )
        );
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
  max-width: 1200px;
  margin: 2rem auto;
  padding: 2rem;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.profile-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  align-items: start;
}

.profile-header {
  background: #f8f9fa;
  padding: 2rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.profile-name h1 {
  font-size: 2.25rem;
  color: #2c3e50;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.profile-info p {
  font-size: 1.1rem;
  color: #4a5568;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.edit-bio-btn,
.edit-tags-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.edit-bio-btn:hover,
.edit-tags-btn:hover {
  background: #0056b3;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tag-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin: 1.5rem 0;
}

.chip {
  background: #007bff15;
  color: #007bff;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  border: 1px solid #007bff30;
}

.modal-content {
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
}

.badges-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 1.5rem;
  padding: 2rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.badge-item {
  position: relative;
  text-align: center;
  transition: all 0.2s ease;
}

.badge-icon {
  width: 80px;
  height: 80px;
  object-fit: contain;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.badge-tooltip {
  background: #2c3e50;
  color: white;
  padding: 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  line-height: 1.4;
  width: 220px;
  pointer-events: none;
}

@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .profile-header {
    padding: 1.5rem;
  }

  .badges-list {
    padding: 1.5rem;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-content {
  animation: fadeIn 0.3s ease;
}
</style>
