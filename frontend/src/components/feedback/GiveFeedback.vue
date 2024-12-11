<template>
  <div class="scrollable-container">
    <form class="feedback-form" @submit.prevent="submitFeedback">
      <!-- Overall Section -->
      <div class="section">
        <h3 class="section-title">Overall</h3>
        <div v-for="(field, index) in overallFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
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
        <h3 class="section-title">Content and Structure</h3>
        <div v-for="(field, index) in contentFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
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
        <h3 class="section-title">Participation</h3>
        <div v-for="(field, index) in participationFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
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
        <h3 class="section-title">Equipment and Organisation</h3>
        <div v-for="(field, index) in organisationFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
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
        <h3 class="section-title">Comments</h3>
        <div v-for="(field, index) in commentFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
          </div>
          <div class="feedback-input">
            <textarea :name="field.key" v-model="feedbackData[field.key]" :maxlength="field.maxlength"
              rows="3"></textarea>
          </div>
        </div>
      </div>

      <!-- Recommendation Section -->
      <div class="section">
        <h3 class="section-title">Recommendations</h3>
        <div v-for="(field, index) in recommendationFields" :key="index" class="feedback-row">
          <div class="feedback-label">
            <span>{{ field.label }}</span>
          </div>

          <div class="feedback-input">
            <!-- Textarea for comments -->
            <textarea v-if="field.maxlength" :name="field.key" v-model="feedbackData[field.key]"
              :maxlength="field.maxlength" rows="3"></textarea>

            <!-- Radio buttons for boolean (isEventRecommended) -->
            <div v-else-if="field.key === 'isEventRecommended'" class="boolean-options">
              <label>
                <input type="radio" :name="field.key" value="true" v-model="feedbackData[field.key]" />
                Yes
              </label>
              <label>
                <input type="radio" :name="field.key" value="false" v-model="feedbackData[field.key]" />
                No
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
        <button type="submit" class="submit-button">Submit Feedback</button>
        <label>
          <input type="checkbox" v-model="feedbackData.anonymousFeedback" />
          Submit as anonymous?
        </label>
      </div>
    </form>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const apiUrl = 'http://193.196.54.172:8000/api';

const overallFields = [
  { key: 'overallScore', label: 'Overall Impression' },
  { key: 'organisationalScore', label: 'Organisation' },
  { key: 'relevanceScore', label: 'Relevance' },
];

const contentFields = [
  { key: 'understandabilityScore', label: 'Understandability' },
  { key: 'contentDepthScore', label: 'Content Depth' },
  { key: 'practicalityScore', label: 'Practicality' },
  { key: 'reasonabilityScore', label: 'Reasonability' },
];

const trainerFields = [
  { key: 'competencyScore', label: 'Competency' },
  { key: 'presentabilityScore', label: 'Presentability' },
  { key: 'interactivityScore', label: 'Interactivity' },
  { key: 'timeManagementScore', label: 'Time Management' },
];

const participationFields = [
  { key: 'participationScore', label: 'Participation' },
  { key: 'atmosphereScore', label: 'Atmosphere' },
  { key: 'networkingScore', label: 'Networking' },
];

const organisationFields = [
  { key: 'equipmentScore', label: 'Equipment' },
  { key: 'comfortabilityScore', label: 'Comfortability' },
  { key: 'communicationScore', label: 'Communication' },
];

const commentFields = [
  { key: 'enjoymentComment', label: 'What did you enjoy most?', maxlength: 200 },
  { key: 'improvementComment', label: 'What could be improved?', maxlength: 200 },
  { key: 'requestComment', label: 'Any requests for future events?', maxlength: 200 },
];

const recommendationFields = [
  { key: 'personalImprovementComment', label: 'What is something you have learned?', maxlength: 200 },
  { key: 'isEventRecommended', label: 'Would you recommend this Workshop?' },
  { key: 'recommendationComment', label: 'Why or why not?', maxlength: 200 },
  { key: 'similarEventParticipationScore', label: 'How likely are you to take part in a similar workshop?' }
];

const feedbackData = ref<Record<string, any>>({});

const userId = ref<number | null>(null);
const eventId = ref('');
const token = ref('');

const route = useRoute();
const router = useRouter();

const submitFeedback = async () => {
  if (!userId.value) {
    alert("Please login first.");
    return;
  }

  console.log(eventId.value);
  console.log(token.value);
  console.log(userId.value);

  if(!eventId.value || !token.value){
    alert('Ungültige Daten. Bitte scannen sie den QR-Code erneut.');
    return;
  }

  feedbackData.value['eventId'] = eventId.value;
  feedbackData.value['userId'] = userId.value;

  // TODO: daten validieren

  try {
    const feedbackResponse = await fetch(`${apiUrl}/feedback`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(feedbackData.value),
    });

    if(!feedbackResponse.ok){
      alert('Fehler beim Senden des Feedbacks.');
      // TODO: Ansage von Fehlern oder prompt, um daten anzufügen
      return;
    }

    const attendanceResponse = await fetch(`${apiUrl}/events/${eventId.value}/attendance`, {
      method: "POST",
      headers:{
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        token: token.value,
        userId: userId.value,
      }),
    });

    if(!attendanceResponse.ok){
      alert('Fehler bem Bestätigen der Anwesenheit.');
      return;
    }
    alert('Feedback erfolgreich übermittelt und Anwesenheit bestätigt!');
    router.push('/home');
  } catch (error) {
    console.error('Error submitting feedback:', error);
    alert('An error occurred while submitting feedback.');
  }
};

onMounted(() => {
  const storedUserId = localStorage.getItem('userId');
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
