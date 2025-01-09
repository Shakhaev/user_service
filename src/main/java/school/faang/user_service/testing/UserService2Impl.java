package school.faang.user_service.testing;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class UserService2Impl implements UserService2 {

    private final User2Repository user2Repository;
    private final EmailVerificationService emailVerificationService;

    @Override
    public User2 createUser(String firstName, String lastName, String email, String password, String repeatPassword) {
        System.out.println("createUser method is triggered");

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("User's first name is empty");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("User's last name is empty");
        }

        User2 user2 = new User2(UUID.randomUUID().toString(), firstName, lastName, email);

        boolean isUserCreated;
        try {
            isUserCreated = user2Repository.save(user2);
        } catch (Exception e) {
            throw new UserServiceException("test");
        }
        if (!isUserCreated) throw new UserServiceException("Could not create user");

        try {
            emailVerificationService.scheduleEmailConfirmation(user2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user2;
    }

}
