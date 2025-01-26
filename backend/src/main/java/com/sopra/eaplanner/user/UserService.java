package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.dtos.RatedEventDTO;
import com.sopra.eaplanner.event.participation.EventParticipationDTO;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.event.tags.TagResponseDTO;
import com.sopra.eaplanner.event.tags.TagService;
import com.sopra.eaplanner.feedback.FeedbackRepository;
import com.sopra.eaplanner.feedback.FeedbackService;
import com.sopra.eaplanner.feedback.FeedbackUtil;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.forumpost.dtos.ForumPostResponseDTO;
import com.sopra.eaplanner.reward.dtos.RewardResponseDTO;
import com.sopra.eaplanner.trainerprofile.HostedEventsDTO;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.trainerprofile.TrainerProfileRepository;
import com.sopra.eaplanner.trainerprofile.TrainerProfileResponseDTO;
import com.sopra.eaplanner.user.dtos.UserRequestDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TrainerProfileRepository trainerProfileRepository;

    @Autowired
    private EventParticipationService eventParticipationService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private TagService tagService;

    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserResponseDTO(user);
    }

    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserResponseDTO(user);
    }

    public Iterable<UserResponseDTO> getAllUsers() {
        Iterable<User> users = userRepository.findAll();
        List<UserResponseDTO> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(new UserResponseDTO(user));
        }
        return dtos;
    }

    public Iterable<EventResponseDTO> getRegisteredEvents(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getRegisteredEvents()
                .stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toSet());
    }

    public HostedEventsDTO getHostedEvents(Long id) {
        List<Event> hostedEvents = eventRepository.findAllEventsOfOrganizer(id);

        LocalDateTime now = LocalDateTime.now();

        List<RatedEventDTO> pastEvents = new ArrayList<>();
        List<RatedEventDTO> futureEvents = new ArrayList<>();

        for (Event event : hostedEvents) {
            Set<TagResponseDTO> tags = event.getTags().stream().map(TagResponseDTO::new).collect(Collectors.toSet());
            LocalDate eventDate = event.getDate();
            LocalTime eventStartTime = event.getStartTime();
            LocalTime eventEndTime = event.getEndTime();

            LocalDateTime eventStartDateTime = eventDate.atTime(eventStartTime);
            LocalDateTime eventEndDateTime = eventDate.atTime(eventEndTime);

            if (eventEndDateTime.isBefore(now)) {
                Double averageRating = event.getFeedbacks().stream()
                        .mapToDouble(FeedbackUtil::getFeedbackRating)
                        .sum() / event.getFeedbacks().size();
                pastEvents.add(new RatedEventDTO(event, averageRating, tags));
            } else if (eventStartDateTime.isAfter(now)) {
                futureEvents.add(new RatedEventDTO(event, 0.0, tags));
            } else {
                futureEvents.add(new RatedEventDTO(event, 0.0, tags));
            }
        }

        return new HostedEventsDTO(pastEvents, futureEvents);
    }

    public Iterable<FeedbackResponseDTO> getGivenFeedback(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getFeedbacks()
                .stream()
                .map(FeedbackResponseDTO::new)
                .collect(Collectors.toSet());
    }

    public Set<ForumPostResponseDTO> getUserForumPosts(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .getForumPosts()
                .stream()
                .map(ForumPostResponseDTO::new)
                .collect(Collectors.toSet());
    }

    public TrainerProfileResponseDTO getTrainerProfile(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }
        TrainerProfile trainerProfile = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .getTrainerProfile();

        if (trainerProfile == null) {
            throw new EntityNotFoundException("Trainer profile not found for user with id: " + userId);
        }

        return new TrainerProfileResponseDTO(trainerProfile);
    }


    public Set<RewardResponseDTO> getUserRewards(Long userId) {
        User userWithRewards = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userWithRewards.getRewards()
                .stream().map(RewardResponseDTO::new)
                .collect(Collectors.toSet());
    }

    public Iterable<EventParticipationDTO> getParticipations(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getParticipations()
                .stream()
                .map(EventParticipationDTO::new)
                .collect(Collectors.toSet());
    }

    public Iterable<EventResponseDTO> getPendingFeedbackEvents(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getRegisteredEvents().stream()
                .filter(event -> feedbackRepository.findByEventId(event.getId()).stream()
                            .noneMatch(feedback -> feedback.getUser().getId().equals(id)))
                .map(EventResponseDTO::new)
                .collect(Collectors.toSet());
    }

    public UserResponseDTO createUser(UserRequestDTO requestBody) {
        User userToSave = userRepository.save(new User(requestBody));

        if (userToSave.getRole() == User.Role.ADMIN) {
            TrainerProfile trainerProfile = new TrainerProfile();
            trainerProfile.setUser(userToSave);
            trainerProfile.setBio("");
            trainerProfile.setAverageRating(0.0);
            trainerProfileRepository.save(trainerProfile);
        }

        return new UserResponseDTO(userToSave);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getFirstname() != null && !user.getFirstname().isBlank()) {
            existingUser.setFirstname(user.getFirstname());
        }
        if (user.getLastname() != null && !user.getLastname().isBlank()) {
            existingUser.setLastname(user.getLastname());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        if (existingUser.getDescription() == null) {
            existingUser.setDescription(user.getDescription());
        } else if (!existingUser.getDescription().equals(user.getDescription())) {
            existingUser.setDescription(user.getDescription());
        }

        return new UserResponseDTO(userRepository.save(existingUser));
    }

    public Set<TagResponseDTO> updateUserInterestTags(Long userId, Set<String> requestBody) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Set<Tag> freshTags = tagService.mergeAndGetTagsFromRequest(requestBody);
        existingUser.setInterestTagsTags(freshTags);

        userRepository.save(existingUser);

        return freshTags.stream().map(TagResponseDTO::new).collect(Collectors.toSet());
    }

    public User registerUserToEvent(Long userId, Long eventId) {
        User userToRegister = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Event eventForRegistration = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        userToRegister.getRegisteredEvents().add(eventForRegistration);
        userRepository.save(userToRegister);

        eventForRegistration.getRegisteredUsers().add(userToRegister);
        eventParticipationService.createAttendance(userToRegister, eventForRegistration);
        eventRepository.save(eventForRegistration);
        return userToRegister;
    }

    public Set<TagResponseDTO> getInterestTags(Long eventId) {
        User user = userRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return user.getInterestTags().stream().map(TagResponseDTO::new).collect(Collectors.toSet());
    }

    public void removeUserFromEvent(Long userId, Long eventId) {
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Event eventForRemoval = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        userToRemove.getRegisteredEvents().remove(eventForRemoval);

        eventForRemoval.getRegisteredUsers().remove(userToRemove);
        eventParticipationService.deleteAttendance(userToRemove, eventForRemoval);

        userRepository.save(userToRemove);
        eventRepository.save(eventForRemoval);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}