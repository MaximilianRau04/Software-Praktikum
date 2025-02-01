<template>
  <div>
    <button class="back-button" @click="goBack">Zurück</button>
  </div>

  <div class="scrollable-container">
    <!-- Überschrift und Beschreibung -->
    <div class="survey-header">
      <h2>Feedback für {{ event.name }}</h2>
      <p>Veranstaltung vom {{ new Date(event.date).toLocaleDateString('de-DE', {
        weekday: 'long', year: 'numeric',
        month: 'long', day: 'numeric'
      }) }} <br /></p>
      <div class="survey-instructions">
        <p>
          Wir bitten Sie, diese Umfrage auszufüllen, um wertvolles Feedback zu Ihrem Erlebnis bei <strong>{{ event.name
            }}</strong> mit <strong>{{ organizer.firstname + " " + organizer.lastname }}</strong> zu erhalten. Ihre
          Bewertungen und Kommentare tragen dazu bei, unsere zukünftigen Veranstaltungen kontinuierlich zu verbessern.
        </p>
        <p>
          Bitte bewerten Sie die verschiedenen Bereiche der Veranstaltung auf einer Skala von 1 bis 5, wobei 5 die
          bestmögliche Bewertung und 1 die schlechteste Bewertung darstellt.
        </p>
        <p>
          Wir laden Sie ein, Ihre Gedanken zu den Fragen „Was hat Ihnen besonders gefallen?“ und „Welche Aspekte könnten
          verbessert werden?“ zu teilen. Ihre detaillierten Rückmeldungen sind für uns von großem Wert.
        </p>
        <p>
          Erforderliche Felder sind mit einem <span class="required">*</span> markiert.
        </p>
        <p>
          Sie haben die Möglichkeit, Ihr Feedback anonym abzugeben. Diese Option finden Sie am Ende der Umfrage.
        </p>
        <p>
          Die Bearbeitung der Umfrage dauert etwa 5-7 Minuten.
        </p>
      </div>
    </div>

    <form class="feedback-form" @submit.prevent="submitFeedback">
      <!-- Overall Section -->
      <div class="section">
        <h3 class="section-title">Gesamtbewertung</h3>
        <div v-for="(field, index) in overallFields" :key="index" class="feedback-row" style="position: relative;"
          :ref="`field-${field.key}`">
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="updateField(field.key, rating)">
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
        <div v-for="(field, index) in contentFields" :key="index" class="feedback-row" style="position: relative;"
          :ref="`field-${field.key}`">
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="updateField(field.key, rating)">
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
        <div v-for="(field, index) in trainerFields" :key="index" class="feedback-row" style="position: relative;"
          :ref="`field-${field.key}`">
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="updateField(field.key, rating)">
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
        <div v-for="(field, index) in participationFields" :key="index" class="feedback-row" style="position: relative;"
          :ref="`field-${field.key}`">
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="updateField(field.key, rating)">
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
        <div v-for="(field, index) in organisationFields" :key="index" class="feedback-row" style="position: relative;"
          :ref="`field-${field.key}`">
          <div class="feedback-label">
            <span>{{ field.label }} <span class="required">*</span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="updateField(field.key, rating)">
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
        <div v-for="(field, index) in commentFields" :key="index" class="feedback-row" style="position: relative;"
          :ref="`field-${field.key}`">
          <div class="feedback-label">
            <span>{{ field.label }} <span
                v-if="field.key === 'enjoymentComment' || field.key === 'improvementComment'"><span
                  class="required">*</span></span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>
          <div class="feedback-input">
            <textarea :name="field.key" v-model="feedbackData[field.key]" :maxlength="field.maxLength"
              rows="3"></textarea>
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
        <div v-for="(field, index) in recommendationFields" :key="index" class="feedback-row"
          style="position: relative;" :ref="`field-${field.key}`">
          <div class="feedback-label">
            <span>{{ field.label }} <span
                v-if="field.key === 'isEventRecommended' || field.key === 'similarEventParticipationScore'"><span
                  class="required">*</span></span></span>
            <p class="explanation">{{ field.explanation }}</p>
            <!-- Explanation below the label -->
          </div>

          <div class="feedback-input">
            <!-- Textarea for comments -->
            <textarea v-if="field.maxLength" :name="field.key" v-model="feedbackData[field.key]"
              :maxLength="field.maxLength" rows="3"></textarea>

            <!-- Radio buttons for boolean (isEventRecommended) -->
            <div v-else-if="field.key === 'isEventRecommended'" class="boolean-options">
              <label>
                <input type="radio" :name="field.key" value="true" v-model="feedbackData[field.key]" />
                Ja
              </label>
              <label>
                <input type="radio" :name="field.key" value="false" v-model="feedbackData[field.key]" />
                Nein
              </label>
              <div v-if="errors.isEventRecommended" class="error-message">
                {{ errors.isEventRecommended }}
              </div>
            </div>

            <div v-else-if="field.key === 'similarEventParticipationScore'">
              <div class="rating-group">
                <div v-for="rating in 5" :key="rating" class="rating-circle"
                  :class="{ selected: feedbackData[field.key] === rating }" @click="updateField(field.key, rating)">
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
import { useRoute, useRouter } from "vue-router";
import { showToast, Toast } from "@/types/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import api from "@/util/api";
import { useAuth } from "@/util/auth";

export default {
  data() {
    return {
      overallFields: [
        {
          key: "overallScore",
          label: "Gesamteindruck",
          explanation: "Bewertung des Gesamtbildes und des Eindrucks des Workshops aus Ihrer Sicht.",
        },
        {
          key: "organisationalScore",
          label: "Organisation",
          explanation: "Bewertung der organisatorischen Abläufe und der logistischen Vorbereitung des Workshops.",
        },
        {
          key: "relevanceScore",
          label: "Relevanz",
          explanation: "Bewertung, inwieweit der Inhalt des Workshops für die Teilnehmer relevant und nützlich war.",
        },
      ],
      contentFields: [
        {
          key: "understandabilityScore",
          label: "Verständlichkeit",
          explanation: "Bewertung der Klarheit und Nachvollziehbarkeit des vermittelten Inhalts während des Workshops.",
        },
        {
          key: "contentDepthScore",
          label: "Inhaltliche Tiefe",
          explanation: "Bewertung, wie tiefgehend und fundiert der vermittelte Inhalt des Workshops war.",
        },
        {
          key: "practicalityScore",
          label: "Praktikabilität",
          explanation: "Bewertung der praktischen Anwendbarkeit und Nutzbarkeit des vermittelten Wissens im Arbeitsalltag.",
        },
        {
          key: "reasonabilityScore",
          label: "Nachvollziehbarkeit",
          explanation: "Bewertung, wie gut der Inhalt des Workshops logisch und verständlich aufbereitet war.",
        },
      ],
      trainerFields: [
        {
          key: "competencyScore",
          label: "Kompetenz",
          explanation: "Bewertung der fachlichen Qualifikationen und Expertise des Trainers während des Workshops.",
        },
        {
          key: "presentabilityScore",
          label: "Präsentation",
          explanation: "Bewertung der Präsentationsfähigkeiten und des Auftretens des Trainers bei der Vermittlung des Inhalts.",
        },
        {
          key: "interactivityScore",
          label: "Interaktivität",
          explanation: "Bewertung des Engagements und der aktiven Einbindung der Teilnehmer durch den Trainer im Workshop.",
        },
        {
          key: "timeManagementScore",
          label: "Zeitmanagement",
          explanation: "Bewertung, wie gut der Trainer den Workshop zeitlich strukturiert und die Zeit effizient genutzt hat.",
        },
      ],
      participationFields: [
        {
          key: "participationScore",
          label: "Teilnahme",
          explanation: "Bewertung Ihrer eigenen Beteiligung und Aktivität während des Workshops.",
        },
        {
          key: "atmosphereScore",
          label: "Atmosphäre",
          explanation: "Bewertung der allgemeinen Stimmung und des Arbeitsklimas während des Workshops.",
        },
        {
          key: "networkingScore",
          label: "Networking",
          explanation: "Bewertung der Möglichkeiten, während des Workshops wertvolle Kontakte zu knüpfen und Netzwerke aufzubauen.",
        },
      ],
      organisationFields: [
        {
          key: "equipmentScore",
          label: "Ausrüstung",
          explanation: "Bewertung der Qualität und der Angemessenheit der bereitgestellten technischen Ausstattung und Materialien.",
        },
        {
          key: "comfortabilityScore",
          label: "Komfort",
          explanation: "Bewertung des Komforts und der allgemeinen Umstände, wie z. B. Sitzgelegenheiten und Raumklima, während des Workshops.",
        },
        {
          key: "communicationScore",
          label: "Kommunikation",
          explanation: "Bewertung der Kommunikation und der bereitgestellten Informationen vor und während des Workshops.",
        },
      ],
      commentFields: [
        {
          key: "enjoymentComment",
          label: "Was hat Ihnen besonders gefallen?",
          maxLength: 500,
          explanation: "Platz für Ihre Kommentare zu den Aspekten des Workshops, die Ihnen besonders positiv in Erinnerung geblieben sind.",
        },
        {
          key: "improvementComment",
          label: "Welche Aspekte könnten verbessert werden?",
          maxLength: 500,
          explanation: "Platz für Ihre Anregungen zu möglichen Verbesserungen und Optimierungen des Workshops.",
        },
        {
          key: "requestComment",
          label: "Haben Sie Anregungen für zukünftige Events?",
          maxLength: 500,
          explanation: "Platz für Ihre Vorschläge und Wünsche bezüglich der Gestaltung zukünftiger Workshops oder Veranstaltungen.",
        },
      ],
      recommendationFields: [
        {
          key: "personalImprovementComment",
          label: "Was haben Sie persönlich gelernt?",
          maxLength: 500,
          explanation: "Platz für Ihre Kommentare dazu, welche neuen Erkenntnisse oder Fähigkeiten Sie aus dem Workshop gewonnen haben.",
        },
        {
          key: "isEventRecommended",
          label: "Würden Sie diesen Workshop empfehlen?",
          explanation: "Frage, ob Sie den Workshop aufgrund Ihrer Erfahrungen an andere Personen weiterempfehlen würden.",
        },
        {
          key: "recommendationComment",
          label: "Warum oder warum nicht?",
          maxLength: 500,
          explanation: "Platz für Ihre Begründung, warum Sie den Workshop weiterempfehlen würden oder nicht.",
        },
        {
          key: "similarEventParticipationScore",
          label: "Teilnahme an ähnlichen Workshops in der Zukunft?",
          explanation: "Bewertung, wie wahrscheinlich es ist, dass Sie an ähnlichen Workshops oder Veranstaltungen in der Zukunft teilnehmen würden.",
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
      userId: useAuth().getUserId(),
      router: useRouter(),
      auth: useAuth(),
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
    async submitFeedback() {
      this.isSubmitted = true;

      if (!this.userId) {
        alert("Please login first.");
        return;
      }
      console.log(this.eventId + " eventid")
      console.log(this.token + " token")
      if (!this.eventId || !this.token) {
        showToast(
          new Toast(
            "Fehler",
            "Ungültiger QR-Code, bitte scannen Sie erneut.",
            "error",
            faXmark,
            5
          )
        );
        return;
      }

      if (!this.validateFields()) {
        showToast(
          new Toast(
            "Warnung",
            "Bitte füllen Sie alle erforderlichen Felder aus.",
            "warning",
            faXmark,
            5
          )
        );
        this.scrollToFirstError();
        return;
      }

      this.feedbackData["eventId"] = this.eventId;
      this.feedbackData["userId"] = this.userId;

      try {
        const response = await api.post(`/feedback`, this.feedbackData);

        if (response.status !== 201) {
          showToast(
            new Toast(
              "Fehler",
              "Feedback konnte nicht abgeschickt werden.",
              "error",
              faXmark,
              5
            )
          );
          return;
        }

        showToast(
          new Toast(
            "Erfolg",
            "Ihr Feedback wurde erfolgreich abgesendet.",
            "success",
            faXmark,
            5
          )
        );
        this.$router.push("/home");
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            "Ein Fehler ist aufgetreten während der Feedbackerstellung.",
            "error",
            faXmark,
            5
          )
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
          if (!this.feedbackData[field.key] || !this.feedbackData[field.key].trim()) {
            this.errors[field.key] = "Dieses Feld darf nicht leer sein.";
          }
        });

        if (this.feedbackData.isEventRecommended === null) {
          this.errors.isEventRecommended = "Bitte geben Sie an, ob Sie das Event empfehlen.";
        }

        if (!this.feedbackData.similarEventParticipationScore) {
          this.errors.similarEventParticipationScore = "Dieses Feld ist erforderlich.";
        }
      }

      return Object.keys(this.errors).length === 0;
    },
    scrollToFirstError() {
      this.$nextTick(() => {
        const firstErrorField = Object.keys(this.errors)[0];
        if (firstErrorField) {
          const errorElement = this.$refs[`field-${firstErrorField}`][0];
          if (errorElement) {
            errorElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }
        }
      });
    },
    async confirmAttendance() {

      const body = {
        userId: useAuth().getUserId(),
        token: this.$route.query.token
      }

      try {
        const response = await api.post(`/events/${this.eventId}/attendance`, body);

        const data = await response.data;

        if (response.status === 409) {
          this.router.push('/home');
          return;
        }

        if (response.status !== 200) throw new Error(data.message);

      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            "Anwesenheitsprüfung ist fehlgeschlagen.",
            "error",
            faXmark,
            5
          )
        );
        this.router.push('/home');
      }
    },

    async fetchEventDetails() {
      try {
        const response = await api.get(`/events/${this.eventId}`);
        const eventData = await response.data;
        this.event = eventData;

        const organizerResponse = await api.get(`/events/${this.eventId}/organizer`);
        const organizerData = await organizerResponse.data;
        this.organizer = organizerData;
      } catch (error) {
        showToast(
          new Toast(
            "Fehler",
            "Event Details konnten nicht geladen werden.",
            "error",
            faXmark,
            5
          )
        );
      }
    },

    async verifyFeedbackEligibility() {
      try {
        const response = await api.get(`/events/${this.eventId}/feedback-eligibility`);

        if (response.status !== 200) {
          if (response.status === 403) {
            showToast(
              new Toast(
                "Zugriff verweigert",
                "Sie sind nicht berechtigt Feedback für dieses Event zu geben",
                "error",
                faXmark,
                5
              )
            );
            this.router.push('/home');
          }
          throw new Error('Authorization failed');
        }
      } catch (error) {
        showToast(
          new Toast(
            "Zugriff verweigert",
            "Sie sind nicht eingeloggt.",
            "error",
            faXmark,
            5
          )
        );
        this.router.push('/login');
      }
    },

  },
  async mounted() {
    if (!this.auth.isAuthenticated) {
      this.router.replace({
        name: 'login',
        query: { redirect: this.$route.fullPath }
      });
      return;
    }

    const route = useRoute();

    this.eventId = route.params.eventId as string;
    this.token = this.$route.query.token;

    await this.verifyFeedbackEligibility();
    await this.fetchEventDetails();
    await this.confirmAttendance();
  },
};
</script>

<style scoped>
.back-button {
  position: absolute;
  top: 5rem;
  left: 5rem;
  background-color: #009ee2;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  z-index: 100;
}

.back-button:hover {
  background-color: #007bb5;
  transform: translateY(-1px);
}

.scrollable-container {
  padding: 4rem 1rem 2rem;
  background-color: #f8f9ff;
  min-height: 100vh;
  margin: 0 auto;
}

.survey-header {
  margin-bottom: 2rem;
  padding: 0 1rem;
  max-width: 900px;
  margin: 2rem auto;
}

.survey-header h2 {
  font-size: 1.75rem;
  color: #01172F;
  margin-bottom: 1rem;
}

.survey-instructions {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  font-size: 1rem;
  line-height: 1.6;
}

.feedback-form {
  background: white;
  padding: 2rem 1rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  max-width: 800px;
  margin: 0 auto;
}

.section {
  margin-bottom: 2rem;
}

.section-title {
  color: #01172F;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #eee;
}

.feedback-row {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
  position: relative;
}

@media (min-width: 768px) {
  .feedback-row {
    flex-direction: row;
    align-items: flex-start;
  }
}

.feedback-label {
  flex: 1;
  min-width: 250px;
}

.explanation {
  color: #666;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.rating-group {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.rating-circle {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e3f2fd;
  color: #01172F;
  cursor: pointer;
  transition: all 0.2s ease;
}

.rating-circle.selected {
  background: #009ee2;
  color: white;
}

.feedback-input textarea {
  width: 85%;
  padding: 1rem;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s ease;
}

.feedback-input textarea:focus {
  border-color: #009ee2;
  outline: none;
}

.error-message,
.comment-error {
  color: #dc3545;
  font-size: 0.9rem;
  margin-top: 0.5rem;
  position: static;
  transform: none;
  background: none;
  box-shadow: none;
  padding: 0;
}

.submit-section {
  text-align: center;
  margin-top: 2rem;
}

.submit-button {
  background: #009ee2;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.submit-button:hover {
  background: #007bb5;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .back-button {
    top: 5rem;
    left: 1.7rem;
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }

  .survey-header h2 {
    font-size: 1.5rem;
  }

  .section-title {
    font-size: 1.25rem;
  }

  .rating-circle {
    width: 2.2rem;
    height: 2.2rem;
    font-size: 0.9rem;
  }

  .feedback-form {
    padding: 1.5rem 0.5rem;
    min-width: auto;
  }

  .feedback-label {
    font-size: 0.95rem;
  }
}

input[type="checkbox"] {
  accent-color: #009ee2;
  margin-right: 0.5rem;
}

.required {
  color: #dc3545;
  margin-left: 0.25rem;
}
</style>