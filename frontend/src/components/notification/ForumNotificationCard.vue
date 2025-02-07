<template>
  <NotificationCardBase :notification="notification" @mark-as-read="markAsRead">
    <template #message>
      <p>
        {{ notification.context.responderName }} hat geantwortet in
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
  setup(props, { emit }) {
    const router = useRouter();

    const navigateToForumPost = () => {
      emit("closeNoti", props.notification);
      emit("mark-as-read", props.notification.id);
    };

    return { navigateToForumPost };
  },
};
</script>
