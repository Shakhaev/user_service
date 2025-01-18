package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserMentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRelationService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMentorshipMapper userMentorshipMapper;

    public List<UserMentorshipDto> getMentees(long userId) {
        User mentor = getUser(userId);

        return Optional.ofNullable(mentor.getMentees())
                .orElseGet(ArrayList::new)
                .stream()
                .map(userMentorshipMapper::toDto)
                .toList();
    }

    private User getUser(long userId) {
        return mentorshipRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Не существует пользователя с ID: " + userId));
    }

    public List<UserMentorshipDto> getMentors(long userId) {
        User mentee = getUser(userId);

        return Optional.ofNullable(mentee.getMentors())
                .orElseGet(ArrayList::new)
                .stream()
                .map(userMentorshipMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Ментор с ID: %d не найден!!!", mentorId)));

        if (!mentor.getMentees().removeIf((mentee) -> mentee.getId().equals(menteeId))){
            throw new EntityNotFoundException(String.format("Менти с ID: %d не найден!!!", menteeId));
        }

        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = mentorshipRepository.findById(menteeId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Менти с ID: %d не найден!!!", menteeId)));

        if(!mentee.getMentors().removeIf((mentor) -> mentor.getId().equals(mentorId))){
            throw new EntityNotFoundException(String.format("Ментор с ID: %d не найден!!!", mentorId));
        }

        mentorshipRepository.save(mentee);
    }
}

