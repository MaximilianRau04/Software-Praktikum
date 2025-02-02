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
          <span class="value">
            {{ event.startTime ? event.startTime.split(':').slice(0, 2).join(':') : '-' }} –
            {{ event.endTime ? event.endTime.split(':').slice(0, 2).join(':') : '-' }}
          </span>
        </div>
        <div class="meta-item">
          <MapPinIcon class="icon-small" />
          <span class="value">{{ event.room.name || "-" }}</span>
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

import { showToast, Toast } from "@/types/toasts";
import { faXmark } from "@fortawesome/free-solid-svg-icons";

import {
  CalendarIcon,
  ClockIcon,
  MapPinIcon,
  TagIcon,
  ArrowRightCircleIcon,
  UserCircleIcon
} from "@heroicons/vue/24/outline";
import { User } from "@/types/User";
import api from "@/util/api";

const router = useRouter();
const props = defineProps<{ event: Event }>();
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
 * Fetches the organizer of the event.
 */
async function fetchOrganizer() {
  try {
    const response = await api.get(`/events/${props.event.id}/organizer`);
    organizer.value = await response.data;
  } catch (error) {
      showToast(
        new Toast(
          "Fehler",
          `Veranstalter/in für ${props.event.name} konnte nicht geladen werden.`,
          "error",
          faXmark,
          5
        )
      );
  }
}

/**
 * Fetches the tags of the event.
 */
async function fetchTags() {
  try {
    const response = await api.get(`/events/${props.event.id}/tags`);
    tags.value = await response.data;
  } catch (error) {
    showToast(
        new Toast(
          "Fehler",
          `Event-Tags für ${props.event.name} konnten nicht geladen werden.`,
          "error",
          faXmark,
          5
        )
      );
  }
}

const handleTrainerClick = () => {
  router.push({ name: "Profile", params: { username: organizer.value.username } });
}

onMounted(() => {
  fetchOrganizer();
  fetchTags();
});
</script>
