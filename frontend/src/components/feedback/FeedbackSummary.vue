<template>
  <div class="feedback-summary-container">
    <div class="event-header" :style="{ backgroundImage: backgroundImageUrl ? `url(${backgroundImageUrl})` : '' }">
      <h1 class="event-title">Zusammenfassung</h1>
    </div>

    <div v-if="isLoading" class="loading">
      <p>Loading...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>Error: {{ error }}</p>
    </div>

    <div v-else>
      <!-- General Information -->
      <div class="general-info-container">
        <div class="general-info-left">
          <h2>Event Details</h2>
          <table>
            <tr>
              <td><strong>Event Name:</strong></td>
              <td>{{ data.eventName || "undefined" }}</td>
            </tr>
            <tr>
              <td><strong>Organizer:</strong></td>
              <td>{{ data.organizerName || "undefined" }}</td>
            </tr>
            <tr>
              <td><strong>Date and Time:</strong></td>
              <td>{{ data.eventDate || "undefined" }}</td>
            </tr>
            <tr>
              <td><strong>Description:</strong></td>
              <td>{{ data.description || "No description available" }}</td>
            </tr>
            <tr>
              <td><strong>Tags:</strong></td>
              <td>{{ data.tags?.join(", ") || "No tags available" }}</td>
            </tr>
          </table>
          <button @click="generatePDF">Download PDF</button>
        </div>

        <div class="general-info-right">
          <!-- Radar Chart Container -->
          <h2>Gesamteindruck</h2>
          <div class="chart-container">
            <canvas id="radarChart"></canvas>
          </div>
        </div>
      </div>

      <div class="numerical-feedback">
        <h2>Statistische Analyse</h2>
        <div class="feedback-blocks">
          <!-- Loop through ordered categories -->
          <div v-for="(category, index) in categoryOrder" :key="index" class="feedback-block">
            <h3>{{ category.name.charAt(0).toUpperCase() + category.name.slice(1) }}</h3>

            <!-- Large scale for the category -->
            <div class="large-scale">
              <div class="scale">
                <div class="scale-track">
                  <div class="pin" :style="{ left: `${(category.data.average - 1) * 25}%` }"
                    @mouseover="showPopup('category', index, category.data.average)" @mouseleave="hidePopup">
                    <div v-if="popupVisible['category'] && hoveredPinIndex['category'] === index" class="popup">
                      Average: {{ category.data.average.toFixed(2) }}
                    </div>
                  </div>
                </div>
                <div class="scale-labels">
                  <span>1</span><span>2</span><span>3</span><span>4</span><span>5</span>
                </div>
              </div>
            </div>

            <!-- Small scales for each sub-score -->
            <div class="small-scales">
              <div v-for="(subScore, subIndex) in getOrderedSubScores(category.data.subAverages)" :key="subIndex"
                class="small-scale">
                <h4>{{ subScore.name.charAt(0).toUpperCase() + subScore.name.slice(1) }}</h4>
                <div class="scale">
                  <div class="scale-track">
                    <div class="pin" :style="{ left: `${((subScore.value - 1) / 4) * 100}%` }"
                      @mouseover="showPopup('subScore', subIndex, subScore.value)" @mouseleave="hidePopup">
                      <div v-if="popupVisible['subScore'] && hoveredPinIndex['subScore'] === subIndex" class="popup">
                        Sub-score: {{ subScore.value.toFixed(2) }}
                      </div>
                    </div>
                  </div>
                  <div class="scale-labels">
                    <span>1</span><span>2</span><span>3</span><span>4</span><span>5</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="comments">
        <h2>Kommentare</h2>

        <div v-for="(categoryComments, category) in data.comments" :key="category" class="comment-category">
          <!-- Category Title -->
          <h3>{{ formatKey(category) }}</h3>

          <!-- Card for Comments -->
          <div v-if="categoryComments.length" class="comment-cards">
            <div v-for="(commentObj, index) in categoryComments" :key="index" class="comment-card"
              :style="getCardGradient(commentObj.sentiment)">
              <p class="comment-author"><strong>{{ commentObj.author || "Anonymous" }}:</strong></p>
              <div class="comment-content">
                <div class="comment-column">{{ commentObj.comment }}</div>
              </div>
            </div>
          </div>
          <p v-else>Es wurden keine Kommentare für diese Rubrik abgegeben.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import config from "@/config";
import { Chart, RadarController, RadialLinearScale, LineElement, PointElement, Filler, Legend, Tooltip } from 'chart.js';
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";
pdfMake.addVirtualFileSystem(pdfFonts);

Chart.register(
  RadarController,
  RadialLinearScale,
  LineElement,
  PointElement,
  Filler,
  Legend,
  Tooltip
);

export default {
  name: "EventSummary",
  data() {
    return {
      data: null,
      isLoading: true,
      error: null,
      backgroundImageUrl: null,
      averageScores: [],
      labels: [],
      ctx: null,
      showPopupVisible: false,
      categoryOrder: [],
      popupVisible: {
        category: false,
        subScore: false
      },
      hoveredPinIndex: {
        category: null,
        subScore: null
      },
      hoveredPinValue: null
    };
  },
  computed: {
    eventId() {
      return this.$route.params.eventId;
    },
    numericalFeedbackCategories() {
      return this.data && this.data.numericalFeedback
        ? Object.keys(this.data.numericalFeedback)
        : [];
    },
  },
  methods: {
    /**
     * Fetches event summary data and trainer profile ID.
     */
    async fetchData() {
      try {
        this.isLoading = true;
        await this.fetchEventSummary();
        this.calculateAverages();
        this.setRadarChartLabels();

      } catch (err) {
        this.error = err.message || "An unknown error occurred";
        console.error("Error fetching data:", err);
      } finally {
        this.isLoading = false;
        this.$nextTick(() => {
          // Create the radar chart after DOM is updated
          this.createRadarChart();
        });
      }
    },

    /**
     * Fetches the event summary data from the API.
     */
    async fetchEventSummary() {
      try{
        const response = await fetch(
        `${config.apiBaseUrl}/events/${this.eventId}/summary`,
      );

      if (!response.ok) {
        throw new Error(`Failed to fetch event summary: ${response.status}`);
      }

      this.data = await response.json();

      this.categoryOrder = [
        { name: "OVERALL", data: this.data.numericalFeedback.OVERALL },
        { name: "CONTENT_AND_STRUCTURE", data: this.data.numericalFeedback.CONTENT_AND_STRUCTURE },
        { name: "PARTICIPATION", data: this.data.numericalFeedback.PARTICIPATION },
        { name: "IT_AND_ORGANISATION", data: this.data.numericalFeedback.IT_AND_ORGANISATION },
        { name: "TRAINER", data: this.data.numericalFeedback.TRAINER },
        { name: "SIMILARITY", data: this.data.numericalFeedback.SIMILARITY },
      ]
      }catch(err){
        this.error = err.message || "An error occurred";
        console.error("Error fetching Summary:", err);
      }
    },

    async fetchWordCloud() {
      if (!this.eventId) {
        console.error("Event ID is missing.");
        this.error = "Event ID is missing.";
        return;
      }
      try {
        const response = await fetch(`${config.apiBaseUrl}/events/${this.eventId}/word-cloud`);
        if (!response.ok) {
          throw new Error(`Failed to fetch word cloud: ${response.status}`);
        }
        const blob = await response.blob();
        this.backgroundImageUrl = URL.createObjectURL(blob);
      } catch (err) {
        this.error = err.message || "An error occurred";
        console.error("Error fetching word cloud:", err);
      }
    },

    calculateAverages() {
      if (!this.data) return;

      this.averageScores = this.categoryOrder.map(category => {
        return category.data.average;
      })

    },

    /**
     * Sets radar chart labels based on data groups.
     */
    setRadarChartLabels() {
      if (!this.categoryOrder) return;

      // Set the labels dynamically for the radar chart
      this.labels = this.categoryOrder.map((category) => {
        return category.name.replace(/_/g, " ").toUpperCase();
      });
    },

    createRadarChart() {
      const canvas = document.getElementById("radarChart");
      if (!canvas) {
        console.error("Canvas element for radar chart not found.");
        return;
      }

      this.ctx = canvas.getContext("2d");
      new Chart(this.ctx, {
        type: 'radar',
        data: {
          labels: this.labels,
          datasets: [
            {
              label: 'Durchschnittliche Bewertungen',
              data: this.averageScores,
              backgroundColor: 'rgba(64, 85, 241, 0.2)',
              borderColor: 'rgba(64, 85, 241, 1)',
              pointBackgroundColor: 'rgba(64, 85, 241, 1)',
            },
          ],
        },
        options: {
          scales: {
            r: {
              min: 0,
              max: 5,
              stepSize: 1,
              max: 5,
              ticks: {
                // Setting fixed step size and ensuring no decimal values
                stepSize: 1,
                callback: function (value) {
                  // Ensure the tick marks show only whole numbers
                  return Number.isInteger(value) ? value : '';
                },
              },
            },
          },
          plugins: {
            legend: {
              position: 'top',
            },
          },
        },
      });
    },

    getOrderedSubScores(subAverages) {
      return Object.keys(subAverages).map(key => ({
        name: key,
        value: subAverages[key]
      }));
    },

    /**
     * Formats a key string to be more human-readable.
     * 
     * formatKey(category) {
      // Format the category name (e.g., "Overall" -> "Overall Feedback")
      return `${category.charAt(0).toUpperCase() + category.slice(1)} Feedback`;
    },
     * 
     * @param {string} key The key to format.
     * @returns {string} The formatted key.
     */
    formatKey(key) {
      if (!key || typeof key !== "string") return "Unknown";
      return key
        .replace(/([A-Z])/g, " $1")
        .replace(/^./, (str) => str.toUpperCase());
    },

    /**
   * Generates a gradient style based on sentiment.
   * @param {string} sentiment - The sentiment of the comment ("positive", "neutral", or "negative").
   * @returns {string} The gradient style.
   */
    getCardGradient(sentiment) {
      const colors = {
        positive: "linear-gradient(to bottom right, rgba(255, 255, 255, 0.9), #2ecc71 99%)",
        neutral: "linear-gradient(to bottom right, rgba(255, 255, 255, 0.9), #3498db 99%)",
        negative: "linear-gradient(to bottom right, rgba(255, 255, 255, 0.9), #e74c3c 99%)",
      };
      return { background: colors[sentiment] || "linear-gradient(to bottom right, rgba(255, 255, 255, 0.9), #7f8c8d 99%)" };
    },
    showPopup(type, index, value) {
      this.popupVisible[type] = true;
      this.hoveredPinIndex[type] = index;
      this.hoveredPinValue = value;
    },

    hidePopup() {
      this.popupVisible = {
        category: false,
        subScore: false
      };
      this.hoveredPinIndex = {
        category: null,
        subScore: null
      };
      this.hoveredPinValue = null;
    },
    async generatePDF() {
      if (!this.backgroundImageUrl) {
        console.error("Word cloud image is not available.");
        return;
      }

      const wordCloudImage = await this.blobToDataUrl(this.backgroundImageUrl);

      const chartCanvas = document.getElementById('radarChart');
      const chartImage = chartCanvas.toDataURL("image/png");

      try {
        const docDefinition = {
          content: [
            {
              image: wordCloudImage, // Word cloud image
              width: 900, // Set width of the image
              height: 150, // Set height of the image (adjust based on your image's aspect ratio)
              alignment: 'center',
              margin: [0, 10], // Margin below the image
              fit: [500, 200],// Margin below the header
            },
            {
              text: 'Event Bericht', // The text to display centered
              style: 'header',
              alignment: 'center', // Center the text in the header
              margin: [0, -60], // Position the text in the center of the word cloud (adjust x, y if necessary)
              decoration: 'underline',
            },
            {
              text: '',
              margin: [0,60]
            },
            {
              text: 'Event Details',
              alignment: 'center',
              style: 'subheader',
              margin: [0, 10],
            },
            {
              columns: [
                {
                  width: '50%',
                  table: {
                    body: [
                      ['Event Name', this.data.eventName || 'undefined'],
                      ['Organizer', this.data.organizerName || 'undefined'],
                      ['Date and Time', this.data.eventDate || 'undefined'],
                      ['Description', this.data.description || 'No description available'],
                      ['Tags', this.data.tags?.join(', ') || 'No tags available'],
                    ],
                  },
                  layout: 'lightHorizontalLines',
                  margin: [0, 0, 0, 20],
                },
                {
                  image: chartImage, // Add radar chart image here if needed.
                  width: '50%', // Adjust width as needed
                  height: 700, // Adjust height as needed
                  alignment: 'center',
                  margin: [0, 0, 0, 5], // Margin below the image
                  fit: [300, 300],// Margin below the header
                },
              ]
            },
            {
              text: 'Statistische Analyse',
              style: 'subheader',
              alignment: 'center',
            },
            ...this.categoryOrder.map(category => {
              return {
                text: `${category.name.replace(/_/g, " ")}: Durchschnitt - ${category.data.average.toFixed(2)}`,
                style: 'feedbackCategory',
              };
            }),
            {
              text: 'Kommentare',
              style: 'subheader',
            },
            ...Object.keys(this.data.comments).map(category => {
              const categoryComments = this.data.comments[category];
              return [
                { text: this.formatKey(category), style: 'commentCategoryHeader' },
                ...categoryComments.map(commentObj => {
                  return {
                    text: `${commentObj.author || 'Anonymous'}: ${commentObj.comment}`,
                    style: 'commentText',
                  };
                }),
              ];
            }),
          ],
          styles: {
            header: {
              fontSize: 24,
              bold: true,
              alignment: 'center',
            },
            subheader: {
              fontSize: 18,
              bold: true,
              margin: [0, 10, 0, 10],
            },
            feedbackCategory: {
              fontSize: 14,
              margin: [0, 10, 0, 10],
            },
            commentCategoryHeader: {
              fontSize: 16,
              bold: true,
              margin: [0, 10, 0, 10],
            },
            commentText: {
              fontSize: 12,
              margin: [0, 5, 0, 5],
            },
          },
        };

        pdfMake.createPdf(docDefinition).download(`event-summary-${this.eventId}`);
      } catch (err) {
        console.error("Error generating PDF:", err);
      }
    },
    async blobToDataUrl(blobUrl) {
      return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", blobUrl, true);
        xhr.responseType = "blob";
        xhr.onload = () => {
          const reader = new FileReader();
          reader.onloadend = () => {
            resolve(reader.result); // This is the Data URL
          };
          reader.onerror = reject;
          reader.readAsDataURL(xhr.response);
        };
        xhr.onerror = reject;
        xhr.send();
      });
    }
  },
  mounted() {
    this.fetchData();
    this.fetchWordCloud();
  },
};
</script>

<style scoped>
/* Container for the entire feedback summary */
.feedback-summary-container {
  width: 100%;
  /* Default: full width */
  max-width: 1000px;
  /* Maximum width of the container */
  min-width: 700px;
  /* Minimum width of the container */
  margin: 0 auto;
  /* Center the container horizontally */
  padding: 10px;
  /* Optional padding for inner spacing */
  box-sizing: border-box;
  /* Include padding in width calculation */
}

.event-header {
  position: relative;
  width: 100%;
  height: 150px;
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Overlay to darken or blur the background */
.event-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(100, 126, 126, 0.096);
  /* Semi-transparent black overlay */
  backdrop-filter: blur(1px);
  /* Optional: Adds a blur effect */
  z-index: 1;
  /* Ensures the overlay sits on top of the background */
}

/* Title styling */
.event-title {
  position: relative;
  /* Ensures it appears above the overlay */
  font-size: 2.5rem;
  color: #f7fbff;
  font-weight: bold;
  text-shadow: 0px 3px 6px rgba(0, 0, 0, 0.8);
  /* Creates depth and contrast */
  z-index: 2;
  /* Ensures the text is above the overlay */
}

h1,
h2 {
  color: #34495e;
  text-align: center;
}

.loading {
  text-align: center;
  font-size: 1.2em;
  color: #3498db;
}

.error {
  color: #e74c3c;
  text-align: center;
  font-weight: bold;
}

.general-info-container {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.general-info-left,
.general-info-right {
  flex: 1;
  margin: 10px;
}

.general-info-left table {
  width: 100%;
  border-collapse: collapse;
}

.general-info-left table td {
  padding: 8px;
  border: 1px solid #ddd;
}

.general-info-right .chart-container {
  max-width: 500px;
  height: 400px;
}

h2 {
  margin-bottom: 10px;
  font-size: 1.5rem;
  color: #333;
}

p {
  font-size: 1rem;
  color: #555;
}

.loading,
.error {
  text-align: center;
}

.loading p,
.error p {
  font-size: 1.2rem;
  color: #e74c3c;
}

.numerical-feedback {
  display: flex;
  flex-direction: column;
}

.feedback-blocks {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-gap: 30px;
}

.feedback-block {
  padding: 20px;
  border: 2px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.large-scale .scale {
  position: relative;
  height: 20px;
  width: 100%;
  background: linear-gradient(to right, #01172F, #EAEAEA, #009EE2);
  border-radius: 50px;
  margin-bottom: 20px;
}

.scale-track {
  position: relative;
  width: 100%;
  height: 100%;
}

.pin {
  position: absolute;
  top: -10px;
  width: 20px;
  height: 20px;
  background-color: white;
  border-radius: 50%;
  transform: translate(-50%, 0);
  border: 2px solid #000;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.pin:hover {
  transform: translate(-50%, -5px);
  /* Slightly raise the pin on hover */
  background-color: #3498db;
  /* Change pin color on hover */
}

.popup {
  position: absolute;
  top: -30px;
  left: -50%;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px;
  border-radius: 3px;
  font-size: 12px;
  white-space: nowrap;
  transition: opacity 0.2s ease;
}

.popup p {
  margin: 0;
}

.scale-labels {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
}

.small-scales {
  margin-top: 20px;
}

.small-scale {
  margin-bottom: 10px;
}

.small-scale .scale {
  width: 100%;
  height: 10px;
  background: linear-gradient(to right, #01172F, #EAEAEA, #009EE2);
  border-radius: 50px;
  margin-bottom: 10px;
}

.small-scale .scale-track {
  position: relative;
  width: 100%;
  height: 100%;
}

.small-scale .pin {
  position: absolute;
  top: -8px;
  width: 15px;
  height: 15px;
  background-color: white;
  border-radius: 50%;
  border: 2px solid #000;
  cursor: pointer;
}

.popup {
  position: absolute;
  top: -30px;
  left: -50%;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px;
  border-radius: 3px;
  font-size: 12px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
  background: #ecf0f1;
  border-radius: 5px;
  overflow: hidden;
}

table th,
table td {
  border: 1px solid #bdc3c7;
  padding: 10px;
  text-align: left;
}

table th {
  background-color: #34495e;
  color: #ecf0f1;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  background: #ecf0f1;
  margin: 5px 0;
  padding: 10px;
  border-radius: 5px;
}

.comment-category {
  margin-bottom: 20px;
}

.comment-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(45%, 1fr));
  /* Two columns */
  gap: 15px;
}

.comment-card {
  padding: 15px;
  border-radius: 8px;
  color: #2c3e50;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
  background: white;
  /* Default background */
  background-size: 200%;
  /* Makes the gradient more subtle */
}

.comment-card:hover {
  transform: translateY(-5px);
}

.comment-author {
  font-size: 1em;
  margin-bottom: 8px;
}

.comment-content {
  display: flex;
  gap: 10px;
}

.comment-column {
  flex: 1;
  word-break: break-word;
}
</style>
