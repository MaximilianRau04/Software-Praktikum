<template>
  <div class="profile-container">
    <TrainerProfile
      v-if="isTrainer && trainerProfileData"
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

export default {
  name: "Profile",
  components: { TrainerProfile, UserProfile },
  props: ["username"],
  data() {
    return {
      userData: null,
      trainerProfileData: null,
    };
  },
  computed: {
    isTrainer() {
        console.log(this.userData?.role)
      return this.userData?.role === "ADMIN";
    },
  },
  methods: {
    /**
     * Fetches the user data and the trainer profile data if the user is a trainer
     */
    async fetchProfile() {
      try {
        const userResponse = await fetch(
          `${config.apiBaseUrl}/users/search?username=${this.username}`,
        );
        if (!userResponse.ok) {
          throw new Error("User data fetch failed");
        }
        this.userData = await userResponse.json();
        if (this.isTrainer) {
          const trainerResponse = await fetch(
            `/api/trainerProfiles/${this.userData.id}`,
          );
          if (!trainerResponse.ok) {
            showToast(
              new Toast(
                "Error",
                `Fehler der Laden der Trainer Profils`,
                "error",
                faXmark,
                10,
              ),
            );
          }
          this.trainerProfileData = await trainerResponse.json();
        }
      } catch (error) {
        showToast(
          new Toast(
            "Error",
            `Fehler der Laden der Trainer Profils`,
            "error",
            faXmark,
            10,
          ),
        );
      }
    },
  },
  created() {
    this.fetchProfile();
  },
};
</script>

<style scoped>
.profile-container {
  padding: 2rem;
}
</style>
