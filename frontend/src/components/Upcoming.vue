<template>
    <div class="workshop-list">
      <div v-for="(workshop, index) in workshops" :key="index" class="workshop">
        <div class="header">
          <div class="profile-image">
            <img :src="workshop.image" alt="Profile Image">
          </div>
          <div class="infos">
            <h2>{{ workshop.name }}</h2>
            <p>{{ workshop.location }}</p>
            <p>{{ workshop.date }}</p>
          </div>
          <button class="details" @click="showAlert">See Details</button>
        </div>
        <div class="description-box">
          <div class="label">Beschreibung:</div>
          <p>{{ workshop.description }}</p>
        </div>
      </div>
  </div>
</template>


<script>
import ScrollableDivs from './Scrollable.vue';

export default {
  name: 'Upcoming',
  components: {
    ScrollableDivs
  },
  inject: ['isVisible'],
  data() {
    return {
      workshop: {
        name: 'Git Einführung',
        image: new URL('@/assets/itestra.jpg', import.meta.url).href,
        location: 'Stuttgart',
        date: '20.12.2021',
        description: 'Eine Einführung zu Git von Itestra an der Uni Stuttgart'
      },
      workshops: [
        {
          name: 'Exchange Day1',
          image: new URL('@/assets/itestra.jpg', import.meta.url).href,
          location: 'Ort 1',
          date: 'Datum 1',
          description: 'Beschreibung 1'
        },
        {
          name: 'Exchange Day2',
          image: new URL('@/assets/itestra.jpg', import.meta.url).href,
          location: 'Ort 2',
          date: 'Datum 2',
          description: 'Beschreibung 2'
        }
      ]
    };
  },
  methods: {
    showAlert() {
      this.$emit('toggle-visibility');
    },
  }
};
</script>
