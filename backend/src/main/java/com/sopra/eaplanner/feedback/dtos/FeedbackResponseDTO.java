package com.sopra.eaplanner.feedback.dtos;

import com.sopra.eaplanner.feedback.Feedback;

public class FeedbackResponseDTO {
    private Long id;

    //Overall
    private Integer overallScore;
    private Integer organisationalScore;
    private Integer relevanceScore;

    // Content and structure
    private Integer understandabilityScore;
    private Integer contentDepthScore;
    private Integer practicalityScore;
    private Integer reasonabilityScore;

    // Trainer
    private Integer competencyScore;
    private Integer presentabilityScore;
    private Integer interactivityScore;
    private Integer timeManagementScore;

    // Participation
    private Integer participationScore;
    private Integer atmosphereScore;
    private Integer networkingScore;

    // IT and Organisation
    private Integer equipmentScore;
    private Integer comfortabilityScore;
    private Integer communicationScore;

    // Comments
    private String enjoymentComment;
    private String improvementComment;
    private String requestComment;

    // Closing Comments
    private String personalImprovementComment;
    private boolean isEventRecommended;
    private String recommendationComment;

    private Integer similarEventParticipationScore;

    public FeedbackResponseDTO(Feedback feedback) {
        this.id = feedback.getId();
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
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public Integer getOrganisationalScore() {
        return organisationalScore;
    }

    public void setOrganisationalScore(Integer organisationalScore) {
        this.organisationalScore = organisationalScore;
    }

    public Integer getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Integer relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public Integer getUnderstandabilityScore() {
        return understandabilityScore;
    }

    public void setUnderstandabilityScore(Integer understandabilityScore) {
        this.understandabilityScore = understandabilityScore;
    }

    public Integer getContentDepthScore() {
        return contentDepthScore;
    }

    public void setContentDepthScore(Integer contentDepthScore) {
        this.contentDepthScore = contentDepthScore;
    }

    public Integer getPracticalityScore() {
        return practicalityScore;
    }

    public void setPracticalityScore(Integer practicalityScore) {
        this.practicalityScore = practicalityScore;
    }

    public Integer getReasonabilityScore() {
        return reasonabilityScore;
    }

    public void setReasonabilityScore(Integer reasonabilityScore) {
        this.reasonabilityScore = reasonabilityScore;
    }

    public Integer getCompetencyScore() {
        return competencyScore;
    }

    public void setCompetencyScore(Integer competencyScore) {
        this.competencyScore = competencyScore;
    }

    public Integer getPresentabilityScore() {
        return presentabilityScore;
    }

    public void setPresentabilityScore(Integer presentabilityScore) {
        this.presentabilityScore = presentabilityScore;
    }

    public Integer getInteractivityScore() {
        return interactivityScore;
    }

    public void setInteractivityScore(Integer interactivityScore) {
        this.interactivityScore = interactivityScore;
    }

    public Integer getTimeManagementScore() {
        return timeManagementScore;
    }

    public void setTimeManagementScore(Integer timeManagementScore) {
        this.timeManagementScore = timeManagementScore;
    }

    public Integer getParticipationScore() {
        return participationScore;
    }

    public void setParticipationScore(Integer participationScore) {
        this.participationScore = participationScore;
    }

    public Integer getAtmosphereScore() {
        return atmosphereScore;
    }

    public void setAtmosphereScore(Integer atmosphereScore) {
        this.atmosphereScore = atmosphereScore;
    }

    public Integer getNetworkingScore() {
        return networkingScore;
    }

    public void setNetworkingScore(Integer networkingScore) {
        this.networkingScore = networkingScore;
    }

    public Integer getEquipmentScore() {
        return equipmentScore;
    }

    public void setEquipmentScore(Integer equipmentScore) {
        this.equipmentScore = equipmentScore;
    }

    public Integer getComfortabilityScore() {
        return comfortabilityScore;
    }

    public void setComfortabilityScore(Integer comfortabilityScore) {
        this.comfortabilityScore = comfortabilityScore;
    }

    public Integer getCommunicationScore() {
        return communicationScore;
    }

    public void setCommunicationScore(Integer communicationScore) {
        this.communicationScore = communicationScore;
    }

    public String getEnjoymentComment() {
        return enjoymentComment;
    }

    public void setEnjoymentComment(String enjoymentComment) {
        this.enjoymentComment = enjoymentComment;
    }

    public String getImprovementComment() {
        return improvementComment;
    }

    public void setImprovementComment(String improvementComment) {
        this.improvementComment = improvementComment;
    }

    public String getRequestComment() {
        return requestComment;
    }

    public void setRequestComment(String requestComment) {
        this.requestComment = requestComment;
    }

    public String getPersonalImprovementComment() {
        return personalImprovementComment;
    }

    public void setPersonalImprovementComment(String personalImprovementComment) {
        this.personalImprovementComment = personalImprovementComment;
    }

    public boolean getIsEventRecommended() {
        return isEventRecommended;
    }

    public void setIsEventRecommended(boolean isEventRecommended) {
        this.isEventRecommended = isEventRecommended;
    }

    public String getRecommendationComment() {
        return recommendationComment;
    }

    public void setRecommendationComment(String recommendationComment) {
        this.recommendationComment = recommendationComment;
    }

    public Integer getSimilarEventParticipationScore() {
        return similarEventParticipationScore;
    }

    public void setSimilarEventParticipationScore(Integer similarEventParticipationScore) {
        this.similarEventParticipationScore = similarEventParticipationScore;
    }


    public static FeedbackResponseDTO mockWith(
            Integer overallScore,
            Integer organisationalScore,
            Integer relevanceScore,
            Integer understandabilityScore,
            Integer contentDepthScore,
            Integer practicalityScore,
            Integer reasonabilityScore,
            Integer competencyScore,
            Integer presentabilityScore,
            Integer interactivityScore,
            Integer timeManagementScore,
            Integer participationScore,
            Integer atmosphereScore,
            Integer networkingScore,
            Integer equipmentScore,
            Integer comfortabilityScore,
            Integer communicationScore,
            String enjoymentComment,
            String improvementComment,
            String requestComment,
            String personalImprovementComment,
            boolean isEventRecommended,
            String recommendationComment,
            Integer similarEventParticipationScore
    ) {
        return new FeedbackResponseDTO(
                overallScore,
                organisationalScore,
                relevanceScore,
                understandabilityScore,
                contentDepthScore,
                practicalityScore,
                reasonabilityScore,
                competencyScore,
                presentabilityScore,
                interactivityScore,
                timeManagementScore,
                participationScore,
                atmosphereScore,
                networkingScore,
                equipmentScore,
                comfortabilityScore,
                communicationScore,
                enjoymentComment,
                improvementComment,
                requestComment,
                personalImprovementComment,
                isEventRecommended,
                recommendationComment,
                similarEventParticipationScore
        );
    }

    private FeedbackResponseDTO(
            Integer overallScore,
            Integer organisationalScore,
            Integer relevanceScore,
            Integer understandabilityScore,
            Integer contentDepthScore,
            Integer practicalityScore,
            Integer reasonabilityScore,
            Integer competencyScore,
            Integer presentabilityScore,
            Integer interactivityScore,
            Integer timeManagementScore,
            Integer participationScore,
            Integer atmosphereScore,
            Integer networkingScore,
            Integer equipmentScore,
            Integer comfortabilityScore,
            Integer communicationScore,
            String enjoymentComment,
            String improvementComment,
            String requestComment,
            String personalImprovementComment,
            boolean isEventRecommended,
            String recommendationComment,
            Integer similarEventParticipationScore
    ) {
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
    }
}