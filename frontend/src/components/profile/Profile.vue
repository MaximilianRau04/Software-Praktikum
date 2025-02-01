<template>
  <div class="profile-container">
    <TrainerProfile
      v-if="trainerProfileData"
      :user="userData"
      :trainer="trainerProfileData"
    />
    <UserProfile v-else :user="userData" />
  </div>
</template>

<script>
import TrainerProfile from "@/components/profile/TrainerProfile.vue";
import UserProfile from "@/components/profile/UserProfile.vue";
import config from "@/config";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { useAuth } from "@/util/auth";
import api from "@/util/api";

export default {
  name: "Profile",
  components: { TrainerProfile, UserProfile },
  props: ["username"],
  data() {
    return {
      userData: {},
      trainerProfileData: {},
    };
  },
  computed: {
    isTrainer() {
      return this.userData.role === "ADMIN";
    },
  },
  methods: {
    /**
     * Fetches the user data and the trainer profile data if the user is a trainer
     */
    async fetchProfile() {
      try {
        const userResponse = await api.get(`/users/search?username=${this.username}`);
        if (userResponse.status !== 200) {
          throw new Error("User data fetch failed");
        }
        this.userData = await userResponse.data;
        

        if (this.isTrainer) {
          const trainerResponse = await api.get(`/trainerProfiles/${this.userData.id}`);
          if (trainerResponse.status !== 200) {
            showToast(
              new Toast(
                "Fehler",
                `Trainerprofil konnte nicht geladen werden.`,
                "error",
                faXmark,
                5,
              ),
            );
          }
          this.trainerProfileData = await trainerResponse.data;
        }
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
                `Trainerprofil konnte nicht geladen werden.`,
                "error",
                faXmark,
                5,
          ),
        );
      }
    },
  },
  mounted() {
    this.fetchProfile();
  },
};
</script>

<style scoped>
.profile-container {
  padding: 2rem;
}
</style>
