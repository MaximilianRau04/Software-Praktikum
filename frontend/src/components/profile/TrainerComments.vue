<template>
  <div class="trainer-comments">
    <h3>Trainer Feedback</h3>
    <ul>
      <li
        v-for="(comment, index) in limitedComments"
        :key="index"
        class="comment-item"
      >
        <div class="comment-header">
          <span class="author">{{ comment.author }}</span>
          <span class="rating">Rating: {{ comment.rating }}</span>
        </div>
        <p class="comment-text">{{ comment.comment }}</p>
      </li>
    </ul>
    <p v-if="comments.length === 0" class="no-comments">
      Es wurden noch keine Kommentare abgegeben.
    </p>
  </div>
</template>

<script>
export default {
  name: "TrainerComments",
  props: {
    comments: {
      type: Array,
      required: true,
      validator(value) {
        return value.every(
          (comment) =>
            typeof comment.author === "string" &&
            typeof comment.comment === "string" &&
            typeof comment.rating === "number",
        );
      },
    },
  },
  computed: {
    limitedComments() {
      return this.comments.slice(0, 5);
    },
  },
};
</script>

<style scoped>
.trainer-comments {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.trainer-comments h3 {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
}

.comment-item {
  padding: 0.5rem 0;
  border-bottom: 1px solid #e0e0e0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  margin-bottom: 0.25rem;
}

.comment-text {
  margin: 0;
  font-size: 0.9rem;
  color: #333;
}

.rating {
  color: #ffc107;
}

.no-comments {
  font-style: italic;
  color: #888;
  text-align: center;
}
</style>
