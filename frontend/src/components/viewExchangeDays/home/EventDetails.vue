<template>
  <div class="event-details">
    <div class="trainer-avatar" @click="handleTrainerClick" v-if="organizer">
      <div class="avatar-placeholder">
        <UserCircleIcon class="w-6 h-6 text-white" />
      </div>
    </div>
    
    <div class="event-header-home">
      <h2>{{ event.name }}</h2>
      <div class="meta-grid">
        <div class="meta-item">
          <CalendarIcon class="icon-small" />
          <span class="value">{{ formatDate(event.date) }}</span>
        </div>
        <div class="meta-item">
          <ClockIcon class="icon-small" />
          <span class="value">{{ event.startTime || "-" }} – {{ event.endTime || "-" }}</span>
        </div>
        <div class="meta-item">
          <MapPinIcon class="icon-small" />
          <span class="value">{{ event.room || "-" }}</span>
        </div>
      </div>
    </div>

    <div class="event-tags" v-if="tags?.length">
      <span class="tag" v-for="tag in tags" :key="tag.id">
        <TagIcon class="icon-tag" />
        {{ tag.name }}
      </span>
    </div>

    <button @click="goToEvent(event.id)" class="register-button">
      <ArrowRightCircleIcon class="icon-button" />
      Details
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import "@/assets/event-details.css";
import { Event } from "@/types/Event";
import { defineProps } from "vue";
import { 
  CalendarIcon,
  ClockIcon,
  MapPinIcon,
  DocumentTextIcon,
  TagIcon,
  ArrowRightCircleIcon,
  UserCircleIcon
} from "@heroicons/vue/24/outline";
import { User } from "@/types/User";

const router = useRouter();
const props = defineProps<{ event: Event }>();
const isPastEvent = ref(false);
const tags = ref([]);
const organizer = ref<User | null>(null);
const goToEvent = (eventId) => {
  router.push({ name: "EventPage", params: { eventId } });
};

/**
 * Formats a timestamp into a human-readable date string.
 *
 * @param {string} timestamp - The date in milliseconds.
 * @returns {string} - The formatted date string in 'DD.MM.YYYY' format.
 */
function formatDate(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString("de-DE");
}

/**
 * Checks if the event is in the past.
 */
function checkIfPastEvent() {
  const eventDate = new Date(props.event.date);
  const now = new Date();
  const currentTime = now.getTime();
  isPastEvent.value = eventDate <= now && new Date(`${props.event.date}T${props.event.startTime}`).getTime() <= currentTime;
}

async function fetchOrganizer() {
  try {
    const response = await fetch(`/api/events/${props.event.id}/organizer`);
    organizer.value = await response.json();
  } catch (error) {
    console.error("Error fetching organizer:", error);
  }
}

async function fetchTags() {
  try {
    const response = await fetch(`/api/events/${props.event.id}/tags`);
    tags.value = await response.json();
  } catch (error) {
    console.error("Error fetching organizer:", error);
  }
}

const handleTrainerClick = ()=>{
  router.push({ name: "Profile", params: { username: organizer.value.username } });
}

onMounted(() => {
  checkIfPastEvent();
  fetchOrganizer();
  fetchTags();
});
</script>
