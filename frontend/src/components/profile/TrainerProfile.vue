<template>
  <div v-if="trainer">
    <TrainerHeader :user="user" :bio="trainer.bio" />
  </div>
  <div v-else>
    <p>Lade Trainerdaten...</p>
  </div>
  <div>
    <TrainerTags :tags="trainerTags" />
  </div>
  <div>
    <TrainerStarRating :averageRating="trainer.averageRating" />
  </div>
  <div>
    <TrainerComments :comments="trainerComments" />
  </div>
  <div>
    <TrainerEvents :pastEvents="pastEvents" :futureEvents="futureEvents" />
  </div>
</template>

<script>
import TrainerHeader from "@/components/profile/TrainerHeader.vue";
import TrainerTags from "@/components/profile/TrainerTags.vue";
import TrainerStarRating from "@/components/profile/TrainerStarRating.vue";
import TrainerComments from "@/components/profile/TrainerComments.vue";
import TrainerEvents from "@/components/profile/TrainerEvents.vue";

import config from "@/config";

export default {
  name: "TrainerProfile",
  components: {
    TrainerHeader,
    TrainerTags,
    TrainerStarRating,
    TrainerComments,
    TrainerEvents,
  },
  props: {
    trainer: {
      type: Object,
      required: true,
    },
    user: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      trainerTags: [],
      trainerComments: [],
      pastEvents: [],
      futureEvents: [],
    };
  },
  watch: {
    trainerData: "fetchTrainerDetails",
  },
  methods: {
    async fetchTrainerDetails() {
      try {
        const tagsResponse = await fetch(
          `${config.apiBaseUrl}/trainerProfiles/${this.trainer.id}/expertiseTags`,
        );
        if (tagsResponse.ok) {
          this.trainerTags = await tagsResponse.json();
        }

        const commentsResponse = 0;

        const eventResponse = await fetch(
          `${config.apiBaseUrl}/users/${this.trainer.userId}/hostedEvents`,
        );
        if (eventResponse.ok) {
          const eventsData = await eventResponse.json();
          this.pastEvents = eventsData.pastEvents;
          this.futureEvents = eventsData.futureEvents;
        }
      } catch (error) {
        console.error("Error fetching trainer details:", error);
      }
    },
  },
  created() {
    this.fetchTrainerDetails();
  },
};
</script>

<style scoped>
.trainer-profile {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 2rem;
}
</style>
