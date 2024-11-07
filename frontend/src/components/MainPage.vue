<template>
  <div class="container">
      <div class="leftSide" v-if="isVisible">
          <Upcoming :isVisible="isVisible" @toggle-visibility="toggleVisibility" />
      </div>
      <div class="leftSide" v-else="isVisible">
          <Detail :isVisible="isVisible" @toggle-visibility="toggleVisibility" />
      </div>
      <div class="rightSide">
          <ScrollableDivs :items="workshops" />
      </div>
  </div>
</template>

<script>
import Upcoming from './Upcoming.vue';
import Detail from './Detail.vue';
import ScrollableDivs from './Scrollable.vue';

export default {
  name: 'MainPage',
  components: {
      Upcoming,
      ScrollableDivs,
      Detail
  },
  data() {
      return {
          isVisible: true
      };
  },
  methods: {
      toggleVisibility() {
          this.isVisible = !this.isVisible;
      }
  }
};
</script>
