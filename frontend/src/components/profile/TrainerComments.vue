<template>
    <div>
        <h2>Erhaltene Kommentare aus vergangenen Veranstaltungen</h2>
        <div v-if="pinnedComments.length">
            <div 
                v-for="pinned in pinnedComments" 
                :key="pinned.id" 
                class="comment-card"
            >
                <div class="card-header">
                    <span class="author">{{ pinned.author }}</span>
                    <span class="rating">
                        ⭐ {{ pinned.rating.toFixed(1) }}
                    </span>
                </div>
                <div class="card-body">
                    <p class="comment">{{ pinned.comment }}</p>
                </div>
            </div>
        </div>
        <p v-else>Noch keine Kommentare erhalten...</p>
    </div>
</template>

<script>
import axios from "axios";
import config from "@/config";

export default {
    data() {
        return {
            pinnedComments: [],
        };
    },
    props: {
        trainerId: {
            type: Number,
            required: true,
        },
    },
    created() {
        axios
            .get(`${config.apiBaseUrl}/pinned-comments/${this.trainerId}`)
            .then((response) => (this.pinnedComments = response.data))
            .catch((error) => console.error("Error fetching pinned comments:", error));
    },
};
</script>

<style scoped>
.comment-card {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 10px;
    padding: 1rem;
    margin-bottom: 1rem;
    background-color: #f9f9f9;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 1rem;
    font-weight: bold;
    color: #333;
}

.author {
    color: #009ee2;
}

.rating {
    color: #ff9800;
}

.card-body {
    font-size: 0.9rem;
    color: #555;
}

.comment {
    margin: 0;
    word-wrap: break-word;
    line-height: 1.5;
}
</style>
