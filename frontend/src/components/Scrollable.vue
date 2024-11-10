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
import config from "../config";

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


<style scoped>
.scroll-container {
  max-height: 100vh; /* Der Container nutzt die gesamte Höhe des Viewports */
  overflow-y: auto; /* Aktiviert Scrollen, wenn der Inhalt größer ist als der verfügbare Platz */
  box-sizing: border-box; /* Padding und Border werden in der Höhe berücksichtigt */
  padding: 10px; /* Optional: Innenabstand für die Scrollleiste */
  height: 85vh;
}

.exchangeDay-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.list-item {
  padding: 10px;
  border: 2px solid black;
  border-radius: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.list-item:hover {
  transform: scale(0.95);
}

.header {
  display: flex;
  align-items: center;
  width: 100%;
}

.date-box {
  background-color: #333;
  color: white;
  font-size: 18px;
  font-weight: bold;
  padding: 5px 10px; 
  margin-right: 20px;
  border-radius: 5px;
  text-align: center;
  height: 40px;
  display: flex;
  align-items: center; 
  justify-content: center;
}

.infos {
  flex: 1;
}

.infos h2 {
  margin: 0;
  font-size: 20px;
}

.infos p {
  margin: 5px 0;
  font-size: 14px;
}

</style>
