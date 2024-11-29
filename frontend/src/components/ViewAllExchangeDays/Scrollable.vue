<!-- 
 This component displays a scrollable list of Exchange Days. 
  Emits `select-exchange-day` when an item is clicked.
-->
<template>
  <div class="scroll-container">
      <!-- Container for the list of Exchange Days -->
    <div class="exchangeDay-list">
        <!-- Loop through exchangeDays array and render each Exchange Day -->
      <div v-for="(exchangeDay, index) in exchangeDays" :key="index" @click="selectExchangeDay(exchangeDay)" class="list-item">
        <div class="header">
          <div class="date-box">
            <div><p>{{ formatDate(exchangeDay.date) }}</p></div>
          </div>
            <!-- Exchange Day details -->
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

    /**
     * Fetches all Exchange Days from the API and stores them in the `exchangeDays` ref.
     */
    function fetchExchangeDays() {
      fetch(`${config.apiBaseUrl}/exchange-days`)
        .then(response => response.json())
        .then(data => {
          console.log(data);
          // Populate exchangeDays with data from the API
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

    /**
     * Emits the selected Exchange Day to the parent component.
     * @param {Object} exchangeDay - The selected Exchange Day.
     */
    function selectExchangeDay(workshop) {
      emit('select-exchange-day', workshop); 
    }
    /**
     * Formats a timestamp into "DD.MM.YYYY" format.
     * @param {number} timestamp - The timestamp to format.
     * @returns {string} The formatted date string.
     */
    function formatDate(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleDateString("de-DE");
    }

    function splitDate(dateString) {
      return dateString.split('.').map(Number);
    }

   // Fetch data when the component is mounted
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


