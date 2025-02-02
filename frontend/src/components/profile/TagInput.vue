<template>
    <div class="tag-container">
        <div class="tag-wrapper">
            <!-- Input field for tags -->
            <input v-if="!tagSelect" type="text" v-model="tagInput"
                placeholder="Tag schreiben und mit Eingabe bestätigen" @input="filterTags"
                @keyup.enter="addTagFromInput" :disabled="modelValue.length >= 5" class="tag-input" />

            <!-- Display filtered tags to choose from -->
            <div class="tag-list">
                <button 
                    v-for="tag in filteredTags" 
                    :key="tag.id" 
                    type="button" 
                    @click="addTag(tag)"
                    :disabled="isTagSelected(tag) || modelValue.length >= 5"
                    >
                    <TagIcon class="icon-tag" />
                    {{ tag.name }}
                </button>
            </div>

            <!-- Display selected tags -->
            <div class="tag-chips">
                <span v-for="(tag, index) in modelValue" :key="tag.id" class="chip">
                    {{ tag.name }}
                    <button type="button" class="remove-tag" @click="removeTag(tag.id)">
                        &times;
                    </button>
                </span>
                <span v-if="modelValue.length === 0" class="chip impostor-button">
                    Hallo
                    <button type="button" class="remove-tag">
                        &times;
                    </button>
                </span>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { TagIcon } from '@heroicons/vue/24/outline';
import { defineComponent } from 'vue';
import type { Tag } from '@/types/Tag';

export default defineComponent({
  components: {
    TagIcon
  },
  props: {
    modelValue: {
      type: Array as () => Tag[],
      required: true
    },
    availableTags: {
      type: Array as () => Tag[],
      required: true
    },
    tagSelect: {
      type: Boolean,
      required: true
    }
  },
  emits: ['update:modelValue', 'new-tag'],
  data() {
    return {
      tagInput: '',
      filteredTags: [] as Tag[]
    };
  },
  watch: {
    availableTags: {
      immediate: true,
      handler(newVal: Tag[]) {
        this.filterTags();
      }
    },
    modelValue: {
      deep: true,
      handler() {
        this.filterTags();
      }
    }
  },
  methods: {
    filterTags() {
      const search = this.tagInput.toLowerCase();
      this.filteredTags = this.availableTags.filter(tag =>
        tag.name.toLowerCase().includes(search)
      );
    },
    isTagSelected(tag: Tag) {
      return this.modelValue.some(selected => selected.id === tag.id);
    },
    addTagFromInput() {
      const trimmedInput = this.tagInput.trim();
      if (!trimmedInput) return;

      const existingTag = this.availableTags.find(t => 
        t.name.toLowerCase() === trimmedInput.toLowerCase()
      );

      if (existingTag) {
        this.addTag(existingTag);
      } else {
        const newTag = { id: Date.now().toString(), name: trimmedInput };
        this.$emit('new-tag', newTag);
        this.addTag(newTag);
      }
      this.tagInput = '';
    },
    addTag(tag: Tag) {
      if (!this.isTagSelected(tag) && this.modelValue.length < 5) {
        this.$emit('update:modelValue', [...this.modelValue, tag]);
      }
    },
    removeTag(tagId: string | number) {
      const newTags = this.modelValue.filter(tag => tag.id !== tagId);
      this.$emit('update:modelValue', newTags);
    }
  }
});
</script>

<style scoped>
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
    gap: 0.5rem;
    justify-content: center;
    margin: 1rem 0;
    padding: 0 1rem;
    width: 100%;
}

.chip {
    background-color: #3498db;
    color: white;
    padding: 0.3rem 0.7rem;
    border-radius: 20px;
    display: flex;
    align-items: center;
    gap: 0.5rem;
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
    padding: 0;
    line-height: 1;
}

.tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    margin-top: 1rem;
    justify-content: center;
    width: 100%;
    padding: 0 1rem;
}

.tag-list button {
    background-color: white;
    color: #3498db;
    padding: 0.3rem 0.7rem;
    border: 2px solid #3498db;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
}

.tag-list button:hover {
    background-color: #3498db;
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
    padding: 10px;
    border-radius: 20px;
    border: 2px solid #3498db;
    font-size: 16px;
    line-height: 1.5;
    transition: border-color 0.3s;
    text-align: center;
    margin: 0 auto;
}

.tag-input:focus {
    border-color: #2c3e50;
    outline: none;
}
</style>