<template>
  <NotificationCardBase :notification="notification" @mark-as-read="markAsRead">
    <template #message>
      <p>
        <span class="notification-title" @click="navigateToProfile">
          Sie haben die Errungenschaft <img v-if="rewardPng" :src="rewardPng" alt="Badge Image"
            class="badge-image" /><strong>{{ notification.title }}</strong> 
            erreicht. Klicken Sie hier, um sich die Errungenschaft auf Ihrem Profil anzuschauen.
        </span>
      </p>
    </template>
  </NotificationCardBase>
</template>

<script>
import { ref, onMounted, watch } from "vue";
import NotificationCardBase from "./NotificationCardBase.vue";
import { useRouter } from "vue-router";
import Cookies from "js-cookie";
import config from "@/config";

export default {
  components: { NotificationCardBase },
  props: { notification: { type: Object, required: true } },
  setup(props, { emit }) {
    const router = useRouter();
    const rewardPng = ref(null);

    const navigateToProfile = () => {
      emit('closeNoti', props.notification);
      emit('mark-as-read', props.notification.id);
    };

    const fetchBadgePng = async () => {
      if (!props.notification.context) {
        console.error("Notification context is missing!");
        return;
      }

      try {
        const badgeImageResponse = await fetch(
          `${config.apiBaseUrl}/rewards/badge?type=${props.notification.context.rewardType}&currentLevel=${props.notification.context.currentLevel}`
        );

        if (!badgeImageResponse.ok) throw new Error("Badge image fetch failed");

        const badgeImageBlob = await badgeImageResponse.blob();
        rewardPng.value = URL.createObjectURL(badgeImageBlob);
      } catch (err) {
        console.error("Error fetching badge image:", err);
      }
    };

    onMounted(fetchBadgePng);

    // Watch for prop changes (in case new notifications are loaded)
    watch(() => props.notification, fetchBadgePng, { deep: true });

    return { rewardPng, navigateToProfile };
  }
};
</script>

<style scoped>
.badge-image {
  width: 50px;
  height: 50px;
  object-fit: contain;
  margin-right: 8px;
  vertical-align: middle;
}
</style>