package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.participation.EventParticipationDTO;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.forumpost.ForumPostResponseDTO;
import com.sopra.eaplanner.reward.Reward;
import com.sopra.eaplanner.reward.dtos.RewardResponseDTO;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.trainerprofile.TrainerProfileRepository;
import com.sopra.eaplanner.trainerprofile.TrainerProfileResponseDTO;
import com.sopra.eaplanner.user.dtos.UserRequestDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private EventParticipationService eventParticipationService;

    @Autowired
    private TrainerProfileRepository trainerProfileRepository;

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

    public UserResponseDTO createUser(UserRequestDTO requestBody) {
        User userToSave = userRepository.save(new User(requestBody));

        if (userToSave.getRole() == User.Role.ADMIN) {
            TrainerProfile trainerProfile = new TrainerProfile();
            trainerProfile.setUser(userToSave);


            trainerProfile.setBio("");
            trainerProfile.setAverageRating(0.0);
            trainerProfile.setExpertiseTags(new ArrayList<>());

            trainerProfileRepository.save(trainerProfile);
        }

        return new UserResponseDTO(userToSave);
    }



    public UserResponseDTO updateUser(Long id, UserRequestDTO user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already in use.");
        }

        if (!user.getUsername().isBlank()) {
            existingUser.setUsername(user.getUsername());
        }
        if (!user.getFirstname().isBlank()) {
            existingUser.setFirstname(user.getFirstname());
        }
        if (!user.getLastname().isBlank()) {
            existingUser.setLastname(user.getLastname());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        return new UserResponseDTO(userRepository.save(existingUser));
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

    public User removeUserFromEvent(Long userId, Long eventId) {
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Event eventForRemoval = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        userToRemove.getRegisteredEvents().remove(eventForRemoval);

        eventForRemoval.getRegisteredUsers().remove(userToRemove);
        eventParticipationService.deleteAttendance(userToRemove, eventForRemoval);

        userRepository.save(userToRemove);
        eventRepository.save(eventForRemoval);

        return userToRemove;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}