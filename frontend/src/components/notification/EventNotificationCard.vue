<template>
  <NotificationCardBase :notification="notification" @mark-as-read="markAsRead">
    <template #message>
      <p>
        Dein Workshop
        <span class="notification-title" @click="navigateToEvent">
          "{{ notification.title }}"
        </span>
        findet statt {{ timeUntilEvent }}
      </p>
    </template>
  </NotificationCardBase>
</template>

<script>
import { computed } from "vue";
import NotificationCardBase from "./NotificationCardBase.vue";
import { formatDistanceToNow, parseISO } from "date-fns";
import { useRouter } from "vue-router";
import { de } from "date-fns/locale";

export default {
  components: { NotificationCardBase },
  props: { notification: { type: Object, required: true } },
  setup(props, { emit }) {
    const router = useRouter();

    const timeUntilEvent = computed(() =>
      formatDistanceToNow(parseISO(props.notification.context.eventDateTime), {
        addSuffix: true,
        locale: de,
      }),
    );
    const navigateToEvent = () => {
      emit('closeNoti', props.notification);
      emit('mark-as-read', props.notification.id);
    };

    return { timeUntilEvent, navigateToEvent };
  },
};
</script>
