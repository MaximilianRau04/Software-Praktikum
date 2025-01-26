package com.sopra.eaplanner.feedback;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a feedback entity for an event, storing scores and comments for various categories,
 * including event-related aspects, trainer performance, participant experience, and IT/organizational factors.
 * The feedback is linked to an event, a user, and optionally a trainer profile.
 */
@Entity
public class Feedback implements FeedbackScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Overall
    @Min(value = 1, message = "overallScore must be between 1 and 5.")
    @Max(value = 5, message = "overallScore must be between 1 and 5.")
    private Integer overallScore;
    @Min(value = 1, message = "organisationalScore must be between 1 and 5.")
    @Max(value = 5, message = "organisationalScore must be between 1 and 5.")
    private Integer organisationalScore;
    @Min(value = 1, message = "relevanceScore must be between 1 and 5.")
    @Max(value = 5, message = "relevanceScore must be between 1 and 5.")
    private Integer relevanceScore;

    // Content and structure
    @Min(value = 1, message = "understandabilityScore must be between 1 and 5.")
    @Max(value = 5, message = "understandabilityScore must be between 1 and 5.")
    private Integer understandabilityScore;
    @Min(value = 1, message = "contentDepthScore must be between 1 and 5.")
    @Max(value = 5, message = "contentDepthScore must be between 1 and 5.")
    private Integer contentDepthScore;
    @Min(value = 1, message = "practicalityScore must be between 1 and 5.")
    @Max(value = 5, message = "practicalityScore must be between 1 and 5.")
    private Integer practicalityScore;
    @Min(value = 1, message = "reasonabilityScore must be between 1 and 5.")
    @Max(value = 5, message = "reasonabilityScore must be between 1 and 5.")
    private Integer reasonabilityScore;

    // Trainer
    @Min(value = 1, message = "competencyScore must be between 1 and 5.")
    @Max(value = 5, message = "competencyScore must be between 1 and 5.")
    private Integer competencyScore;
    @Min(value = 1, message = "presentabilityScore must be between 1 and 5.")
    @Max(value = 5, message = "presentabilityScore must be between 1 and 5.")
    private Integer presentabilityScore;
    @Min(value = 1, message = "interactivityScore must be between 1 and 5.")
    @Max(value = 5, message = "interactivityScore must be between 1 and 5.")
    private Integer interactivityScore;
    @Min(value = 1, message = "timeManagementScore must be between 1 and 5.")
    @Max(value = 5, message = "timeManagementScore must be between 1 and 5.")
    private Integer timeManagementScore;

    // Participation
    @Min(value = 1, message = "participationScore must be between 1 and 5.")
    @Max(value = 5, message = "participationScore must be between 1 and 5.")
    private Integer participationScore;
    @Min(value = 1, message = "atmosphereScore must be between 1 and 5.")
    @Max(value = 5, message = "atmosphereScore must be between 1 and 5.")
    private Integer atmosphereScore;
    @Min(value = 1, message = "networkingScore must be between 1 and 5.")
    @Max(value = 5, message = "networkingScore must be between 1 and 5.")
    private Integer networkingScore;

    // IT and Organisation
    @Min(value = 1, message = "equipmentScore must be between 1 and 5.")
    @Max(value = 5, message = "equipmentScore must be between 1 and 5.")
    private Integer equipmentScore;
    @Min(value = 1, message = "comfortabilityScore must be between 1 and 5.")
    @Max(value = 5, message = "comfortabilityScore must be between 1 and 5.")
    private Integer comfortabilityScore;
    @Min(value = 1, message = "communicationScore must be between 1 and 5.")
    @Max(value = 5, message = "communicationScore must be between 1 and 5.")
    private Integer communicationScore;

    // Comments
    @Size(max = 500, message = "enjoymentComment cannot exceed 200 characters")
    private String enjoymentComment;
    @Size(max = 500, message = "improvementComment cannot exceed 200 characters")
    private String improvementComment;
    @Size(max = 500, message = "requestComment cannot exceed 200 characters")
    private String requestComment;

    // Closing Comments
    @Size(max = 500, message = "personalImprovementComment cannot exceed 200 characters")
    private String personalImprovementComment;
    private boolean isEventRecommended;
    @Size(max = 500, message = "recommendationComment cannot exceed 200 characters")
    private String recommendationComment;

    @Min(value = 1, message = "similarEventParticipationScore must be between 1 and 5.")
    @Max(value = 5, message = "similarEventParticipationScore must be between 1 and 5.")
    private Integer similarEventParticipationScore;

    private boolean anonymousFeedback;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @NotNull
    @JsonBackReference
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "trainer_profile_id")
    @JsonBackReference
    private TrainerProfile trainerProfile;

    private Double sentiment = 0.0;

    private boolean enjoymentCommentPinned;

    private boolean improvementCommentPinned;

    private boolean requestCommentPinned;

    private boolean personalImprovementCommentPinned;

    private boolean recommendationCommentPinned;

    public Feedback() {
    }

    public Feedback(Long id, Integer overallScore, Integer organisationalScore, Integer relevanceScore, Integer understandabilityScore, Integer contentDepthScore, Integer practicalityScore, Integer reasonabilityScore, Integer competencyScore, Integer presentabilityScore, Integer interactivityScore, Integer timeManagementScore, Integer participationScore, Integer atmosphereScore, Integer networkingScore, Integer equipmentScore, Integer comfortabilityScore, Integer communicationScore, String enjoymentComment, String improvementComment, String requestComment, String personalImprovementComment, boolean isEventRecommended, String recommendationComment, Integer similarEventParticipationScore, boolean anonymousFeedback, Event event, User user) {
        this.id = id;
        this.overallScore = overallScore;
        this.organisationalScore = organisationalScore;
        this.relevanceScore = relevanceScore;
        this.understandabilityScore = understandabilityScore;
        this.contentDepthScore = contentDepthScore;
        this.practicalityScore = practicalityScore;
        this.reasonabilityScore = reasonabilityScore;
        this.competencyScore = competencyScore;
        this.presentabilityScore = presentabilityScore;
        this.interactivityScore = interactivityScore;
        this.timeManagementScore = timeManagementScore;
        this.participationScore = participationScore;
        this.atmosphereScore = atmosphereScore;
        this.networkingScore = networkingScore;
        this.equipmentScore = equipmentScore;
        this.comfortabilityScore = comfortabilityScore;
        this.communicationScore = communicationScore;
        this.enjoymentComment = enjoymentComment;
        this.improvementComment = improvementComment;
        this.requestComment = requestComment;
        this.personalImprovementComment = personalImprovementComment;
        this.isEventRecommended = isEventRecommended;
        this.recommendationComment = recommendationComment;
        this.similarEventParticipationScore = similarEventParticipationScore;
        this.anonymousFeedback = anonymousFeedback;
        this.event = event;
        this.user = user;
    }


    public Feedback(FeedbackRequestDTO feedback, Event event, User user) {
        this.overallScore = feedback.getOverallScore();
        this.organisationalScore = feedback.getOrganisationalScore();
        this.relevanceScore = feedback.getRelevanceScore();
        this.understandabilityScore = feedback.getUnderstandabilityScore();
        this.contentDepthScore = feedback.getContentDepthScore();
        this.practicalityScore = feedback.getPracticalityScore();
        this.reasonabilityScore = feedback.getReasonabilityScore();
        this.competencyScore = feedback.getCompetencyScore();
        this.presentabilityScore = feedback.getPresentabilityScore();
        this.interactivityScore = feedback.getInteractivityScore();
        this.timeManagementScore = feedback.getTimeManagementScore();
        this.participationScore = feedback.getParticipationScore();
        this.atmosphereScore = feedback.getAtmosphereScore();
        this.networkingScore = feedback.getNetworkingScore();
        this.equipmentScore = feedback.getEquipmentScore();
        this.comfortabilityScore = feedback.getComfortabilityScore();
        this.communicationScore = feedback.getCommunicationScore();
        this.enjoymentComment = feedback.getEnjoymentComment();
        this.improvementComment = feedback.getImprovementComment();
        this.requestComment = feedback.getRequestComment();
        this.personalImprovementComment = feedback.getPersonalImprovementComment();
        this.isEventRecommended = feedback.getIsEventRecommended();
        this.recommendationComment = feedback.getRecommendationComment();
        this.similarEventParticipationScore = feedback.getSimilarEventParticipationScore();
        this.anonymousFeedback = feedback.isAnonymousFeedback();
        this.event = event;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Min(value = 1, message = "overallScore must be between 1 and 5.") @Max(value = 5, message = "overallScore must be between 1 and 5.") Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(@Min(value = 1, message = "overallScore must be between 1 and 5.") @Max(value = 5, message = "overallScore must be between 1 and 5.") Integer overallScore) {
        this.overallScore = overallScore;
    }

    public @Min(value = 1, message = "organisationalScore must be between 1 and 5.") @Max(value = 5, message = "organisationalScore must be between 1 and 5.") Integer getOrganisationalScore() {
        return organisationalScore;
    }

    public void setOrganisationalScore(@Min(value = 1, message = "organisationalScore must be between 1 and 5.") @Max(value = 5, message = "organisationalScore must be between 1 and 5.") Integer organisationalScore) {
        this.organisationalScore = organisationalScore;
    }

    public @Min(value = 1, message = "relevanceScore must be between 1 and 5.") @Max(value = 5, message = "relevanceScore must be between 1 and 5.") Integer getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(@Min(value = 1, message = "relevanceScore must be between 1 and 5.") @Max(value = 5, message = "relevanceScore must be between 1 and 5.") Integer relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public @Min(value = 1, message = "understandabilityScore must be between 1 and 5.") @Max(value = 5, message = "understandabilityScore must be between 1 and 5.") Integer getUnderstandabilityScore() {
        return understandabilityScore;
    }

    public void setUnderstandabilityScore(@Min(value = 1, message = "understandabilityScore must be between 1 and 5.") @Max(value = 5, message = "understandabilityScore must be between 1 and 5.") Integer understandabilityScore) {
        this.understandabilityScore = understandabilityScore;
    }

    public @Min(value = 1, message = "contentDepthScore must be between 1 and 5.") @Max(value = 5, message = "contentDepthScore must be between 1 and 5.") Integer getContentDepthScore() {
        return contentDepthScore;
    }

    public void setContentDepthScore(@Min(value = 1, message = "contentDepthScore must be between 1 and 5.") @Max(value = 5, message = "contentDepthScore must be between 1 and 5.") Integer contentDepthScore) {
        this.contentDepthScore = contentDepthScore;
    }

    public @Min(value = 1, message = "practicalityScore must be between 1 and 5.") @Max(value = 5, message = "practicalityScore must be between 1 and 5.") Integer getPracticalityScore() {
        return practicalityScore;
    }

    public void setPracticalityScore(@Min(value = 1, message = "practicalityScore must be between 1 and 5.") @Max(value = 5, message = "practicalityScore must be between 1 and 5.") Integer practicalityScore) {
        this.practicalityScore = practicalityScore;
    }

    public @Min(value = 1, message = "reasonabilityScore must be between 1 and 5.") @Max(value = 5, message = "reasonabilityScore must be between 1 and 5.") Integer getReasonabilityScore() {
        return reasonabilityScore;
    }

    public void setReasonabilityScore(@Min(value = 1, message = "reasonabilityScore must be between 1 and 5.") @Max(value = 5, message = "reasonabilityScore must be between 1 and 5.") Integer reasonabilityScore) {
        this.reasonabilityScore = reasonabilityScore;
    }

    public @Min(value = 1, message = "competencyScore must be between 1 and 5.") @Max(value = 5, message = "competencyScore must be between 1 and 5.") Integer getCompetencyScore() {
        return competencyScore;
    }

    public void setCompetencyScore(@Min(value = 1, message = "competencyScore must be between 1 and 5.") @Max(value = 5, message = "competencyScore must be between 1 and 5.") Integer competencyScore) {
        this.competencyScore = competencyScore;
    }

    public @Min(value = 1, message = "presentabilityScore must be between 1 and 5.") @Max(value = 5, message = "presentabilityScore must be between 1 and 5.") Integer getPresentabilityScore() {
        return presentabilityScore;
    }

    public void setPresentabilityScore(@Min(value = 1, message = "presentabilityScore must be between 1 and 5.") @Max(value = 5, message = "presentabilityScore must be between 1 and 5.") Integer presentabilityScore) {
        this.presentabilityScore = presentabilityScore;
    }

    public @Min(value = 1, message = "interactivityScore must be between 1 and 5.") @Max(value = 5, message = "interactivityScore must be between 1 and 5.") Integer getInteractivityScore() {
        return interactivityScore;
    }

    public void setInteractivityScore(@Min(value = 1, message = "interactivityScore must be between 1 and 5.") @Max(value = 5, message = "interactivityScore must be between 1 and 5.") Integer interactivityScore) {
        this.interactivityScore = interactivityScore;
    }

    public @Min(value = 1, message = "timeManagementScore must be between 1 and 5.") @Max(value = 5, message = "timeManagementScore must be between 1 and 5.") Integer getTimeManagementScore() {
        return timeManagementScore;
    }

    public void setTimeManagementScore(@Min(value = 1, message = "timeManagementScore must be between 1 and 5.") @Max(value = 5, message = "timeManagementScore must be between 1 and 5.") Integer timeManagementScore) {
        this.timeManagementScore = timeManagementScore;
    }

    public @Min(value = 1, message = "participationScore must be between 1 and 5.") @Max(value = 5, message = "participationScore must be between 1 and 5.") Integer getParticipationScore() {
        return participationScore;
    }

    public void setParticipationScore(@Min(value = 1, message = "participationScore must be between 1 and 5.") @Max(value = 5, message = "participationScore must be between 1 and 5.") Integer participationScore) {
        this.participationScore = participationScore;
    }

    public @Min(value = 1, message = "atmosphereScore must be between 1 and 5.") @Max(value = 5, message = "atmosphereScore must be between 1 and 5.") Integer getAtmosphereScore() {
        return atmosphereScore;
    }

    public void setAtmosphereScore(@Min(value = 1, message = "atmosphereScore must be between 1 and 5.") @Max(value = 5, message = "atmosphereScore must be between 1 and 5.") Integer atmosphereScore) {
        this.atmosphereScore = atmosphereScore;
    }

    public @Min(value = 1, message = "networkingScore must be between 1 and 5.") @Max(value = 5, message = "networkingScore must be between 1 and 5.") Integer getNetworkingScore() {
        return networkingScore;
    }

    public void setNetworkingScore(@Min(value = 1, message = "networkingScore must be between 1 and 5.") @Max(value = 5, message = "networkingScore must be between 1 and 5.") Integer networkingScore) {
        this.networkingScore = networkingScore;
    }

    public @Min(value = 1, message = "equipmentScore must be between 1 and 5.") @Max(value = 5, message = "equipmentScore must be between 1 and 5.") Integer getEquipmentScore() {
        return equipmentScore;
    }

    public void setEquipmentScore(@Min(value = 1, message = "equipmentScore must be between 1 and 5.") @Max(value = 5, message = "equipmentScore must be between 1 and 5.") Integer equipmentScore) {
        this.equipmentScore = equipmentScore;
    }

    public @Min(value = 1, message = "comfortabilityScore must be between 1 and 5.") @Max(value = 5, message = "comfortabilityScore must be between 1 and 5.") Integer getComfortabilityScore() {
        return comfortabilityScore;
    }

    public void setComfortabilityScore(@Min(value = 1, message = "comfortabilityScore must be between 1 and 5.") @Max(value = 5, message = "comfortabilityScore must be between 1 and 5.") Integer comfortabilityScore) {
        this.comfortabilityScore = comfortabilityScore;
    }

    public @Min(value = 1, message = "communicationScore must be between 1 and 5.") @Max(value = 5, message = "communicationScore must be between 1 and 5.") Integer getCommunicationScore() {
        return communicationScore;
    }

    public void setCommunicationScore(@Min(value = 1, message = "communicationScore must be between 1 and 5.") @Max(value = 5, message = "communicationScore must be between 1 and 5.") Integer communicationScore) {
        this.communicationScore = communicationScore;
    }

    public @Size(max = 200, message = "enjoymentComment cannot exceed 200 characters") String getEnjoymentComment() {
        return enjoymentComment;
    }

    public void setEnjoymentComment(@Size(max = 200, message = "enjoymentComment cannot exceed 200 characters") String enjoymentComment) {
        this.enjoymentComment = enjoymentComment;
    }

    public @Size(max = 200, message = "improvementComment cannot exceed 200 characters") String getImprovementComment() {
        return improvementComment;
    }

    public void setImprovementComment(@Size(max = 200, message = "improvementComment cannot exceed 200 characters") String improvementComment) {
        this.improvementComment = improvementComment;
    }

    public @Size(max = 200, message = "requestComment cannot exceed 200 characters") String getRequestComment() {
        return requestComment;
    }

    public void setRequestComment(@Size(max = 200, message = "requestComment cannot exceed 200 characters") String requestComment) {
        this.requestComment = requestComment;
    }

    public @Size(max = 200, message = "personalImprovementComment cannot exceed 200 characters") String getPersonalImprovementComment() {
        return personalImprovementComment;
    }

    public void setPersonalImprovementComment(@Size(max = 200, message = "personalImprovementComment cannot exceed 200 characters") String personalImprovementComment) {
        this.personalImprovementComment = personalImprovementComment;
    }

    public boolean getIsEventRecommended() {
        return isEventRecommended;
    }

    public void setIsEventRecommended(boolean eventRecommended) {
        isEventRecommended = eventRecommended;
    }

    public @Size(max = 200, message = "recommendationComment cannot exceed 200 characters") String getRecommendationComment() {
        return recommendationComment;
    }

    public void setRecommendationComment(@Size(max = 200, message = "recommendationComment cannot exceed 200 characters") String recommendationComment) {
        this.recommendationComment = recommendationComment;
    }

    public @Min(value = 1, message = "similarEventParticipationScore must be between 1 and 5.") @Max(value = 5, message = "similarEventParticipationScore must be between 1 and 5.") Integer getSimilarEventParticipationScore() {
        return similarEventParticipationScore;
    }

    public void setSimilarEventParticipationScore(@Min(value = 1, message = "similarEventParticipationScore must be between 1 and 5.") @Max(value = 5, message = "similarEventParticipationScore must be between 1 and 5.") Integer similarEventParticipationScore) {
        this.similarEventParticipationScore = similarEventParticipationScore;
    }

    public boolean isAnonymousFeedback() {
        return anonymousFeedback;
    }

    public void setAnonymousFeedback(boolean anonymousFeedback) {
        this.anonymousFeedback = anonymousFeedback;
    }

    public @NotNull Event getEvent() {
        return event;
    }

    public void setEvent(@NotNull Event event) {
        this.event = event;
    }

    public @NotNull User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    public TrainerProfile getTrainerProfile() {
        return trainerProfile;
    }

    public void setTrainerProfile(TrainerProfile trainerProfile) {
        this.trainerProfile = trainerProfile;
    }

    public boolean isEnjoymentCommentPinned() {
        return enjoymentCommentPinned;
    }

    public void setEnjoymentCommentPinned(boolean enjoymentCommentPinned) {
        this.enjoymentCommentPinned = enjoymentCommentPinned;
    }

    public boolean isImprovementCommentPinned() {
        return improvementCommentPinned;
    }

    public void setImprovementCommentPinned(boolean improvementCommentPinned) {
        this.improvementCommentPinned = improvementCommentPinned;
    }

    public boolean isRequestCommentPinned() {
        return requestCommentPinned;
    }

    public void setRequestCommentPinned(boolean requestCommentPinned) {
        this.requestCommentPinned = requestCommentPinned;
    }

    public boolean isPersonalImprovementCommentPinned() {
        return personalImprovementCommentPinned;
    }

    public void setPersonalImprovementCommentPinned(boolean personalImprovementCommentPinned) {
        this.personalImprovementCommentPinned = personalImprovementCommentPinned;
    }

    public boolean isRecommendationCommentPinned() {
        return recommendationCommentPinned;
    }

    public void setRecommendationCommentPinned(boolean recommendationCommentPinned) {
        this.recommendationCommentPinned = recommendationCommentPinned;
    }

    public Double getSentiment(){
        return sentiment;
    }

    public void setSentiment(Double sentiment){
        this.sentiment = sentiment;
    }

}