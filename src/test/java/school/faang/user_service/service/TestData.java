package school.faang.user_service.service;

import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class TestData {
    public static Stream<MentorshipRequest> getMentorshipRequestsStream(){
        return getListOfRequests().stream();
    }

    public static List<MentorshipRequest> getListOfRequests() {
        User userBob = User.builder()
                .id(1L)
                .username("Bob")
                .build();
        User userAlice = User.builder()
                .id(2L)
                .username("Alice")
                .build();
        User userJohn = User.builder()
                .id(3L)
                .username("John")
                .build();
        User userJack = User.builder()
                .id(4L)
                .username("Jack")
                .build();

        MentorshipRequest request1 = MentorshipRequest.builder()
                .id(1L)
                .requester(userBob)
                .status(RequestStatus.ACCEPTED)
                .description("description")
                .receiver(userAlice)
                .build();
        MentorshipRequest request2 = MentorshipRequest.builder()
                .id(2L)
                .requester(userAlice)
                .status(RequestStatus.PENDING)
                .description("description")
                .receiver(userJohn)
                .build();
        MentorshipRequest request3 = MentorshipRequest.builder()
                .id(3L)
                .requester(userAlice)
                .status(RequestStatus.REJECTED)
                .description("description")
                .receiver(userJack)
                .build();
        MentorshipRequest request4 = MentorshipRequest.builder()
                .id(4L)
                .requester(userAlice)
                .status(RequestStatus.ACCEPTED)
                .description("specification")
                .receiver(userJack)
                .build();
        MentorshipRequest request5 = MentorshipRequest.builder()
                .id(5L)
                .requester(userAlice)
                .status(RequestStatus.ACCEPTED)
                .description("description")
                .receiver(userJack)
                .build();

        return List.of(request1, request2, request3, request4, request5);
    }
}
