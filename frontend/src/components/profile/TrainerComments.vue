<template>
  <div>
    <h2>Erhaltene Kommentare aus vergangenen Veranstaltungen</h2>
    <div v-if="pinnedComments.length" class="comments-grid">
      <div
        v-for="pinned in pinnedComments"
        :key="pinned.id"
        class="comment-card"
      >
        <div class="card-header">
          <span class="author">{{ pinned.author }}</span>
          <span class="rating"> ⭐ {{ pinned.rating.toFixed(1) }} </span>
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
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

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
  mounted() {
    axios
      .get(
        `${config.apiBaseUrl}/trainerProfiles/${this.trainerId}/pinned-comments`,
      )
      .then((response) => (this.pinnedComments = response.data))
      .catch((error) =>
        showToast(
          new Toast(
            "Error",
            `Fehler beim Laden der kommentare`,
            "error",
            faXmark,
            10,
          ),
        ),
      );
  },
};
</script>

<style scoped>
h2 {
  margin-top: 0;
}

.comments-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  padding: 1rem;
}

.comment-card {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 1rem;
  background-color: #ffffff;
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
