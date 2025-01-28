<template>
  <div class="scroll-container">
    <div class="exchangeDay-list">
      <div
        v-for="(exchangeDay, index) in sortedExchangeDays"
        :key="index"
        @click="selectExchangeDay(exchangeDay)"
        class="list-item"
      >
        <h2 class="heading">{{ exchangeDay.name }}</h2>
        <hr class=".separator-exchange" />
        <div class="details">
          <p class="date">
            {{ formatDate(exchangeDay.startDate) }} bis {{ formatDate(exchangeDay.endDate) }}
          </p>
          <p class="location">
            {{ exchangeDay.location.street }} {{ exchangeDay.location.houseNumber }},<br />
            {{ exchangeDay.location.city }}, {{ exchangeDay.location.country }}
          </p>
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