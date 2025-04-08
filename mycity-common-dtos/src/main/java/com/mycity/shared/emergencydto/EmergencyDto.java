package com.mycity.shared.emergencydto;

import java.time.LocalDateTime;
import java.util.List;

public class EmergencyDto {

    private Long id;
    private Long userId;
    private String reporterName;
    private String reporterContactPhone;
    private String reporterContactEmail;
    private Double latitude;
    private Double longitude;
    private String address;
    private String locationDescription;
    private String emergencyCategory;
    private String emergencyType;
    private String severityLevel;
    private String incidentDescription;
    private LocalDateTime incidentTime;
    private String status;
    private Long assignedAdminId;
    private String assignedTeam;
    private String resolutionDetails;
    private List<String> imageAttachmentUrls;
    private LocalDateTime reportedAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime lastUpdatedAt;
    private Long placeId;
}