package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserMentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BecomeMentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMentorshipMapper userMentorshipMapper;

    public List<UserMentorshipDto> getMentees(long userId) {
        User mentor = mentorshipRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("Не существует пользователя с ID: " + userId));

        return mentor.getMentees().stream()
                .map(userMentorshipMapper::toDto)
                .toList();
    }

    public List<UserMentorshipDto> getMentors(long userId) {
        User mentee = mentorshipRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("Не существует пользователя с ID: " + userId));

        return mentee.getMentors().stream()
                .map(userMentorshipMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId).orElseThrow(() ->
                new NoSuchElementException(String.format("Ментор с ID: %d не найден!!!", mentorId)));

        if (!mentor.getMentees().removeIf((mentee) -> mentee.getId().equals(menteeId))){
            throw new NoSuchElementException(String.format("Менти с ID: %d не найден!!!", menteeId));
        }

        mentorshipRepository.save(mentor);
        log.info("Менти с ID: {} из списка ментора с ID: {} удален.", menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = mentorshipRepository.findById(menteeId).orElseThrow(() ->
                new NoSuchElementException(String.format("Менти с ID: %d не найден!!!", menteeId)));

        if(!mentee.getMentors().removeIf((mentor) -> mentor.getId().equals(mentorId))){
            throw new NoSuchElementException(String.format("Ментор с ID: %d не найден!!!", mentorId));
        }

        mentorshipRepository.save(mentee);
        log.info("Ментор с ID: {} из списка менти с ID: {} удален", mentorId, menteeId);
    }
}

