<template>
    <div>
        <button v-if="trainer.userId === getUserIdFromCookie()" class="manage-button"
            @click="navigateToManage">Verwalten</button>
    </div>
    <div>
        <!-- Header Section -->
        <div class="header-container">
            <div class="header-content">
                <!-- Use TrainerHeader and pass user & bio -->
                <TrainerHeader v-if="user && trainer" :user="user" :bio="trainer.bio || 'Keine Biografie verfügbar'" />
                <!-- Expertise Tags -->
                <div class="tags-container">
                    <hr />
                    <TrainerTags v-if="trainer.id" :tags="trainerTags" />
                </div>
                <div class="rating">
                    <TrainerStarRating v-if="trainer.averageRating" :averageRating="trainer.averageRating" />
                </div>
            </div>
        </div>

        <!-- Comments Section -->
        <div class="comments-section">
            <div class="comments-grid">
                <TrainerComments v-if="trainer.id" :trainerId="trainer.id" />
            </div>
        </div>

        <!-- Past and Future Events -->
        <div class="trainer-events-wrapper">
            <TrainerEvents :pastEvents="pastEvents" :futureEvents="futureEvents" />
        </div>
    </div>
</template>

<script>
import TrainerHeader from "./TrainerHeader.vue";
import TrainerTags from "@/components/profile/TrainerTags.vue";
import TrainerStarRating from "@/components/profile/TrainerStarRating.vue";
import TrainerComments from "@/components/profile/TrainerComments.vue";
import TrainerEvents from "@/components/profile/TrainerEvents.vue";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import config from "@/config";
import Cookies from "js-cookie";
import { useAuth } from "@/util/auth";
import api from "@/util/api";


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
    mounted(){
        this.fetchTrainerDetails();
    },
    methods: {
        getUserIdFromCookie() {
            return useAuth().getUserId();
        },

        /**
         * Fetches the tags and events for the trainer
         */
        async fetchTrainerDetails() {
            console.log("Trainer before fetch:", this.trainer);
            try {
                const tagsResponse = await api.get(`/trainerProfiles/${this.trainer.id}/expertiseTags`);
                if (tagsResponse.status === 200) {
                    this.trainerTags = await tagsResponse.data;
                }

                const eventResponse = await api.get(`/users/${this.trainer.userId}/hostedEvents`);
                if (eventResponse.status === 200) {
                    const eventsData = await eventResponse.data;
                    this.pastEvents = eventsData.pastEvents;
                    this.futureEvents = eventsData.futureEvents;
                }
            } catch (error) {
                console.error(error)
                showToast(
                    new Toast(
                        "Fehler",
                        `Die Kommentare konnten nicht geladen werden.`,
                        "error",
                        faXmark,
                        5,
                    ),
                );
            }
        },
        navigateToManage() {
            console.log("Navigating with trainerId:", this.trainer.id, "Type:", typeof this.trainer.id);
            this.$router.push({
                name: "TrainerProfileManage",
                params: { trainerId: this.trainer.id },
            });
        },
    },
};
</script>

<style scoped>
.manage-button {
    position: fixed;
    top: 24%;
    right: 20%;
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

.manage-button:hover {
    background-color: #007bb5;
}

.header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    flex-wrap: wrap;
    max-width: 600px;
    margin: 0 auto;
    background-color: #ffffff;
}

.header-content {
    text-align: center;
    flex: 1;
}

.header-content h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.header-content .username {
    font-size: 1.2rem;
    color: #555;
    margin-bottom: 1rem;
}

.header-content .bio {
    margin-top: 1rem;
    font-size: 1rem;
    color: #333;
    max-width: 400px;
    margin: 0 auto;
}

.rating {
    flex-shrink: 0;
    margin-right: 1;
    display: flex;
    justify-content: center;
}

.tags-container {
    text-align: center;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.tags-container hr {
    border: 0;
    height: 1px;
    background: #ddd;
    margin: 1.5rem 0;
}

.comments-section {
    display: flex;
    justify-content: center;
    align-items: center;
    max-width: 900px;
    margin: 1rem auto;
    padding: 1rem;
}

.comments-section h2 {
    font-size: 1.5rem;
    margin-bottom: 1rem;
}

.comments-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 1rem;
}

.comments-grid div {
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    padding: 1rem;
    border-radius: 8px;
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

.trainer-events-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 0.1rem;
    width: 100%;
}

.trainer-events {
    display: flex;
    justify-content: center;
    gap: 2rem;
    max-width: 900px;
    min-width: 600px;
    width: 100%;
}
</style>