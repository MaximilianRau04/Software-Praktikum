<template>
  <div class="trainer-tags" v-if="tags">
    <span v-for="(tag, index) in tags" :key="index" class="chip" :style="{ backgroundColor: getRandomColor() }">
      <TagIcon class="icon-tag" />
      {{ tag.name }}
    </span>
  </div>
  <div v-else>
    <p>Lade Expertise-Tags...</p>
  </div>
</template>

<script>
import { TagIcon } from "@heroicons/vue/24/outline";

export default {
  name: "TrainerTags",
  components: { TagIcon },
  props: {
    tags: {
      type: Array,
      required: true,
      validator(value) {
        return value.every((tag) => typeof tag.name === "string");
      },
    },
  },
  methods: {
    getColorPalette() {
      return ["#01172F", "#003C5E", "#526760", "#009EE2", "#1A73E8", "#354F52"];
    },
    getRandomColor() {
      const palette = this.getColorPalette();
      const randomIndex = Math.floor(Math.random() * palette.length);
      return palette[randomIndex];
    },
  },
};
</script>

<style scoped>
.trainer-tags {
  margin-top: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.chip {
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 16px;
  font-size: 0.875rem;
  font-weight: bold;
  color: white;
  cursor: pointer;
  user-select: none;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.chip:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.icon-tag {
  color: white;
}
</style>
