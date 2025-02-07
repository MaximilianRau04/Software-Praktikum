<script setup lang="ts">
import { useAuth } from "@/util/auth";

const props = defineProps({
  role: {
    type: String,
    required: true,
  },
});

const { hasRole } = useAuth();
</script>

<template>
  <div>
    <slot v-if="hasRole(role)" />
    <template v-else> </template>
  </div>
</template>
