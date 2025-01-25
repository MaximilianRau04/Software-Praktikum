<template>
  <div class="profile-container">
    <TrainerProfile
      v-if="isTrainer && trainerProfileData"
      :user="userData"
      :trainer="trainerProfileData"
    />
  </div>
</template>

<script>
import axios from "axios";
import TrainerProfile from "@/components/profile/TrainerProfile.vue";
import UserProfile from "@/components/profile/UserProfile.vue";
import config from "@/config";

// <UserProfile v-else :user="userData" />
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
      return this.userData?.role === "ADMIN";
    },
  },
  methods: {
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
            throw new Error("Trainer data fetch failed");
          }
          this.trainerProfileData = await trainerResponse.json();
          console.log(this.trainerProfileData.bio);
        }
      } catch (error) {
        console.error("Error fetching profile:", error);
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
