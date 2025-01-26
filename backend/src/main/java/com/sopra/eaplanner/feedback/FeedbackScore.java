package com.sopra.eaplanner.feedback;

public interface FeedbackScore {
    Integer getOverallScore();
    Integer getOrganisationalScore();
    Integer getRelevanceScore();
    Integer getUnderstandabilityScore();
    Integer getContentDepthScore();
    Integer getPracticalityScore();
    Integer getReasonabilityScore();
    Integer getCompetencyScore();
    Integer getPresentabilityScore();
    Integer getInteractivityScore();
    Integer getTimeManagementScore();
    Integer getParticipationScore();
    Integer getAtmosphereScore();
    Integer getNetworkingScore();
    Integer getEquipmentScore();
    Integer getCommunicationScore();
    Integer getComfortabilityScore();
    boolean getIsEventRecommended();
}
