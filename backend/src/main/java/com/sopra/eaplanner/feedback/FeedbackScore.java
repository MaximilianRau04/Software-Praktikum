package com.sopra.eaplanner.feedback;

/**
 * Interface for obtaining feedback scores from various categories related to an event or session.
 * Defines getter methods for retrieving scores in areas such as overall feedback, content, trainer performance,
 * participation, and IT/organizational aspects.
 *
 * <p>Implementing classes should provide the logic for retrieving these feedback scores from their respective data sources.</p>
 */
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
