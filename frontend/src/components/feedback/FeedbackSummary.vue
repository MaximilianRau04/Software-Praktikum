<template>
  <div>
    <button class="back-button" @click="goBack">Zurück</button>
  </div>

  <div class="feedback-summary-scroll">
    <div class="feedback-summary-container">
      <div
        class="event-header"
        :style="{
          backgroundImage: backgroundImageUrl
            ? `url(${backgroundImageUrl})`
            : '',
        }"
      >
        <h1 class="event-title">Zusammenfassung</h1>
      </div>

      <div v-if="loading" class="loading">
        <VueSpinner size="20" color="blue" />
      </div>

      <div v-else-if="dataUnavailable" class="no-data-container">
        <h2>Noch keine Daten verfügbar</h2>
        <p>
          Bitte kommen Sie später wieder, um die Event-Zusammenfassung zu sehen.
        </p>
        <!-- Optional: Add retry button -->
        <button @click="fetchData" class="back-button">Erneut versuchen</button>
      </div>

      <div v-else>
        <!-- General Information -->
        <div class="general-info-container" v-if="event">
          <div class="general-info-left">
            <h2>Event Details</h2>
            <table>
              <tr>
                <td><strong>Event Name:</strong></td>
                <td>{{ event.name || "undefined" }}</td>
              </tr>
              <tr>
                <td><strong>Trainer:</strong></td>
                <td>
                  {{
                    organizer.firstname + " " + organizer.lastname ||
                    "undefined"
                  }}
                </td>
              </tr>
              <tr>
                <td><strong>Datum und Uhrzeit:</strong></td>
                <td>
                  {{
                    new Date(event.date).toLocaleDateString("de-DE", {
                      weekday: "long",
                      year: "numeric",
                      month: "long",
                      day: "numeric",
                    })
                  }}
                  <br />
                </td>
              </tr>
              <tr>
                <td><strong>Ort:</strong></td>
                <td>
                  {{
                    location.street +
                    " " +
                    location.houseNumber +
                    ", " +
                    location.postalCode +
                    " " +
                    location.city +
                    ", " +
                    location.country
                  }}
                </td>
              </tr>
              <tr>
                <td><strong>Beschreibung:</strong></td>
                <td>
                  {{ event.description || "Keine Beschreibung vorhanden." }}
                </td>
              </tr>
              <tr>
                <td><strong>Registrierte Nutzer:</strong></td>
                <td>{{ registeredUsers }}</td>
              </tr>
              <tr>
                <td><strong>abgegebene Feedbacks:</strong></td>
                <td>{{ givenFeedback }}</td>
              </tr>
              <tr>
                <td><strong>Tags:</strong></td>
                <td>
                  <li v-for="(tag, index) in tags" :key="index">
                    {{ tag.name }}
                  </li>
                </td>
              </tr>
            </table>
            <button class="pdf-button" @click="generatePDF">
              Als PDF exportieren
            </button>
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
            <div
              v-for="(category, index) in categoryOrder"
              :key="index"
              class="feedback-block"
            >
              <h3>
                {{
                  category.name.charAt(0).toUpperCase() + category.name.slice(1)
                }}
              </h3>

              <!-- Large scale for the category -->
              <div class="large-scale">
                <div class="scale">
                  <div class="scale-track">
                    <div
                      class="pin"
                      :style="{ left: `${(category.data.average - 1) * 25}%` }"
                      @mouseover="
                        showPopup('category', index, category.data.average)
                      "
                      @mouseleave="hidePopup"
                    >
                      <div
                        v-if="
                          popupVisible['category'] &&
                          hoveredPinIndex['category'] === index
                        "
                        class="popup"
                      >
                        Durchschnitt: {{ category.data.average.toFixed(2) }}
                      </div>
                    </div>
                  </div>
                  <div class="scale-labels">
                    <span>1</span><span>2</span><span>3</span><span>4</span
                    ><span>5</span>
                  </div>
                </div>
              </div>

              <!-- Small scales for each sub-score -->
              <div class="small-scales">
                <div
                  v-for="(subScore, subIndex) in getOrderedSubScores(
                    category.data.subAverages,
                  )"
                  :key="subIndex"
                  class="small-scale"
                >
                  <h4>
                    {{
                      formatKey(
                        subScore.name.charAt(0).toUpperCase() +
                          subScore.name.slice(1),
                      )
                    }}
                  </h4>
                  <div class="scale">
                    <div class="scale-track">
                      <div
                        class="pin"
                        :style="{
                          left: `${((subScore.value - 1) / 4) * 100}%`,
                        }"
                        @mouseover="
                          showPopup('subScore', subIndex, subScore.value)
                        "
                        @mouseleave="hidePopup"
                      >
                        <div
                          v-if="
                            popupVisible['subScore'] &&
                            hoveredPinIndex['subScore'] === subIndex
                          "
                          class="popup"
                        >
                          Unterpunktzahl: {{ subScore.value.toFixed(2) }}
                        </div>
                      </div>
                    </div>
                    <div class="scale-labels">
                      <span>1</span><span>2</span><span>3</span><span>4</span
                      ><span>5</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="tag-statistics" v-if="data.tagStatistics">
          <h2>Tag Leistungsanalyse</h2>
          <div class="tag-cards">
            <div
              v-for="(statistic, tagName) in data.tagStatistics"
              :key="tagName"
              class="tag-card"
            >
              <div class="tag-header">
                <div class="tag-meta">
                  <h3>{{ statistic.tag.name }}</h3>
                  <div class="engagement-score">
                    <div
                      class="score-dot"
                      :style="engagementColor(statistic.averageWeight)"
                    ></div>
                    <span>{{ formatWeight(statistic.averageWeight) }}</span>
                  </div>
                </div>
                <div class="participation-badges">
                  <div class="badge">
                    👥 {{ statistic.totalVisits }}
                    <span class="tooltip"
                      >Einzigartige Teilnehmer des Tags</span
                    >
                  </div>
                  <div class="badge">
                    💬 {{ statistic.totalFeedback }}
                    <span class="tooltip">Globale Anzahl Feedback zum Tag</span>
                  </div>
                </div>
              </div>

              <div class="tag-metrics">
                <div class="metric-group">
                  <h4>Feedback-Stimmung</h4>
                  <div class="sentiment-display">
                    <div class="sentiment-face">
                      {{ getSentimentEmoji(statistic.averageSentiment) }}
                    </div>
                    <div class="sentiment-bar">
                      <div class="sentiment-fill"></div>
                      <div
                        class="sentiment-indicator"
                        :style="{
                          left: `${((statistic.averageSentiment + 1) / 2) * 100}%`,
                        }"
                      ></div>
                      <div class="sentiment-labels">
                        <span>Negative</span>
                        <span>Neutral</span>
                        <span>Positive</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="metric-group">
                  <h4>Durchschnitt</h4>
                  <div class="star-rating">
                    <div
                      class="stars"
                      :style="`--rating: ${statistic.averageRating}`"
                      aria-label="Rating"
                    ></div>
                    <span class="rating-text">
                      ({{ statistic.averageRating.toFixed(1) }}/5)
                    </span>
                  </div>
                </div>
              </div>

              <div class="weight-visual">
                <div
                  class="donut-chart"
                  :style="donutStyle(statistic.averageWeight)"
                >
                  <span class="weight-percent">
                    {{ Math.round(statistic.averageWeight * 100) }}%
                  </span>
                </div>
                <div class="weight-labels">
                  <span>geringer Einfluss</span>
                  <span>Hoher Einfluss</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="comments">
          <h2>Kommentare</h2>

          <div
            v-for="(categoryComments, category) in data.comments"
            :key="category"
            class="comment-category"
          >
            <!-- Category Title -->
            <h3>{{ formatKey(category) }}</h3>

            <!-- Card for Comments -->
            <div v-if="categoryComments.length" class="comment-cards">
              <div
                v-for="(commentObj, index) in categoryComments"
                :key="index"
                class="comment-card"
                :style="getCardGradient(commentObj.sentiment)"
              >
                <p class="comment-author">
                  <strong>{{ commentObj.author || "Anonymous" }}:</strong>
                </p>
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
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import TrainerTags from "@/components/profile/TrainerTags.vue";
import {
  Chart,
  RadarController,
  RadialLinearScale,
  LineElement,
  PointElement,
  Filler,
  Legend,
  Tooltip,
} from "chart.js";
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";
import { showToast, Toast } from "@/types/toasts";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { VueSpinner } from "vue3-spinners";
import api from "@/util/api";
import "@/assets/feedback/feedback-summary.css";

pdfMake.addVirtualFileSystem(pdfFonts);

Chart.register(
  RadarController,
  RadialLinearScale,
  LineElement,
  PointElement,
  Filler,
  Legend,
  Tooltip,
);

export default {
  components: {
    TrainerTags,
    VueSpinner,
  },
  name: "EventSummary",
  data() {
    return {
      router: useRouter(),
      event: null,
      organizer: {
        firstname: "",
        lastname: "",
      },
      registeredUsers: 0,
      givenFeedback: 0,
      location: {
        street: "",
        houseNumber: "",
        postalCode: "",
        city: "",
      },
      data: null,
      tags: [],
      dataUnavailable: false,
      loading: true,
      error: null,
      backgroundImageUrl: null,
      averageScores: [],
      labels: [],
      ctx: null,
      showPopupVisible: false,
      categoryOrder: [],
      popupVisible: {
        category: false,
        subScore: false,
      },
      hoveredPinIndex: {
        category: null,
        subScore: null,
      },
      hoveredPinValue: null,
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
    goBack() {
      this.router.push({
        name: "EventPage",
        params: { eventId: this.eventId.toString() },
      });
    },

    getColorPalette() {
      return ["#009EE2", "#01172F", "#4CAF50"];
    },

    getRandomColor() {
      const palette = this.getColorPalette();
      const randomIndex = Math.floor(Math.random() * palette.length);
      return palette[randomIndex];
    },

    getSentimentClass(sentiment) {
      if (sentiment > 0.3) return "positive";
      if (sentiment < -0.3) return "negative";
      return "neutral";
    },

    formatWeight(weight) {
      return `Einfluss ${Math.round(weight * 100)}%`;
    },

    engagementColor(weight) {
      const hue = 210; // Blue base
      const saturation = 70;
      const lightness = 50 - weight * 20;
      return {
        backgroundColor: `hsl(${hue}, ${saturation}%, ${lightness}%)`,
      };
    },

    getSentimentEmoji(sentiment) {
      if (sentiment > 0.3) return "😊";
      if (sentiment > -0.3) return "😐";
      return "😞";
    },

    sentimentBarStyle(sentiment) {
      const percentage = ((sentiment + 1) / 2) * 100;
      return {
        width: `${percentage}%`,
        left: `${Math.max(0, percentage - 50)}%`,
        background: `linear-gradient(to right, 
        ${sentiment > 0 ? "#e74c3c" : "#3498db"}, 
        ${sentiment > 0 ? "#2ecc71" : "#3498db"})`,
      };
    },

    donutStyle(weight) {
      return {
        "--percentage": weight * 100,
      };
    },

    /**
     * Fetches event summary data and trainer profile ID.
     */
    async fetchData() {
      try {
        this.loading = true;
        await this.fetchEventSummary();

        await Promise.all([
          this.fetchEventDetails(),
          this.fetchWordCloud(),
          this.fetchEventTags(),
          this.fetchRegisteredUsers(),
        ]);

        this.calculateAverages();
        this.setRadarChartLabels();
      } catch (err) {
        if (this.dataUnavailable) {
          showToast(
            new Toast(
              "Information",
              "Bislang wurde noch kein Feedback zu diesem Event abgegeben. Kommen Sie bitte später wieder.",
              "info",
              faXmark,
              5,
            ),
          );
          console.error(err);
        } else {
          showToast(
            new Toast(
              "Fehler",
              "Ein kritischer Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.",
              "error",
              faXmark,
              5,
            ),
          );
          console.error(err);
        }
      } finally {
        this.loading = false;
        this.$nextTick(() => {
          this.createRadarChart();
        });
      }
    },

    /**
     * Fetches the users who have registered for the event.
     */
    async fetchRegisteredUsers() {
      try {
        const response = await api.get(
          `/events/${this.eventId}/registeredUsers`,
        );
        this.registeredUsers = response.data.length;
      } catch (err) {
        console.error(err);
      }
    },

    /**
     * Fetches the event summary data from the API.
     */
    async fetchEventSummary() {
      try {
        const response = await api.get(`/events/${this.eventId}/summary`);

        if (response.status === 204 || response.status === 400) {
          this.dataUnavailable = true;
          throw new Error("No feedback available");
        }

        this.givenFeedback =
          response.data.numericalFeedback.OVERALL.responseCount;
        this.data = response.data;

        if (response.status === 400) {
          showToast(
            new Toast(
              "Information",
              `Bislang wurde noch kein Feedback zu diesem Event abgegeben.`,
              "info",
              faXmark,
              5,
            ),
          );
        }

        this.categoryOrder = [
          { name: "GESAMT", data: this.data.numericalFeedback.OVERALL },
          {
            name: "INHALT UND STRUKTUR",
            data: this.data.numericalFeedback.CONTENT_AND_STRUCTURE,
          },
          {
            name: "TEILNAME",
            data: this.data.numericalFeedback.PARTICIPATION,
          },
          {
            name: "IT UND ORGANISATION",
            data: this.data.numericalFeedback.IT_AND_ORGANISATION,
          },
          { name: "TRAINER", data: this.data.numericalFeedback.TRAINER },
          { name: "ÄHNLICHKEIT", data: this.data.numericalFeedback.SIMILARITY },
        ];
      } catch (err) {
        if (err.response?.status === 400 || err.response?.status === 404) {
          this.dataUnavailable = true;
        }
        throw err;
      }
    },

    /**
     * Fetches the event details from the API.
     */
    async fetchEventDetails() {
      try {
        if (this.dataUnavailable) return;

        const response = await api.get(`/events/${this.eventId}`);
        const eventData = await response.data;
        this.event = eventData;

        const organizerResponse = await api.get(
          `/events/${this.eventId}/organizer`,
        );
        this.organizer = await organizerResponse.data;

        const locationResponse = await api.get(
          `/events/${this.eventId}/location`,
        );
        this.location = await locationResponse.data;
      } catch (err) {
        throw err;
      }
    },

    /**
     * Fetches the word cloud image from the API.
     */
    async fetchWordCloud() {
      if (this.dataUnavailable) return;

      if (!this.eventId) {
        showToast(
          new Toast(
            "Fehler",
            `Es wurde keine Event ID gefunden. Laden Sie bitte die Seite neu.`,
            "error",
            faXmark,
            5,
          ),
        );
        return;
      }
      try {
        const response = await api.get(`/events/${this.eventId}/word-cloud`, {
          responseType: "blob",
        });
        if (response.status !== 200) {
          showToast(
            new Toast(
              "Fehler",
              `Die Word Cloud konnte nicht geladen werden.`,
              "error",
              faXmark,
              5,
            ),
          );
        }
        const blob = await response.data;
        this.backgroundImageUrl = URL.createObjectURL(blob);
      } catch (err) {
        throw err;
      }
    },
    async fetchEventTags() {
      if (this.dataUnavailable) return;

      try {
        const tagsResponse = await api.get(`/events/${this.eventId}/tags`);
        if (tagsResponse.status !== 200)
          throw new Error("Event Tags konnten nicht geladen werden.");
        this.tags = await tagsResponse.data;
      } catch (err) {
        throw err;
      }
    },

    /**
     * Calculates the average scores for each category.
     */
    calculateAverages() {
      if (!this.data) return;

      this.averageScores = this.categoryOrder.map((category) => {
        return category.data.average;
      });
    },

    /**
     * Sets radar chart labels based on data groups.
     */
    setRadarChartLabels() {
      if (!this.categoryOrder) return;

      this.labels = this.categoryOrder.map((category) => {
        return category.name.replace(/_/g, " ").toUpperCase();
      });
    },

    /**
     * Creates a radar chart using Chart.js.
     */
    createRadarChart() {
      const canvas = document.getElementById("radarChart");

      this.ctx = canvas.getContext("2d");
      new Chart(this.ctx, {
        type: "radar",
        data: {
          labels: this.labels,
          datasets: [
            {
              label: "Durchschnittliche Bewertungen",
              data: this.averageScores,
              backgroundColor: "rgba(64, 85, 241, 0.2)",
              borderColor: "rgba(64, 85, 241, 1)",
              pointBackgroundColor: "rgba(64, 85, 241, 1)",
            },
          ],
        },
        options: {
          scales: {
            r: {
              min: 0,
              max: 5,
              stepSize: 1,
              ticks: {
                stepSize: 1,
                callback: function (value) {
                  return Number.isInteger(value) ? value : "";
                },
              },
            },
          },
          plugins: {
            legend: {
              position: "top",
            },
          },
        },
      });
    },

    getOrderedSubScores(subAverages) {
      return Object.keys(subAverages).map((key) => ({
        name: key,
        value: subAverages[key],
      }));
    },

    /**
     * Formats a key string to be more human-readable.
     *
     * @param {string} key The key to format.
     * @returns {string} The formatted key.
     */
    formatKey(key) {
      const translations = {
        CONTENT_AND_STRUCTURE: "Inhalt und Struktur",
        PARTICIPATION: "Teilnahme",
        IT_AND_ORGANISATION: "IT und Organisation",
        TRAINER: "Trainer",
        SIMILARITY: "Ähnlichkeit",
        Organisational: "Organisatorisch",
        Overall: "Gesamt",
        Relevance: "Relevant",
        Understandability: "Verständlichkeit",
        Practicality: "Praktikabilität",
        Reasonability: "Angemessenheit",
        ContentDepth: "Inhaltstiefe",
        Participation: "Teilnahme",
        Atmosphere: "Atmosphäre",
        Networking: "Vernetzung",
        Equipment: "Ausstattung",
        Comfortability: "Komfortabilität",
        Communication: "Kommunikation",
        Presentability: "Präsentierbarkeit",
        TimeManagement: "Zeitmanagement",
        Competency: "Kompetenz",
        Interactivity: "Interaktivität",
        SimilarEventParticipation: "Ähnliche Eventteilnahme",
        ENJOYMENT: "Vergnügen",
        PERSONAL_IMPROVEMENT: "Persönliche Verbesserung",
        RECOMMENDATION: "Empfehlung",
        REQUEST: "Anfrage",
        IMPROVEMENT: "Verbesserung",
      };
      if (!key || typeof key !== "string") return "Unknown";
      return (
        translations[key] ||
        key.replace(/([A-Z])/g, " $1").replace(/^./, (str) => str.toUpperCase())
      );
    },

    /**
     * Generates a gradient style based on sentiment.
     * @param {string} sentiment - The sentiment of the comment ("positive", "neutral", or "negative").
     * @returns {string} The gradient style.
     */
    getCardGradient(sentiment) {
      const colors = {
        positive:
          "linear-gradient(to bottom right, rgba(255, 255, 255, 0.9) 30%, #2ecc71 170%)",
        neutral:
          "linear-gradient(to bottom right, rgba(255, 255, 255, 0.9) 30%, #3498db 170%)",
        negative:
          "linear-gradient(to bottom right, rgba(255, 255, 255, 0.9) 30%, #e74c3c 170%)",
      };
      return {
        background:
          colors[sentiment] ||
          "linear-gradient(to bottom right, rgba(255, 255, 255, 0.9) 30%, #7f8c8d 170%)",
      };
    },
    showPopup(type, index, value) {
      this.popupVisible[type] = true;
      this.hoveredPinIndex[type] = index;
      this.hoveredPinValue = value;
    },

    hidePopup() {
      this.popupVisible = {
        category: false,
        subScore: false,
      };
      this.hoveredPinIndex = {
        category: null,
        subScore: null,
      };
      this.hoveredPinValue = null;
    },

    /**
     * Generates a PDF document with the event summary.
     */
    async generatePDF() {
      if (!this.backgroundImageUrl) {
        showToast(
          new Toast(
            "Fehler",
            `Word Cloud Bild ist nicht verfügbar.`,
            "error",
            faXmark,
            5,
          ),
        );
        return;
      }

      const wordCloudImage = await this.blobToDataUrl(this.backgroundImageUrl);
      const chartCanvas = document.getElementById("radarChart");
      const chartImage = chartCanvas.toDataURL("image/png");

      try {
        const docDefinition = {
          content: [
            {
              image: wordCloudImage,
              width: 900,
              height: 150,
              alignment: "center",
              margin: [0, 10],
              fit: [500, 200],
            },
            {
              text: "Event Bericht",
              style: "header",
              alignment: "center",
              margin: [0, -60],
              decoration: "underline",
            },
            {
              text: "",
              margin: [0, 60],
            },
            {
              text: "Detaillierte Informationen zum Event",
              style: "subheader",
            },
            {
              table: {
                widths: ["30%", "70%"],
                body: [
                  [
                    { text: "Event Name", style: "tableHeader" },
                    {
                      text: this.event.name || "Nicht verfügbar",
                      style: "tableContent",
                    },
                  ],
                  [
                    { text: "Organisator", style: "tableHeader" },
                    {
                      text: this.data.organizerName || "Nicht verfügbar",
                      style: "tableContent",
                    },
                  ],
                  [
                    { text: "Datum", style: "tableHeader" },
                    {
                      text:
                        new Date(this.event.date).toLocaleDateString("de-DE", {
                          weekday: "long",
                          year: "numeric",
                          month: "long",
                          day: "numeric",
                        }) || "Nicht verfügbar",
                      style: "tableContent",
                    },
                  ],
                  [
                    { text: "Ort", style: "tableHeader" },
                    {
                      text: this.location.city || "Nicht angegeben",
                      style: "tableContent",
                    },
                  ],
                  [
                    { text: "Beschreibung", style: "tableHeader" },
                    {
                      text:
                        this.event.description ||
                        "Keine Beschreibung verfügbar",
                      style: "tableContent",
                    },
                  ],
                  [
                    { text: "Registrierte Nutzer", style: "tableHeader" },
                    {
                      text:
                        this.registeredUsers || "Keine registrierten Nutzer",
                      style: "tableContent",
                    },
                  ],
                  [
                    { text: "abgegebene Feedbacks", style: "tableHeader" },
                    {
                      text: this.givenFeedback || "Keine registrierten Nutzer",
                      style: "tableContent",
                    },
                  ],
                  [
                    { text: "Tags", style: "tableHeader" },
                    {
                      text:
                        this.tags?.map((tag) => tag.name).join(", ") ||
                        "Keine Tags",
                      style: "tableContent",
                    },
                  ],
                ],
              },
              layout: "lightHorizontalLines",
              margin: [0, 10, 0, 20],
            },
            {
              text: "Statistische Analyse",
              style: "subheader",
            },
            {
              table: {
                widths: ["50%", "50%"],
                body: [
                  ...this.categoryOrder.map((category) => [
                    {
                      text: category.name.replace(/_/g, " "),
                      style: "analyzeHeader",
                    },
                    {
                      text: `Durchschnitt: ${category.data.average.toFixed(2)}`,
                      style: "analyzeContent",
                      alignment: "right",
                    },
                  ]),
                ],
              },
              layout: "lightHorizontalLines",
              margin: [0, 10, 0, 20],
            },
            { text: "Radar Chart", style: "subheader", pageBreak: "before" },
            {
              image: chartImage,
              width: 400,
              height: 400,
              alignment: "center",
              margin: [0, 10],
            },
            {
              text: "Kommentare",
              style: "subheader",
            },
            ...Object.keys(this.data.comments).map((category) => {
              const categoryComments = this.data.comments[category];
              return [
                {
                  text: this.formatKey(category),
                  style: "commentCategoryHeader",
                },
                ...categoryComments.map((commentObj) => ({
                  text: `${commentObj.author || "Anonym"}: ${commentObj.comment}`,
                  style: "commentText",
                  margin: [10, 5, 0, 5],
                })),
              ];
            }),
          ],
          styles: {
            header: {
              fontSize: 24,
              bold: true,
              alignment: "center",
              margin: [0, 0, 0, 20],
              color: "#2c3e50",
            },
            subheader: {
              fontSize: 18,
              bold: true,
              margin: [0, 10, 0, 10],
              color: "#34495e",
            },
            tableHeader: {
              bold: true,
              fillColor: "#f2f2f2",
              color: "#2c3e50",
              fontSize: 12,
            },
            tableContent: {
              fontSize: 11,
              color: "#333",
            },
            analyzeHeader: {
              fontSize: 14,
              bold: true,
              color: "#2980b9",
            },
            analyzeContent: {
              fontSize: 12,
              color: "#7f8c8d",
            },
            commentCategoryHeader: {
              fontSize: 16,
              bold: true,
              margin: [0, 15, 0, 10],
              color: "#2c3e50",
            },
            commentText: {
              fontSize: 11,
              color: "#555",
              italics: true,
            },
          },
          defaultStyle: {},
        };

        pdfMake
          .createPdf(docDefinition)
          .download(`event-summary-${this.eventId}`);
      } catch (err) {
        showToast(
          new Toast(
            "Feler",
            `Ein Fehler bei der Erstellung der PDF ist aufgetreten, versuchen Sie es später erneut.`,
            "error",
            faXmark,
            5,
          ),
        );
      }
    },

    generateStarRating(average) {
      const maxStars = 5;
      const fullStars = Math.round(average);
      let stars = "";

      for (let i = 0; i < fullStars; i++) {
        stars += "★";
      }
      for (let i = fullStars; i < maxStars; i++) {
        stars += "☆";
      }

      return stars;
    },

    /**
     * Converts a blob URL to a data URL.
     * @param {string} blobUrl - The blob URL to convert.
     * @returns {Promise<string>} The data URL.
     */
    async blobToDataUrl(blobUrl) {
      return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", blobUrl, true);
        xhr.responseType = "blob";
        xhr.onload = () => {
          const reader = new FileReader();
          reader.onloadend = () => {
            resolve(reader.result);
          };
          reader.onerror = reject;
          reader.readAsDataURL(xhr.response);
        };
        xhr.onerror = reject;
        xhr.send();
      });
    },
  },
  mounted() {
    this.fetchData();
  },
};
</script>

<style scoped>
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

.event-header::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(100, 126, 126, 0.096);
  backdrop-filter: blur(1px);
  z-index: 1;
}

.event-title {
  position: relative;
  font-size: 2.5rem;
  color: #f7fbff;
  font-weight: bold;
  text-shadow: 0px 3px 6px rgba(0, 0, 0, 0.8);
  z-index: 2;
}
</style>
