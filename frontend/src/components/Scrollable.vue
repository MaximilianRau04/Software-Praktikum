<template>
  <div class="scroll-container">
    <div class="workshop-list">
      <div v-for="(workshop, index) in workshops" :key="index" @click="selectWorkshop(workshop)" class="list-item">
        <div class="header">
          <div class="profile-image">
            <img :src="workshop.image || defaultImage" alt="Profile Image">
          </div>
          <div class="infos">
            <h2>{{ workshop.name }}</h2>
            <p>{{ workshop.location }}</p>
            <p>{{ formatDate(workshop.date) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineProps, defineEmits, onMounted, ref } from "vue";
import config from "../config";

export default {
  name: "ScrollableDivs",
  setup(props, { emit }) {
    const workshops = ref([]);
    const defaultImage = new URL('@/assets/itestra.jpg', import.meta.url).href;

    function fetchExchangeDays() {
      fetch(`${config.apiBaseUrl}/exchange-days`)
        .then(response => response.json())
        .then(data => {
          console.log(data);
          workshops.value = data.map(item => ({
            name: item.name,
            image: defaultImage,
            location: item.location,
            date: item.date,
            description: item.description,
            id: item.id
          }));
        })
        .catch(error => console.error("Error fetching exchange days:", error));
    }

    function selectWorkshop(workshop) {
      emit('select-exchange-day', workshop); 
    }

    function formatDate(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleDateString("de-DE");
    }

    onMounted(() => fetchExchangeDays());

    return {
      workshops,
      defaultImage,
      formatDate,
      selectWorkshop  
    };
  }
};
</script>
