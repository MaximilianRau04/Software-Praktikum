<template>

    <div class="trainer-profile-manage-container">
        <!-- Back Button -->
        <button class="back-button" @click="goBack">Zurück</button>

        <!-- Main Page Wrapper -->
        <div class="page-wrapper">
            <h1 class="page-header">Trainerprofil verwalten</h1>

            <div class="manage-page">
                <!-- Trainer Bio Section -->
                <div>
                    <h2 class="section-header">Trainer Bio</h2>
                    <p class="section-description">Hier können Sie eine kurze Beschreibung über sich hinzufügen. Diese
                        Beschreibung wird auf Ihrem Trainerprofil angezeigt.</p>
                    <textarea v-model="trainer.bio" placeholder="Geben Sie hier Ihre Bio ein..." rows="5"></textarea>
                </div>

                <!-- Manage Expertise Tags -->
                <div>
                    <h2 class="section-header">Expertise Tags</h2>
                    <p class="section-description">Wählen Sie bis zu 5 Expertise Tags aus, um Ihr Profil zu
                        personalisieren.
                        Diese Tags werden für andere Nutzer auf ihrem Trainerprofil angezeigt. Falls Sie ein Tag
                        einfügen wollen, das Sie noch nicht in der Auflistung sehen, so schreiben Sie dieses direkt in
                        die Leiste rein und drücken Eingabe.</p>
                    <div>
                        <div class="tag-container">
                            <div class="tag-wrapper">
                                <!-- Input field for tags -->
                                <input type="text" v-model="tagInput"
                                    placeholder="Tag schreiben und mit Eingabe bestätigen" @input="filterTags"
                                    @keyup.enter="addTagFromInput" :disabled="selectedTags.length >= 5"
                                    class="tag-input" />



                                <!-- Display filtered tags to choose from -->
                                <div class="tag-list">
                                    <button v-for="tag in filteredTags" :key="tag.id" type="button" @click="addTag(tag)"
                                        :disabled="tag.selected || selectedTags.length >= 5">
                                        <TagIcon class="icon-tag" />
                                        {{ tag.name }}
                                    </button>
                                </div>

                                <!-- Display selected tags -->
                                <div class="tag-chips">
                                    <span v-if="selectedTags.length > 0" v-for="(tag, index) in selectedTags"
                                        :key="tag.id" class="chip">
                                        {{ tag.name }}
                                        <button type="button" class="remove-tag" @click="removeTag(tag.id)">
                                            &times;
                                        </button>
                                    </span>
                                    <span v-else class="chip impostor-button">
                                        Hallo
                                        <button type="button" class="remove-tag">
                                            &times;
                                        </button>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Pinned Comments Section -->
                <h2 class="section-header">Empfohlene Kommentare (max. 6)</h2>
                <p class="section-description">Hier können Sie bis zu 6 Kommentare anheften, die Ihr Trainerprofil
                    verbessern. Diese Kommentare haben Sie in früheren Events und Workshops von Ihren
                    Eventteilnehmerinnen und -teilnehmern erhalten.
                    Diese Kommentare werden auf Ihrem Trainerprofil für alle Nutzer angezeigt.
                </p>
                <div class="pinned-comments-grid">
                    <div v-for="comment in pinnedComments" :key="comment.id" class="pinned-comment-card">
                        <div class="card-header">
                            <span class="author">{{ comment.author }}</span>
                            <span class="rating">⭐ {{ comment.rating.toFixed(1) }}</span>
                        </div>
                        <div class="card-body">
                            <p>{{ comment.comment }}</p>
                            <button @click="unpinComment(comment)" class="unpin-button">Unpin</button>
                        </div>
                    </div>
                </div>

                <!-- Available Comments -->
                <h2 class="section-header">Verfügbare Kommentare</h2>
                <p class="section-description">Hier finden Sie Ihre gesamte Historie zu den erhaltenen Kommentaren aus
                    Ihren Veranstaltungen.
                    Klicken Sie dazu auf einen Eventnamen und wählen Sie Kommentare aus, die Sie gerne auf Ihrem
                    Trainerprofil präsentieren möchten.</p>

                <!-- Event Buttons Row -->
                <div class="events-row">
                    <button v-for="event in events" :key="event.id" @click="setActiveEvent(event)" class="event-button"
                        :class="{ 'active': activeEvent?.id === event.id }">
                        {{ event.name }}
                    </button>
                </div>

                <!-- Fixed Comments Section -->
                <div v-if="activeEvent && activeEvent.comments.length > 0" class="comments-section">
                    <div class="pinned-comments-grid">
                        <div v-for="comment in activeEvent.comments" :key="comment.id" class="pinned-comment-card">
                            <div class="card-header">
                                <span class="author">{{ comment.author }}</span>
                                <span class="rating">⭐ {{ comment.rating.toFixed(1) }}</span>
                            </div>
                            <div class="card-body">
                                <p class="comment-text">{{ comment.comment }}</p>
                                <button @click="pinComment(comment, activeEvent.id)" class="pin-button"
                                    :disabled="pinnedComments.length >= 6">
                                    Pin
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <p class="alt-description" v-else-if="activeEvent?.comments.length === 0"> Es wurden noch keine
                    Kommentare zu diesem Event erhalten.</p>

                <!-- Save Changes -->
                <button @click="saveChanges" class="save-button">Änderungen Speichern</button>
            </div>
        </div>
    </div>
</template>

<script>
import axios from "axios";
import config from "@/config";
import Cookies from "js-cookie";
import { useRoute, useRouter } from "vue-router";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import {
  TagIcon,
} from "@heroicons/vue/24/outline";

export default {
    components: {
        TagIcon,
    },
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
            trainer: {
                id: null,
                userId: null,
                bio: "",
            },
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
        async fetchData() {
            try {
                const trainerResponse = await axios.get(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}`);
                this.trainer = trainerResponse.data;

                const trainerTagsResponse = await axios.get(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/expertiseTags`);
                this.selectedTags = trainerTagsResponse.data

                const eventsResponse = await axios.get(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/comments-by-event`);
                this.events = eventsResponse.data.events;

                const commentsResponse = await axios.get(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/pinned-comments`);
                this.pinnedComments = commentsResponse.data;

                const pinnedCommentIds = new Set(this.pinnedComments.map(comment => comment.id));

                this.events.forEach(event => {
                    event.comments = event.comments.filter(comment => !pinnedCommentIds.has(comment.id));
                });

                const tagsResponse = await axios.get(`${config.apiBaseUrl}/tags`);
                this.allTags = tagsResponse.data

                this.updateTagStates();

            } catch (error) {
                console.error("Error fetching data:", error);
            }
        },

        pinComment(comment, eventId) {
            if (this.pinnedComments.length >= 6) {
                showToast(
                    new Toast(
                        "info",
                        `Sie dürfen nur bis zu 6 Kommentare auf ihrem Trainerprofil anzeigen.`,
                        "info",
                        faXmark,
                        10
                    )
                );
                return;
            }
            const event = this.events.find(e => e.id === eventId);
            if (event) {
                event.comments = event.comments.filter(c => c.id !== comment.id);
            }

            this.pinnedComments.push(comment);
        },

        unpinComment(comment) {
            this.pinnedComments = this.pinnedComments.filter(c => c.id !== comment.id);

            const originalEvent = this.events.find(e => e.id === comment.eventId);
            if (originalEvent) {
                originalEvent.comments.push(comment);
            }
        },

        async saveChanges() {
            try {
                const response = await axios.put(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}`, {
                    bio: this.trainer.bio,
                    userId: this.trainer.userId,
                });
                if (!response.status === 200) {
                    showToast(
                        new Toast(
                            "Fehler",
                            `Die Biographie konnte nicht gespeichert werden.`,
                            "error",
                            faXmark,
                            5
                        )
                    );
                }

                const responseTags = await axios.post(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/expertiseTags`, this.selectedTags.map(tag => tag.name));
                if (!responseTags.status === 200) {
                    showToast(
                        new Toast(
                            "Fehler",
                            `Die ExpertiseTags konnten nicht gespeichert werden`,
                            "error",
                            faXmark,
                            5
                        )
                    );
                }

                const pinnedIds = this.pinnedComments.map(comment => comment.id);
                await axios.post(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/pinned-comments`, pinnedIds);

                if (!pinnedIds.status === 200) {
                    showToast(
                        new Toast(
                            "Fehler",
                            `Die gewählten Kommentare konnten nicht angepinnt werden.`,
                            "error",
                            faXmark,
                            5
                        )
                    );
                }

                showToast(
                    new Toast(
                        "Geschafft!",
                        `Ihr Trainerprofil wurde erfolgreich aktualisiert.`,
                        "success",
                        faXmark,
                        5
                    )
                );
            } catch (error) {
                console.error("Error saving changes:", error);
                alert("Failed to save changes.");
            }

            this.router.push({
                name: "Profile",
                params: { username: Cookies.get("username") },
            });
        },

        updateTagStates() {
            this.filteredTags = this.allTags.map(tag => ({
                ...tag,
                selected: this.selectedTags.some(selected => selected.id === tag.id)
            }));
        },

        filterTags() {
            const search = this.tagInput.toLowerCase();
            this.filteredTags = this.allTags.filter(tag =>
                tag.name.toLowerCase().includes(search) &&
                !this.selectedTags.some(selected => selected.id === tag.id)
            );
        },

        handleKeyup(event) {
            if (event.key === ",") {
                this.addTagFromInput();
            }
        },

        addTagFromInput() {
            const trimmedInput = this.tagInput.trim();
            if (!trimmedInput) return;

            let tag = this.allTags.find(tag => tag.name.toLowerCase() === trimmedInput.toLowerCase());
            if (!tag) {
                tag = { id: Date.now(), name: trimmedInput, selected: false };
                this.allTags.push(tag);
            }

            if (!this.selectedTags.some(selected => selected.id === tag.id) && this.selectedTags.length < 5) {
                this.selectedTags.push(tag);
                this.updateTagStates();
            }
            this.tagInput = "";
            this.filterTags();
        },

        addTag(tag) {
            if (!this.selectedTags.some(selected => selected.id === tag.id) && this.selectedTags.length < 5) {
                this.selectedTags.push(tag);
                this.updateTagStates();
            }
            this.tagInput = "";
        },

        removeTag(tagId) {
            this.selectedTags = this.selectedTags.filter(tag => tag.id !== tagId);
            this.updateTagStates();
        },

        setActiveEvent(event) {
            this.activeEvent = this.activeEvent?.id === event.id ? null : event;
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
    position: absolute;
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

.trainer-profile-manage-container {
    height: calc(100vh - 0px);
    overflow-y: auto;
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
    font-size: 3rem;
    font-weight: 600;
    color: #01172F;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-bottom: 0.8rem;
}

.section-header {
    font-size: 2rem;
    font-weight: 600;
    color: #01172F;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-bottom: 1.5rem;
    position: relative;
    margin-top: 3rem;
}

.section-header::after {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 0;
    width: 100%;
    height: 3px;
    background-color: #009EE2;
    border-radius: 2px;
}

.section-description {
    font-size: 1.1rem;
    font-weight: 400;
    color: #354F52;
    line-height: 1.6;
    text-align: justify;
    margin-top: 1.2rem;
}

.alt-description {
    font-size: 1.1rem;
    font-weight: 400;
    color: #354F52;
    line-height: 1.6;
    text-align: center;
}

.section-description strong {
    font-weight: 600;
    color: #009EE2;
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

.events-row {
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;
    margin-bottom: 2rem;
}

.event-button {
    flex: 1 0 auto;
    min-width: 160px;
    max-width: 240px;
    padding: 0.75rem 1.5rem;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: white;
    cursor: pointer;
    transition: all 0.2s ease;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.event-button:hover {
    background: #f8f9fa;
    transform: translateY(-2px);
}

.event-button.active {
    background: #007bff;
    color: white;
    border-color: #007bff;
}

.comments-section {
    border-top: 2px solid #eee;
    padding-top: 2rem;
    margin-top: 1rem;
}

.comments-section h3 {
    margin-bottom: 1.5rem;
    color: #333;
}

.comments-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
    max-height: 600px;
    overflow-y: auto;
    padding: 1rem;
}

.pinned-comment-card {
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
    .comments-grid {
        grid-template-columns: 1fr;
    }

    .event-button {
        min-width: 120px;
        padding: 0.5rem 1rem;
    }
}

@media (min-width: 1200px) {
    .comments-grid {
        grid-template-columns: repeat(2, 1fr);
    }
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

.input-group {
    margin-bottom: 1rem;
}

.input-group input {
    padding: 0.5rem;
    margin-right: 1rem;
}

.tag-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    max-width: 750px;
    margin: 0 auto;
}

.tag-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
}

.tag-chips {
    display: flex;
    flex-wrap: wrap;
    margin: 1rem;
    margin-bottom: 0px;
}

.chip {
    background-color: #009ee2;
    color: white;
    padding-top: 0.3rem;
    padding-left: 0.7rem;
    border-radius: 20px;
    margin-right: 0.5rem;
}

.impostor-button {
    visibility: hidden;
}

.remove-tag {
    background: transparent;
    border: none;
    color: white;
    font-size: 1.2rem;
    cursor: pointer;
    margin-left: 0.5rem;
}

.tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    margin-top: 1rem;
}

.tag-list button {
    background-color: white;
    color: #009ee2;
    padding: 0.3rem 0.7rem;
    border: 2px solid #009ee2;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.tag-list button:hover {
    background-color: #009ee2;
    color: white;
}

.tag-list button:disabled {
    background-color: #f0f0f0;
    color: #d3d3d3;
    border: 2px solid #d3d3d3;
}

.icon-tag {
  width: 0.75rem;
  height: 0.75rem;
  color: currentColor;
  margin-right: 0.25rem;
}

.tag-input {
    width: 70%;
    max-width: 600px;
    min-height: 50px;
    max-height: 50px;
    padding: 10px;
    border-radius: 20px;
    border: 2px solid #009ee2;
    font-size: 16px;
    line-height: 1.5;
    transition: border-color 0.3s;
}

.tag-input:focus {
    border-color: #007bb5;
    outline: none;
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

.save-button {
    background-color: #4caf50;
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 1rem;
    margin: 50px 0;
}

.save-button:hover {
    background-color: #3e8e41;
}
</style>