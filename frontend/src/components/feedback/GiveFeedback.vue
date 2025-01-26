<template>
  <div>
    <button class="back-button" @click="goBack">Zurück</button>
  </div>

  <div class="scrollable-container">
    <!-- Überschrift und Beschreibung -->
    <div class="survey-header">
      <h2>Feedback für {{ event.name }}</h2>
      <p>
        Veranstaltung vom
        {{
          new Date(event.date).toLocaleDateString("de-DE", {
            weekday: "long",
            year: "numeric",
            month: "long",
            day: "numeric",
          })
        }}
        <br />
      </p>
      <div class="survey-instructions">
        <p>
          Wir bitten Sie, diese Umfrage auszufüllen, um wertvolles Feedback zu
          Ihrem Erlebnis bei <strong>{{ event.name }}</strong> mit
          <strong>{{ organizer.firstname + " " + organizer.lastname }}</strong>
          zu erhalten. Ihre Bewertungen und Kommentare tragen dazu bei, unsere
          zukünftigen Veranstaltungen kontinuierlich zu verbessern.
        </p>
        <p>
          Bitte bewerten Sie die verschiedenen Bereiche der Veranstaltung auf
          einer Skala von 1 bis 5, wobei 5 die bestmögliche Bewertung und 1 die
          schlechteste Bewertung darstellt.
        </p>
        <p>
          Wir laden Sie ein, Ihre Gedanken zu den Fragen „Was hat Ihnen
          besonders gefallen?“ und „Welche Aspekte könnten verbessert werden?“
          zu teilen. Ihre detaillierten Rückmeldungen sind für uns von großem
          Wert.
        </p>
        <p>
          Erforderliche Felder sind mit einem
          <span class="required">*</span> markiert.
        </p>
        <p>
          Sie haben die Möglichkeit, Ihr Feedback anonym abzugeben. Diese Option
          finden Sie am Ende der Umfrage.
        </p>
        <p>Die Bearbeitung der Umfrage dauert etwa 5-7 Minuten.</p>
      </div>
    </div>

    <form class="feedback-form" @submit.prevent="submitFeedback">
      <!-- Overall Section -->
      <div class="section">
        <h3 class="section-title">Gesamtbewertung</h3>
        <div
          v-for="(field, index) in overallFields"
          :key="index"
          class="feedback-row"
          style="position: relative"
          :ref="`field-${field.key}`"
        >
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div
              v-for="rating in 5"
              :key="rating"
              class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }"
              @click="updateField(field.key, rating)"
            >
              {{ rating }}
            </div>
          </div>
          <div v-if="errors[field.key]" class="error-message">
            {{ errors[field.key] }}
          </div>
        </div>
        <hr />
      </div>

      <!-- Content and Structure Section -->
      <div class="section">
        <h3 class="section-title">Inhalt und Struktur</h3>
        <div
          v-for="(field, index) in contentFields"
          :key="index"
          class="feedback-row"
          style="position: relative"
          :ref="`field-${field.key}`"
        >
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div
              v-for="rating in 5"
              :key="rating"
              class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }"
              @click="updateField(field.key, rating)"
            >
              {{ rating }}
            </div>
          </div>
          <div v-if="errors[field.key]" class="error-message">
            {{ errors[field.key] }}
          </div>
        </div>
        <hr />
      </div>

      <!-- Trainer Section -->
      <div class="section">
        <h3 class="section-title">Trainer</h3>
        <div
          v-for="(field, index) in trainerFields"
          :key="index"
          class="feedback-row"
          style="position: relative"
          :ref="`field-${field.key}`"
        >
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div
              v-for="rating in 5"
              :key="rating"
              class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }"
              @click="updateField(field.key, rating)"
            >
              {{ rating }}
            </div>
          </div>
          <div v-if="errors[field.key]" class="error-message">
            {{ errors[field.key] }}
          </div>
        </div>
        <hr />
      </div>

      <!-- Participation Section -->
      <div class="section">
        <h3 class="section-title">Teilnahme</h3>
        <div
          v-for="(field, index) in participationFields"
          :key="index"
          class="feedback-row"
          style="position: relative"
          :ref="`field-${field.key}`"
        >
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div
              v-for="rating in 5"
              :key="rating"
              class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }"
              @click="updateField(field.key, rating)"
            >
              {{ rating }}
            </div>
          </div>
          <div v-if="errors[field.key]" class="error-message">
            {{ errors[field.key] }}
          </div>
        </div>
        <hr />
      </div>

      <!-- Equipment and Organisation Section -->
      <div class="section">
        <h3 class="section-title">Ausrüstung und Organisation</h3>
        <div
          v-for="(field, index) in organisationFields"
          :key="index"
          class="feedback-row"
          style="position: relative"
          :ref="`field-${field.key}`"
        >
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div
              v-for="rating in 5"
              :key="rating"
              class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }"
              @click="updateField(field.key, rating)"
            >
              {{ rating }}
            </div>
          </div>
          <div v-if="errors[field.key]" class="error-message">
            {{ errors[field.key] }}
          </div>
        </div>
        <hr />
      </div>

      <!-- Comments Section -->
      <div class="section">
        <h3 class="section-title">Kommentare</h3>
        <div
          v-for="(field, index) in commentFields"
          :key="index"
          class="feedback-row"
          style="position: relative"
          :ref="`field-${field.key}`"
        >
          <div class="feedback-label">
            <span
              >{{ field.label }}
              <span
                v-if="
                  field.key === 'enjoymentComment' ||
                  field.key === 'improvementComment'
                "
                ><span class="required">*</span></span
              ></span
            >
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="feedback-input">
            <textarea
              :name="field.key"
              v-model="feedbackData[field.key]"
              :maxlength="field.maxLength"
              rows="3"
            ></textarea>
          </div>
          <div v-if="errors[field.key]" class="comment-error">
            {{ errors[field.key] }}
          </div>
        </div>
        <hr />
      </div>

      <!-- Recommendation Section -->
      <div class="section">
        <h3 class="section-title">Empfehlungen</h3>
        <div
          v-for="(field, index) in recommendationFields"
          :key="index"
          class="feedback-row"
          style="position: relative"
          :ref="`field-${field.key}`"
        >
          <div class="feedback-label">
            <span
              >{{ field.label }}
              <span
                v-if="
                  field.key === 'isEventRecommended' ||
                  field.key === 'similarEventParticipationScore'
                "
                ><span class="required">*</span></span
              ></span
            >
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>

          <div class="feedback-input">
            <!-- Textarea for comments -->
            <textarea
              v-if="field.maxLength"
              :name="field.key"
              v-model="feedbackData[field.key]"
              :maxLength="field.maxLength"
              rows="3"
            ></textarea>

            <!-- Radio buttons for boolean (isEventRecommended) -->
            <div
              v-else-if="field.key === 'isEventRecommended'"
              class="boolean-options"
            >
              <label>
                <input
                  type="radio"
                  :name="field.key"
                  value="true"
                  v-model="feedbackData[field.key]"
                />
                Ja
              </label>
              <label>
                <input
                  type="radio"
                  :name="field.key"
                  value="false"
                  v-model="feedbackData[field.key]"
                />
                Nein
              </label>
              <div v-if="errors.isEventRecommended" class="error-message">
                {{ errors.isEventRecommended }}
              </div>
            </div>

            <div v-else-if="field.key === 'similarEventParticipationScore'">
              <div class="rating-group">
                <div
                  v-for="rating in 5"
                  :key="rating"
                  class="rating-circle"
                  :class="{ selected: feedbackData[field.key] === rating }"
                  @click="updateField(field.key, rating)"
                >
                  {{ rating }}
                </div>
              </div>
              <div v-if="errors[field.key]" class="error-message">
                {{ errors[field.key] }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- Submit Button -->
      <div class="section submit-section">
        <div class="anonymous-feedback"></div>
        <button type="submit" class="submit-button">Feedback absenden</button>
        <label>
          <input type="checkbox" v-model="feedbackData.anonymousFeedback" />
          Anonym absenden?
        </label>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import { onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import config from "@/config";
import Cookies from "js-cookie";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

export default {
  data() {
    return {
      overallFields: [
        {
          key: "overallScore",
          label: "Gesamteindruck",
          explanation:
            "Bewertung des Gesamtbildes und des Eindrucks des Workshops aus Ihrer Sicht.",
        },
        {
          key: "organisationalScore",
          label: "Organisation",
          explanation:
            "Bewertung der organisatorischen Abläufe und der logistischen Vorbereitung des Workshops.",
        },
        {
          key: "relevanceScore",
          label: "Relevanz",
          explanation:
            "Bewertung, inwieweit der Inhalt des Workshops für die Teilnehmer relevant und nützlich war.",
        },
      ],
      contentFields: [
        {
          key: "understandabilityScore",
          label: "Verständlichkeit",
          explanation:
            "Bewertung der Klarheit und Nachvollziehbarkeit des vermittelten Inhalts während des Workshops.",
        },
        {
          key: "contentDepthScore",
          label: "Inhaltliche Tiefe",
          explanation:
            "Bewertung, wie tiefgehend und fundiert der vermittelte Inhalt des Workshops war.",
        },
        {
          key: "practicalityScore",
          label: "Praktikabilität",
          explanation:
            "Bewertung der praktischen Anwendbarkeit und Nutzbarkeit des vermittelten Wissens im Arbeitsalltag.",
        },
        {
          key: "reasonabilityScore",
          label: "Nachvollziehbarkeit",
          explanation:
            "Bewertung, wie gut der Inhalt des Workshops logisch und verständlich aufbereitet war.",
        },
      ],
      trainerFields: [
        {
          key: "competencyScore",
          label: "Kompetenz",
          explanation:
            "Bewertung der fachlichen Qualifikationen und Expertise des Trainers während des Workshops.",
        },
        {
          key: "presentabilityScore",
          label: "Präsentation",
          explanation:
            "Bewertung der Präsentationsfähigkeiten und des Auftretens des Trainers bei der Vermittlung des Inhalts.",
        },
        {
          key: "interactivityScore",
          label: "Interaktivität",
          explanation:
            "Bewertung des Engagements und der aktiven Einbindung der Teilnehmer durch den Trainer im Workshop.",
        },
        {
          key: "timeManagementScore",
          label: "Zeitmanagement",
          explanation:
            "Bewertung, wie gut der Trainer den Workshop zeitlich strukturiert und die Zeit effizient genutzt hat.",
        },
      ],
      participationFields: [
        {
          key: "participationScore",
          label: "Teilnahme",
          explanation:
            "Bewertung Ihrer eigenen Beteiligung und Aktivität während des Workshops.",
        },
        {
          key: "atmosphereScore",
          label: "Atmosphäre",
          explanation:
            "Bewertung der allgemeinen Stimmung und des Arbeitsklimas während des Workshops.",
        },
        {
          key: "networkingScore",
          label: "Networking",
          explanation:
            "Bewertung der Möglichkeiten, während des Workshops wertvolle Kontakte zu knüpfen und Netzwerke aufzubauen.",
        },
      ],
      organisationFields: [
        {
          key: "equipmentScore",
          label: "Ausrüstung",
          explanation:
            "Bewertung der Qualität und der Angemessenheit der bereitgestellten technischen Ausstattung und Materialien.",
        },
        {
          key: "comfortabilityScore",
          label: "Komfort",
          explanation:
            "Bewertung des Komforts und der allgemeinen Umstände, wie z. B. Sitzgelegenheiten und Raumklima, während des Workshops.",
        },
        {
          key: "communicationScore",
          label: "Kommunikation",
          explanation:
            "Bewertung der Kommunikation und der bereitgestellten Informationen vor und während des Workshops.",
        },
      ],
      commentFields: [
        {
          key: "enjoymentComment",
          label: "Was hat Ihnen besonders gefallen?",
          maxLength: 500,
          explanation:
            "Platz für Ihre Kommentare zu den Aspekten des Workshops, die Ihnen besonders positiv in Erinnerung geblieben sind.",
        },
        {
          key: "improvementComment",
          label: "Welche Aspekte könnten verbessert werden?",
          maxLength: 500,
          explanation:
            "Platz für Ihre Anregungen zu möglichen Verbesserungen und Optimierungen des Workshops.",
        },
        {
          key: "requestComment",
          label: "Haben Sie Anregungen für zukünftige Events?",
          maxLength: 500,
          explanation:
            "Platz für Ihre Vorschläge und Wünsche bezüglich der Gestaltung zukünftiger Workshops oder Veranstaltungen.",
        },
      ],
      recommendationFields: [
        {
          key: "personalImprovementComment",
          label: "Was haben Sie persönlich gelernt?",
          maxLength: 500,
          explanation:
            "Platz für Ihre Kommentare dazu, welche neuen Erkenntnisse oder Fähigkeiten Sie aus dem Workshop gewonnen haben.",
        },
        {
          key: "isEventRecommended",
          label: "Würden Sie diesen Workshop empfehlen?",
          explanation:
            "Frage, ob Sie den Workshop aufgrund Ihrer Erfahrungen an andere Personen weiterempfehlen würden.",
        },
        {
          key: "recommendationComment",
          label: "Warum oder warum nicht?",
          maxLength: 500,
          explanation:
            "Platz für Ihre Begründung, warum Sie den Workshop weiterempfehlen würden oder nicht.",
        },
        {
          key: "similarEventParticipationScore",
          label: "Teilnahme an ähnlichen Workshops in der Zukunft?",
          explanation:
            "Bewertung, wie wahrscheinlich es ist, dass Sie an ähnlichen Workshops oder Veranstaltungen in der Zukunft teilnehmen würden.",
        },
      ],
      feedbackData: {
        overallScore: null,
        organisationalScore: null,
        relevanceScore: null,

        understandabilityScore: null,
        contentDepthScore: null,
        practicalityScore: null,
        reasonabilityScore: null,

        competencyScore: null,
        presentabilityScore: null,
        interactivityScore: null,
        timeManagementScore: null,

        participationScore: null,
        atmosphereScore: null,
        networkingScore: null,

        equipmentScore: null,
        comfortabilityScore: null,
        communicationScore: null,

        enjoymentComment: "",
        improvementComment: "",
        requestComment: "",

        personalImprovementComment: "",
        isEventRecommended: null,
        recommendationComment: "",

        similarEventParticipationScore: null,

        anonymousFeedback: false,
      },
      userId: null,
      router: useRouter(),
      eventId: "",
      event: {
        name: "",
        date: null,
      },
      organizer: {
        firstname: "",
        lastname: "",
      },
      token: "",
      isSubmitted: false,
      errors: {
        overallScore: null,
        organisationalScore: null,
        relevanceScore: null,

        understandabilityScore: null,
        contentDepthScore: null,
        practicalityScore: null,
        reasonabilityScore: null,

        competencyScore: null,
        presentabilityScore: null,
        interactivityScore: null,
        timeManagementScore: null,

        participationScore: null,
        atmosphereScore: null,
        networkingScore: null,

        equipmentScore: null,
        comfortabilityScore: null,
        communicationScore: null,

        enjoymentComment: "",
        improvementComment: "",

        isEventRecommended: null,

        similarEventParticipationScore: null,
      },
    };
  },
  methods: {
    goBack() {
      this.router.push({
        name: "EventPage",
        params: { eventId: this.eventId.toString() },
      });
    },
    /**
     * Submit the feedback form
     */
    async submitFeedback() {
      this.isSubmitted = true;

      if (!this.userId) {
        showToast(
          new Toast(
            "Info",
            `Bitte loggen Sie sich zu Erst ein`,
            "info",
            faXmark,
            10,
          ),
        );
        return;
      }

      if (!this.eventId || !this.token) {
        showToast(
          new Toast(
            "Error",
            `Ungültige Daten. Bitte scannen sie den QR-Code erneut`,
            "error",
            faXmark,
            10,
          ),
        );
        return;
      }

      if (!this.validateFields()) {
        showToast(
          new Toast(
            "Info",
            `Bitte füllen Sie alle Felder aus`,
            "info",
            faXmark,
            10,
          ),
        );
        this.scrollToFirstError();
        return;
      }

      this.feedbackData["eventId"] = this.eventId;
      this.feedbackData["userId"] = this.userId;

      try {
        const feedbackResponse = await fetch(`${config.apiBaseUrl}/feedback`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(this.feedbackData),
        });

        if (!feedbackResponse.ok) {
          showToast(
            new Toast(
              "Error",
              `Fehler beim Senden des Feedbacks`,
              "error",
              faXmark,
              10,
            ),
          );
          return;
        }

        showToast(
          new Toast(
            "Success",
            `Feedback erfolgreich übermittelt und Anwesenheit bestätigt!t`,
            "success",
            faCheck,
            5,
          ),
        );
        this.$router.push("/home");
      } catch (error) {
        showToast(
          new Toast(
            "Error",
            `Fehler beim Senden des Feedbacks`,
            "error",
            faXmark,
            10,
          ),
        );
      }
    },
    updateField(fieldKey, value) {
      this.feedbackData[fieldKey] = value;

      if (this.errors[fieldKey]) {
        delete this.errors[fieldKey];
      }
    },
    validateFields() {
      this.errors = {};

      if (this.isSubmitted) {
        this.overallFields.forEach((field) => {
          if (!this.feedbackData[field.key]) {
            this.errors[field.key] = "Dieses Feld ist erforderlich.";
          }
        });

        this.contentFields.forEach((field) => {
          if (!this.feedbackData[field.key]) {
            this.errors[field.key] = "Dieses Feld ist erforderlich.";
          }
        });

        this.trainerFields.forEach((field) => {
          if (!this.feedbackData[field.key]) {
            this.errors[field.key] = "Dieses Feld ist erforderlich.";
          }
        });

        this.organisationFields.forEach((field) => {
          if (!this.feedbackData[field.key]) {
            this.errors[field.key] = "Dieses Feld ist erforderlich.";
          }
        });

        this.participationFields.forEach((field) => {
          if (!this.feedbackData[field.key]) {
            this.errors[field.key] = "Dieses Feld ist erforderlich.";
          }
        });

        this.commentFields.slice(0, 2).forEach((field) => {
          if (
            !this.feedbackData[field.key] ||
            !this.feedbackData[field.key].trim()
          ) {
            this.errors[field.key] = "Dieses Feld darf nicht leer sein.";
          }
        });

        if (this.feedbackData.isEventRecommended === null) {
          this.errors.isEventRecommended =
            "Bitte geben Sie an, ob Sie das Event empfehlen.";
        }

        if (!this.feedbackData.similarEventParticipationScore) {
          this.errors.similarEventParticipationScore =
            "Dieses Feld ist erforderlich.";
        }
      }

      return Object.keys(this.errors).length === 0;
    },
    /**
     * Scroll to the first error field
     */
    scrollToFirstError() {
      this.$nextTick(() => {
        const firstErrorField = Object.keys(this.errors)[0];
        if (firstErrorField) {
          const errorElement = this.$refs[`field-${firstErrorField}`][0];
          showToast(new Toast("Error", errorElement, "error", faXmark, 10));
          if (errorElement) {
            errorElement.scrollIntoView({
              behavior: "smooth",
              block: "center",
            });
          } else {
            showToast(
              new Toast("Error", firstErrorField, "error", faXmark, 10),
            );
          }
        }
      });
    },
    /**
     * Fetch the event details and organizer information
     */
    async confirmAttendance() {
      try {
        const attendanceResponse = await fetch(
          `${config.apiBaseUrl}/events/${this.eventId}/attendance`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              token: this.token,
              userId: this.userId,
            }),
          },
        );

        if (!attendanceResponse.ok) {
          const errorData = await attendanceResponse.json();
          if (
            attendanceResponse.status === 400 &&
            errorData.message === "User has already given Feedback."
          ) {
            showToast(
              new Toast(
                "Error",
                `Sie haben bereits Feedback für dieses Event abgegeben. \n Sie werden nun zum Home-Screen weitergeleitet.`,
                "error",
                faXmark,
                10,
              ),
            );
            this.$router.push({ name: "home" });
            return;
          }
          showToast(
            new Toast(
              "Error",
              `Fehler beim Bestätigen der Anwesenheit`,
              "error",
              faXmark,
              10,
            ),
          );
          return;
        }
        showToast(
          new Toast(
            "Success",
            `Anwesenheit wurde erfolgreich bestätigt`,
            "success",
            faCheck,
            5,
          ),
        );
      } catch (error) {
        showToast(
          new Toast(
            "Error",
            `Fehler beim Bestätigen der Anwesenheit`,
            "error",
            faXmark,
            10,
          ),
        );
      }
    },
    /**
     * Fetch the event details and organizer information
     */
    async fetchEventDetails() {
      try {
        const response = await fetch(
          `${config.apiBaseUrl}/events/${this.eventId}`,
        );
        const eventData = await response.json();
        this.event = eventData;

        const organizerResponse = await fetch(
          `${config.apiBaseUrl}/events/${this.eventId}/organizer`,
        );
        const organizerData = await organizerResponse.json();
        this.organizer = organizerData;
      } catch (error) {
        showToast(
          new Toast(
            "Error",
            `Fehler beim laden der Eventdaten`,
            "error",
            faXmark,
            10,
          ),
        );
      }
    },
  },
  /**
   * Fetch the event details and organizer information
   */
  mounted() {
    const storedUserId = Cookies.get("userId");
    if (storedUserId) {
      this.userId = parseInt(storedUserId, 10);
    }

    if (this.userId) {
    } else {
      showToast(
        new Toast("Info", `benutzer ist nicht eingeloggt`, "info", faXmark, 10),
      );
    }

    const route = useRoute();
    this.eventId = route.params.eventId as string;
    this.token = route.query.token as string;

    this.fetchEventDetails();

    this.confirmAttendance();
  },
};
</script>

<style scoped>
.back-button {
  position: fixed;
  top: 5rem;
  left: 5rem;
  background-color: #009ee2;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s;
}

.back-button:hover {
  background-color: #007bb5;
}

.scrollable-container {
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  height: 100%;
  padding: 2rem;
  background-color: #f5f5f5;
  box-sizing: border-box;
}

.survey-header {
  margin-bottom: 2rem;
  text-align: center;
}

.survey-header h2 {
  font-size: 2.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 2rem;
}

.survey-header p {
  font-size: 1rem;
  color: #555;
  margin-bottom: 1rem;
}

.survey-instructions {
  background-color: rgb(255, 255, 255);
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  font-size: 1rem;
  color: #555;
  line-height: 1.6;
  max-width: 800px;
  margin: 0 auto;
  text-align: left;
}

.survey-instructions p {
  margin-bottom: 1rem;
}

.survey-instructions strong {
  font-weight: bold;
  color: #333;
}

.required {
  color: red;
  font-weight: bold;
}

.feedback-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
  background-color: rgb(255, 255, 255);
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  min-width: 600px;
  max-width: 800px;
  width: 100%;
  box-sizing: border-box;
  flex-grow: 2;
}

.section-title {
  text-align: center;
  margin-bottom: 1rem;
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}

.feedback-row {
  display: flex;
  justify-content: flex-start;
  align-items: left;
  margin-bottom: 1rem;
}

.feedback-label {
  text-align: left;
  font-size: 1rem;
  color: #555;
  flex: 1 0 auto;
  min-width: 50px;
  max-width: 500px;
  white-space: nowrap;
}

.explanation {
  font-size: 0.8em;
  color: #666;
  margin-top: 5px;
  line-height: 1.6;
  word-wrap: break-word;
  white-space: pre-line;
  word-break: break-word;
  max-width: 330px;
}

.rating-group {
  display: flex;
  justify-content: flex-start;
  gap: 1rem;
  align-items: center;
}

.rating-circle {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #eaeaea;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.rating-circle.selected {
  background-color: #009ee2;
  color: white;
}

.feedback-input {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.feedback-input textarea {
  width: 100%;
  max-width: 400px;
  min-height: 3rem;
  padding: 0.5rem;
  resize: none;
  border-radius: 4px;
  border: 1px solid #ccc;
  font-size: 12pt;
  background-color: #fff;
}

hr {
  margin: auto;
  border: none;
  border-top: 1px solid #8d8d8d;
}

.boolean-options {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.error-message {
  position: absolute;
  left: 50%;
  transform: translateX(-25%);
  background-color: #ffcccc;
  color: #990000;
  padding: 8px 12px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  font-size: 14px;
  white-space: nowrap;
  z-index: 1000;
  pointer-events: none;
}

.comment-error {
  left: 20%;
  top: 70%;
  position: absolute;
  transform: translateX(-25%);
  background-color: #ffcccc;
  color: #990000;
  padding: 8px 12px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  font-size: 14px;
  white-space: nowrap;
  z-index: 1000;
  pointer-events: none;
}

.error-message::after {
  content: "";
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  border-width: 8px;
  border-style: solid;
  border-color: #ffcccc transparent transparent transparent;
}

.submit-section {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 2rem;
  margin-top: auto;
}

.submit-button {
  padding: 0.8rem 1.2rem;
  font-size: 1rem;
  color: white;
  background-color: #009ee2;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-button:hover {
  background-color: #007bb5;
}

.anonymous-feedback label {
  display: flex;
  align-items: normal;
  font-size: 1rem;
  color: #555;
  cursor: pointer;
}

.anonymous-feedback input {
  margin-right: 1rem;
}
</style>
