<template>
  <div v-if="averageRating >= 1">
    <div
      class="trainer-star-rating"
      :aria-label="'Rating: ' + averageRating + ' out of ' + maxRating"
    >
      <span
        v-for="n in maxRating"
        :key="n"
        class="star"
        :class="getStarClass(n)"
      >
        ★
      </span>
    </div>
  </div>
  <div v-else>
    <p>Noch keine Bewertungen erhalten...</p>
  </div>
</template>

<script>
export default {
  name: "TrainerStarRating",
  props: {
    averageRating: {
      type: Number,
      required: true,
      validator(value) {
        return value >= 0;
      },
    },
    maxRating: {
      type: Number,
      default: 5,
    },
  },
  methods: {
    getStarClass(index) {
      console.log(this.averageRating);
      const ratingFloor = Math.floor(this.averageRating);
      const fractionalPart = this.averageRating - ratingFloor;

      if (index <= ratingFloor) {
        return "filled";
      } else if (index === ratingFloor + 1 && fractionalPart > 0) {
        return "half-filled";
      } else {
        return "empty";
      }
    },
  },
};
</script>

<style scoped>
.trainer-star-rating {
  display: flex;
  align-items: center;
  font-size: 1.5rem;
  color: #ffc107;
}

.star {
  margin-right: 0.2rem;
  position: relative;
}

.star.filled {
  color: #ffc107;
}

.star.half-filled {
  color: #ffc107;
  background: linear-gradient(90deg, #ffc107 50%, #e0e0e0 50%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.star.empty {
  color: #e0e0e0;
}
</style>
