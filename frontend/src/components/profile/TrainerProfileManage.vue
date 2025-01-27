<template>
    <div>
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
                        die Leiste rein und bestätigen es mit einem Komma.</p>
                    <div>

                        <!-- Input field for tags -->
                        <input type="text" v-model="tagInput" placeholder="Tags eingeben und durch Komma trennen"
                            @input="filterTags" @keyup="handleKeyup" :disabled="selectedTags.length >= 5" />

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
                            <button v-for="tag in filteredTags" :key="tag" type="button" @click="addTag(tag)"
                                :disabled="selectedTags.includes(tag)">
                                {{ tag }}
                            </button>
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
                <div v-for="event in events" :key="event.id" class="event-section">
                    <button @click="toggleDropdown(event.id)" class="event-toggle" :ref="'eventButton-' + event.id">
                        {{ event.name }}
                    </button>
                    <div v-if="activeEvent === event.id" class="dropdown" :ref="'dropdown-' + event.id">
                        <div class="comments-grid">
                            <div v-for="comment in event.comments" :key="comment.id" class="comment-card">
                                <div class="card-header">
                                    <span class="author">{{ comment.author }}</span>
                                    <span class="rating">⭐ {{ comment.rating.toFixed(1) }}</span>
                                </div>
                                <div class="card-body">
                                    <p>{{ comment.comment }}</p>
                                    <button @click="pinComment(comment, event.id)" class="pin-button"
                                        :class="{ 'disabled': pinnedComments.length >= 6 }"
                                        :disabled="pinnedComments.length >= 6">Pin</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

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
                this.selectedTags = trainerTagsResponse.data.map(tag => tag.name);

                const eventsResponse = await axios.get(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/comments-by-event`);
                this.events = eventsResponse.data.events;

                const commentsResponse = await axios.get(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/pinned-comments`);
                this.pinnedComments = commentsResponse.data;

                const pinnedCommentIds = new Set(this.pinnedComments.map(comment => comment.id));

                this.events.forEach(event => {
                    event.comments = event.comments.filter(comment => !pinnedCommentIds.has(comment.id));
                });

                const tagsResponse = await axios.get(`${config.apiBaseUrl}/tags`);
                this.allTags = tagsResponse.data.map(tag => tag.name);
                this.filteredTags = this.allTags.filter(tag =>
                    !this.selectedTags.some(selectedTag => selectedTag === tag)
                );
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        },

        toggleDropdown(eventId) {
            this.activeEvent = this.activeEvent === eventId ? null : eventId;

            this.$nextTick(() => {
                const eventButton = this.$refs[`eventButton-${eventId}`][0];
                const dropdown = this.$refs[`dropdown-${eventId}`][0];

                if (eventButton && dropdown) {
                    dropdown.scrollIntoView({
                        behavior: 'smooth',
                        block: 'center',
                        inline: 'center'
                    });
                }
            });
        },

        pinComment(comment, eventId) {
            if (this.pinnedComments.length >= 6) {
                alert("You can only pin up to 6 comments.");
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
                if (response.status === 200) {
                    console.log("Änderungen wurden erfolgreich gespeichert.");
                }

                const pinnedIds = this.pinnedComments.map(comment => comment.id);
                await axios.post(`${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/pinned-comments`, pinnedIds);
                alert("Changes saved successfully!");
            } catch (error) {
                console.error("Error saving changes:", error);
                alert("Failed to save changes.");
            }

            this.router.push({
                name: "Profile",
                params: { username: Cookies.get("username") },
            });
        },

        handleKeyup(event) {
            if (event.key === ",") {
                this.addTagFromInput();
            }
        },

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
            this.filteredTags = this.allTags.filter((tag) =>
                !this.selectedTags.some(selectedTag => selectedTag.id === tag.id)
            );
        },

        filterTags() {
            const query = this.tagInput.toLowerCase();
            this.filteredTags = this.allTags
                .filter((tagName) => tagName.toLowerCase().includes(query));
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