package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MenteeReadDto;
import school.faang.user_service.dto.mentorship.MentorReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.MenteeReadMapper;
import school.faang.user_service.mapper.mentorship.MentorReadMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MenteeReadMapper menteeReadMapper;
    private final MentorReadMapper mentorReadMapper;

    public List<MenteeReadDto> getMentees(long userId) {
        User mentor = getUser(userId);

        return Optional.ofNullable(mentor.getMentees()).orElseGet(ArrayList::new).stream()
                .map(menteeReadMapper::toDto)
                .toList();
    }

    public List<MentorReadDto> getMentors(long userId) {
        User mentee = getUser(userId);

        return Optional.ofNullable(mentee.getMentors()).orElseGet(ArrayList::new).stream()
                .map(mentorReadMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = getUser(mentorId);

        boolean isDeleted = Optional.ofNullable(mentor.getMentees())
                .orElseGet(ArrayList::new)
                .removeIf(mentee -> mentee.getId().equals(menteeId));

        saveIfUserDeleted(mentor, isDeleted);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        User mentee = getUser(menteeId);

        boolean isDeleted = Optional.ofNullable(mentee.getMentors())
                .orElseGet(ArrayList::new)
                .removeIf(mentor -> mentor.getId().equals(mentorId));

        saveIfUserDeleted(mentee, isDeleted);
    }

    private User getUser(long userId) {
        return mentorshipRepository.findById(userId).orElseThrow(() -> {
            String message = "Пользователь с ID " + userId + " не найден";
            return new EntityNotFoundException(message);
        });
    }

    private void saveIfUserDeleted(User user, boolean isDeleted) {
        if (isDeleted) {
            mentorshipRepository.save(user);
        }
    }
}
