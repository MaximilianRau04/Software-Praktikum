<template>
    <div>
        <div class="header-container">
            <TrainerHeader :user="user" :bio="trainer.bio" />
            <button v-if="user.id === trainer.userId" @click="navigateToManage" class="manage-button">
                Verwalten
            </button>
        </div>
        <div v-if="trainer">
            <div>
                <TrainerTags :tags="trainerTags" />
            </div>
            <div>
                <TrainerStarRating :averageRating="trainer.averageRating" />
            </div>
            <div>
                <TrainerComments :trainerId="trainer.id" :userId="trainer.userId" />
            </div>
            <div>
                <TrainerEvents :pastEvents="pastEvents" :futureEvents="futureEvents" />
            </div>
        </div>
        <div v-else>
            <p>Lade Trainerdaten...</p>
        </div>
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
            pastEvents: [],
            futureEvents: [],
        };
    },
    watch: {
        trainer: "fetchTrainerDetails",
    },
    methods: {
        async fetchTrainerDetails() {
            try {
                const tagsResponse = await fetch(`${config.apiBaseUrl}/trainerProfiles/${this.trainer.id}/expertiseTags`);
                if (tagsResponse.ok) {
                    this.trainerTags = await tagsResponse.json();
                }

                const eventResponse = await fetch(`${config.apiBaseUrl}/users/${this.trainer.userId}/hostedEvents`);
                if (eventResponse.ok) {
                    const eventsData = await eventResponse.json();
                    this.pastEvents = eventsData.pastEvents;
                    this.futureEvents = eventsData.futureEvents;
                }
            } catch (error) {
                console.error("Error fetching trainer details:", error);
            }
        },
        navigateToManage() {
            this.$router.push({ name: "TrainerProfileManage", params: { trainerId: Number(this.trainer.id) } });
        },
    },
    created() {
        this.fetchTrainerDetails();
    },
};
</script>

<style scoped>
.header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.manage-button {
    background-color: #009ee2;
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 5px;
    cursor: pointer;
    font-size: 1rem;
    transition: background-color 0.2s;
}

.manage-button:hover {
    background-color: #007bb8;
}
</style>