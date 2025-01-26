<template>
  <div class="date-filter">
    <input
      type="date"
      id="date-picker"
      v-model="selectedDate"
      @change="filterExchangeDays"
    />
  </div>
  <div class="scroll-container">
    <!-- list of exchange days-->
    <div class="exchangeDay-list">
      <div
        v-for="(exchangeDay, index) in sortedExchangeDays"
        :key="index"
        @click="selectExchangeDay(exchangeDay)"
        class="list-item"
      >
        <div class="heading">
          <div class="date-box">
            <div>
              <p>{{ formatDate(exchangeDay.startDate) }}</p>
              <p>bis</p>
              <p>{{ formatDate(exchangeDay.endDate) }}</p>
            </div>
          </div>
          <!-- details of the exchange days -->
          <div class="infos">
            <h2>{{ exchangeDay.name }}</h2>
            <p>
              {{ exchangeDay.location.street }}
              {{ exchangeDay.location.houseNumber }},
              {{ exchangeDay.location.city }},
              {{ exchangeDay.location.country }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from "vue";
import "@/assets/scrollable.css";

export default {
  name: "ScrollableDivs",
  props: {
    exchangeDays: {
      type: Array,
      required: true,
    },
  },
  emits: ["select-exchange-day"],
  setup(props, { emit }) {
    /**
     * sort exchangeDays by startDate
     */
    const sortedExchangeDays = computed(() => {
      return props.exchangeDays.slice().sort((a, b) => {
        return new Date(a.startDate) - new Date(b.startDate);
      });
    });

    /**
     * select exchange day
     * @param {Object} exchangeDay - selected exchange day
     */
    function selectExchangeDay(exchangeDay) {
      emit("select-exchange-day", exchangeDay);
    }

    /**
     * formats a timestamp into a human-readable date string
     * @param {number} timestamp - the date in milliseconds
     * @returns {string} formatted date string in 'DD.MM.YYYY' format
     */
    function formatDate(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleDateString("de-DE");
    }

    return {
      sortedExchangeDays,
      formatDate,
      selectExchangeDay,
    };
  },
};
</script>
