<template>
  <NotificationCardBase :notification="notification" @mark-as-read="markAsRead">
    <template #message>
      <p>
        {{ notification.context.responderName }} has replied in
        <span class="notification-title" @click="navigateToForumPost">
          "{{ notification.title }}"
        </span>
        .
      </p>
    </template>
  </NotificationCardBase>
</template>

<script>
import NotificationCardBase from "./NotificationCardBase.vue";
import { useRouter } from "vue-router";

export default {
  components: { NotificationCardBase },
  props: { notification: { type: Object, required: true } },
  setup(props) {
    const router = useRouter();

    const navigateToForumPost = () => {
      router.push({
        name: "EventPage",
        params: { eventId: props.notification.context.eventId },
        query: { threadId: props.notification.context.threadId },
      });
    };

    return { navigateToForumPost };
  },
};
</script>
