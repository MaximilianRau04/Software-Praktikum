<template>
  <div class="scrollable-container">
    <form class="feedback-form" @submit.prevent="submitFeedback">
      <!-- Overall Section -->
      <div class="section">
        <h3 class="section-title">Gesamtbewertung</h3>
        <div v-for="(field, index) in overallFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
            <p class="explanation">{{ field.explanation }}</p> <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="feedbackData[field.key] = rating">
              {{ rating }}
            </div>
          </div>
        </div>
        <hr />
      </div>

      <!-- Content and Structure Section -->
      <div class="section">
        <h3 class="section-title">Inhalt und Struktur</h3>
        <div v-for="(field, index) in contentFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
            <p class="explanation">{{ field.explanation }}</p> <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="feedbackData[field.key] = rating">
              {{ rating }}
            </div>
          </div>
        </div>
        <hr />
      </div>

      <!-- Trainer Section -->
      <div class="section">
        <h3 class="section-title">Trainer</h3>
        <div v-for="(field, index) in trainerFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
            <p class="explanation">{{ field.explanation }}</p> <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="feedbackData[field.key] = rating">
              {{ rating }}
            </div>
          </div>
        </div>
        <hr />
      </div>

      <!-- Participation Section -->
      <div class="section">
        <h3 class="section-title">Teilnahme</h3>
        <div v-for="(field, index) in participationFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
            <p class="explanation">{{ field.explanation }}</p> <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="feedbackData[field.key] = rating">
              {{ rating }}
            </div>
          </div>
        </div>
        <hr />
      </div>

      <!-- Equipment and Organisation Section -->
      <div class="section">
        <h3 class="section-title">Ausrüstung und Organisation</h3>
        <div v-for="(field, index) in organisationFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
            <p class="explanation">{{ field.explanation }}</p> <!-- Explanation below the label -->
          </div>
          <div class="rating-group">
            <div v-for="rating in 5" :key="rating" class="rating-circle"
              :class="{ selected: feedbackData[field.key] === rating }" @click="feedbackData[field.key] = rating">
              {{ rating }}
            </div>
          </div>
        </div>
        <hr />
      </div>

      <!-- Comments Section -->
      <div class="section">
        <h3 class="section-title">Kommentare</h3>
        <div v-for="(field, index) in commentFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
            <p class="explanation">{{ field.explanation }}</p> <!-- Explanation below the label -->
          </div>
          <div class="feedback-input">
            <textarea :name="field.key" v-model="feedbackData[field.key]" :maxlength="field.maxlength"
              rows="3"></textarea>
          </div>
        </div>
      </div>

      <!-- Recommendation Section -->
      <div class="section">
        <h3 class="section-title">Empfehlungen</h3>
        <div v-for="(field, index) in recommendationFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
            <p class="explanation">{{ field.explanation }}</p> <!-- Explanation below the label -->
          </div>

          <div class="feedback-input">
            <!-- Textarea for comments -->
            <textarea v-if="field.maxlength" :name="field.key" v-model="feedbackData[field.key]"
              :maxlength="field.maxlength" rows="3"></textarea>

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
            </div>
            <div v-else-if="field.key === 'similarEventParticipationScore'">
              <div class="rating-group">
                <div v-for="rating in 5" :key="rating" class="rating-circle"
                  :class="{ selected: feedbackData[field.key] === rating }" @click="feedbackData[field.key] = rating">
                  {{ rating }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- Submit Button -->
      <div class="section submit-section">
        <div class="anonymous-feedback">
        </div>
        <button type="submit" class="submit-button">Feedback absenden</button>
        <label>
          <input type="checkbox" v-model="feedbackData.anonymousFeedback" />
          Anonym absenden?
        </label>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import config from '@/config';
import Cookies from 'js-cookie';

const overallFields = [
  { key: 'overallScore', label: 'Gesamteindruck', explanation: 'Bewertung des allgemeinen Eindrucks des Workshops.' },
  { key: 'organisationalScore', label: 'Organisation', explanation: 'Bewertung der Organisation und Logistik des Workshops.' },
  { key: 'relevanceScore', label: 'Relevanz', explanation: 'Bewertung der Relevanz des Inhalts des Workshops für die Teilnehmer.' },
];

const contentFields = [
  { key: 'understandabilityScore', label: 'Verständlichkeit', explanation: 'Bewertung der Verständlichkeit des präsentierten Inhalts.' },
  { key: 'contentDepthScore', label: 'Inhaltliche Tiefe', explanation: 'Bewertung der Tiefe und Detailgenauigkeit des vermittelten Inhalts.' },
  { key: 'practicalityScore', label: 'Praktikabilität', explanation: 'Bewertung der Praktikabilität und Anwendbarkeit des Inhalts im Arbeitsalltag.' },
  { key: 'reasonabilityScore', label: 'Nachvollziehbarkeit', explanation: 'Bewertung, ob der Inhalt logisch und nachvollziehbar war.' },
];

const trainerFields = [
  { key: 'competencyScore', label: 'Kompetenz', explanation: 'Bewertung der Fachkenntnisse des Trainers.' },
  { key: 'presentabilityScore', label: 'Präsentation', explanation: 'Bewertung der Präsentationsfähigkeiten und des Auftretens des Trainers.' },
  { key: 'interactivityScore', label: 'Interaktivität', explanation: 'Bewertung, wie interaktiv der Workshop war und wie gut der Trainer die Teilnehmer eingebunden hat.' },
  { key: 'timeManagementScore', label: 'Zeitmanagement', explanation: 'Bewertung des Zeitmanagements und der Strukturierung des Workshops.' },
];

const participationFields = [
  { key: 'participationScore', label: 'Teilnahme', explanation: 'Bewertung der eigenen Beteiligung und Aktivität während des Workshops.' },
  { key: 'atmosphereScore', label: 'Atmosphäre', explanation: 'Bewertung der allgemeinen Atmosphäre und Stimmung im Workshop.' },
  { key: 'networkingScore', label: 'Networking', explanation: 'Bewertung der Möglichkeit, während des Workshops neue Kontakte zu knüpfen.' },
];

const organisationFields = [
  { key: 'equipmentScore', label: 'Ausrüstung', explanation: 'Bewertung der bereitgestellten Ausrüstung und technischen Mittel.' },
  { key: 'comfortabilityScore', label: 'Komfort', explanation: 'Bewertung des Komforts, z. B. Sitzmöglichkeiten, Raumklima, etc.' },
  { key: 'communicationScore', label: 'Kommunikation', explanation: 'Bewertung der Kommunikation und Informationen vor und während des Workshops.' },
];

const commentFields = [
  { key: 'enjoymentComment', label: 'Was hat Ihnen am meisten gefallen?', maxlength: 200, explanation: 'Platz für Kommentare darüber, was Ihnen am meisten Spaß gemacht hat.' },
  { key: 'improvementComment', label: 'Was könnte verbessert werden?', maxlength: 200, explanation: 'Platz für Kommentare zu möglichen Verbesserungen.' },
  { key: 'requestComment', label: 'Haben Sie Anregungen für zukünftige Events?', maxlength: 200, explanation: 'Platz für Vorschläge oder Wünsche für zukünftige Veranstaltungen.' },
];

const recommendationFields = [
  { key: 'personalImprovementComment', label: 'Was haben Sie persönlich gelernt?', maxlength: 200, explanation: 'Platz für Kommentare darüber, was Sie aus dem Workshop gelernt haben.' },
  { key: 'isEventRecommended', label: 'Würden Sie diesen Workshop empfehlen?', explanation: 'Frage, ob Sie den Workshop anderen empfehlen würden.' },
  { key: 'recommendationComment', label: 'Warum oder warum nicht?', maxlength: 200, explanation: 'Platz für eine Erklärung, warum oder warum nicht empfohlen wird.' },
  { key: 'similarEventParticipationScore', label: 'Wie wahrscheinlich ist es, dass Sie an einem ähnlichen Workshop teilnehmen?', explanation: 'Bewertung, wie wahrscheinlich es ist, dass Sie an einem ähnlichen Event teilnehmen würden.' }
];

const feedbackData = ref<Record<string, any>>({});

const userId = ref<number | null>(null);
const eventId = ref('');
const token = ref('');

const route = useRoute();
const router = useRouter();

/*
 * Submits the feedback data to the server.
**/
const submitFeedback = async () => {
  if (!userId.value) {
    alert("Please login first.");
    return;
  }

  if (!eventId.value || !token.value) {
    alert('Ungültige Daten. Bitte scannen sie den QR-Code erneut.');
    return;
  }

  feedbackData.value['eventId'] = eventId.value;
  feedbackData.value['userId'] = userId.value;

  // TODO: daten validieren

  try {
    const feedbackResponse = await fetch(`${config.apiBaseUrl}/feedback`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(feedbackData.value),
    });

    if (!feedbackResponse.ok) {
      alert('Fehler beim Senden des Feedbacks.');
      // TODO: Ansage von Fehlern oder prompt, um daten anzufügen
      return;
    }

    alert('Feedback erfolgreich übermittelt und Anwesenheit bestätigt!');
    router.push('/home');
  } catch (error) {
    console.error('Error submitting feedback:', error);
    alert('An error occurred while submitting feedback.');
  }
};

const confirmAttendance = async () => {
  try{
    const attendanceResponse = await fetch(`${config.apiBaseUrl}/events/${eventId.value}/attendance`, {
    method: "POST",
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      token: token.value,
      userId: userId.value,
    }),
  });

  if (!attendanceResponse.ok) {
    alert('Fehler bem Bestätigen der Anwesenheit.');
    return;
  }
  }catch(error){
    console.error('Error confirming attendance:', error);
    alert('An error occurred confirming attendance.');
  }
};

/*
 * Fetches the user ID from cookies and the event ID from the route.
**/
onMounted(() => {
  const storedUserId = Cookies.get('userId');
  if (storedUserId) {
    userId.value = parseInt(storedUserId, 10);
  }

  if (userId) {
    console.log('User is logged in with ID:', userId);
  } else {
    console.log('User is not logged in.');
  }

  eventId.value = route.params.eventId as string;
  token.value = route.query.token as string;

  confirmAttendance();
});
</script>


<style scoped>
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
  overflow-y: auto;
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
  align-items: center;
  margin-bottom: 1rem;
}

.feedback-label {
  text-align: left;
  font-size: 1rem;
  color: #555;
  flex: 1 0 auto;
  min-width: 50px;
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
  max-width: 350px;
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
