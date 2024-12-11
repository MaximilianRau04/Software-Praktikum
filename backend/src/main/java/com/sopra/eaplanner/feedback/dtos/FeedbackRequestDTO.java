package com.sopra.eaplanner.feedback.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class FeedbackRequestDTO {

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
    @Size(max = 200, message = "enjoymentComment cannot exceed 200 characters")
    private String enjoymentComment;
    @Size(max = 200, message = "improvementComment cannot exceed 200 characters")
    private String improvementComment;
    @Size(max = 200, message = "requestComment cannot exceed 200 characters")
    private String requestComment;

    // Closing Comments
    @Size(max = 200, message = "personalImprovementComment cannot exceed 200 characters")
    private String personalImprovementComment;
    private boolean isEventRecommended;
    @Size(max = 200, message = "recommendationComment cannot exceed 200 characters")
    private String recommendationComment;

    @Min(value = 1, message = "similarEventParticipationScore must be between 1 and 5.")
    @Max(value = 5, message = "similarEventParticipationScore must be between 1 and 5.")
    private Integer similarEventParticipationScore;

    private boolean anonymousFeedback;

    private Long eventId;

    private Long userId;

    public FeedbackRequestDTO() {
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

    public void setIsEventRecommended(boolean isEventRecommended) {
        this.isEventRecommended = isEventRecommended;
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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
