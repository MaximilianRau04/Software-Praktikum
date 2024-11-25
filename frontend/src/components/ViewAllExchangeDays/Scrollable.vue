<template>
  <div class="scroll-container">
    <div class="exchangeDay-list">
      <div v-for="(exchangeDay, index) in exchangeDays" :key="index" @click="selectExchangeDay(exchangeDay)" class="list-item">
        <div class="header">
          <div class="date-box">
            <div><p>{{ formatDate(exchangeDay.date) }}</p></div>
          </div>
          <div class="infos">
            <h2>{{ exchangeDay.name }}</h2>
            <p>{{ exchangeDay.location }}</p>
            <p>{{ exchangeDay.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineProps, defineEmits, onMounted, ref } from "vue";
import config from "../../config";
import '../../assets/scrollable.css';

export default {
  name: "ScrollableDivs",
  setup(props, { emit }) {
    const exchangeDays = ref([]);
    const defaultImage = new URL('@/assets/itestra.jpg', import.meta.url).href;

    function fetchExchangeDays() {
      fetch(`${config.apiBaseUrl}/exchange-days`)
        .then(response => response.json())
        .then(data => {
          console.log(data);
          exchangeDays.value = data.map(item => ({
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

    function selectExchangeDay(workshop) {
      emit('select-exchange-day', workshop); 
    }

    function formatDate(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleDateString("de-DE");
    }

    function splitDate(dateString) {
      return dateString.split('.').map(Number);
    }

    onMounted(() => fetchExchangeDays());

    return {
      exchangeDays,
      defaultImage,
      formatDate,
      splitDate,
      selectExchangeDay  
    };
  }
};
</script>


