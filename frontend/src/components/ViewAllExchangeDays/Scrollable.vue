<!--
This Vue component is responsible for displaying a scrollable list of other Exchange Days.
Users can select a date to filter the list of Exchange Days based on the selected date.
-->
<template>
  <div class="scroll-container">
    <!-- Date filter input to select a date -->
    <div class="date-filter">
      <input type="date" id="date-picker" v-model="selectedDate" @change="filterExchangeDays" />
    </div>
    
    <!-- List of Exchange Days that gets filtered based on the selected date -->
    <div class="exchangeDay-list">
      <div v-for="(exchangeDay, index) in filteredExchangeDays" :key="index" @click="selectExchangeDay(exchangeDay)" class="list-item">
        <div class="header">
          <div class="date-box">
            <div><p>{{ formatDate(exchangeDay.date) }}</p></div>
          </div>
          <!-- Exchange Day details -->
          <div class="infos">
            <h2>{{ exchangeDay.name }}</h2>
            <p>{{ exchangeDay.location }}</p>
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
    const filteredExchangeDays = ref([]);
    const selectedDate = ref('');

    /**
     * Fetches all Exchange Days from the API and stores them in the `exchangeDays` ref.
     */
    function fetchExchangeDays() {
      fetch(`${config.apiBaseUrl}/exchange-days`)
        .then(response => response.json())
        .then(data => {
          exchangeDays.value = data.map(item => ({
            name: item.name,
            location: item.location,
           
            date: item.date,
            description: item.description,
            id: item.id
          }));

          filteredExchangeDays.value = exchangeDays.value;
        })
        .catch(error => console.error("Error fetching exchange days:", error));
    }

    /**
     * Emits the selected Exchange Day to the parent component.
     * @param {Object} workshop - The selected workshop
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

    /**
     * Filters the exchange days based on the selected date.
     */
    function filterExchangeDays() {
      if (!selectedDate.value) {
        filteredExchangeDays.value = exchangeDays.value;
        return;
      }
      const filterDate = new Date(selectedDate.value).setHours(0, 0, 0, 0);
      filteredExchangeDays.value = exchangeDays.value.filter(day => {
        const dayDate = new Date(day.date).setHours(0, 0, 0, 0);
        return dayDate === filterDate;
      });
    }

    onMounted(() => fetchExchangeDays());

    return {
      exchangeDays,
      filteredExchangeDays,
      selectedDate,
      formatDate,
      selectExchangeDay,
      filterExchangeDays
    };
  }
};
</script>