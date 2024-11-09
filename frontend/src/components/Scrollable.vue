<template>
    <div class="scroll-container">
      <div class="workshop-list">
        <div v-for="(workshop, index) in workshops" :key="index" class="list-item">
          <div class="header">
            <div class="profile-image">
              <img :src="workshop.image" alt="Profile Image">
            </div>
              <div class="infos">
              <h2>{{ workshop.name }}</h2>
              <p>{{ workshop.location }}</p>
              <p>{{ workshop.date }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
export default {
  name: 'ScrollableDivs',
  data() {
    return {
      workshops: [
        {
          name: 'Exchange Day1',
          image: new URL('@/assets/itestra.jpg', import.meta.url).href,
          location: 'Ort 1',
          date: 'Datum 1'
        },
        {
          name: 'Exchange Day1',
          image: new URL('@/assets/itestra.jpg', import.meta.url).href,
          location: 'Ort 2',
          date: 'Datum 2'
        },
        {
          name: 'GitHub',
          image: new URL('@/assets/itestra.jpg', import.meta.url).href,
          location: 'Ort 1',
          date: 'Datum 1'
        },
        {
          name: 'GitHub2',
          image: new URL('@/assets/itestra.jpg', import.meta.url).href,
          location: 'Ort 1',
          date: 'Datum 1'
        },
        {
          name: 'GitHub2',
          image: new URL('@/assets/itestra.jpg', import.meta.url).href,
          location: 'Ort 1',
          date: 'Datum 1'
        }
      ]
    };
  }
};
</script>
