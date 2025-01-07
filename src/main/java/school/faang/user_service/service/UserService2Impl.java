package school.faang.user_service.service;

import java.util.UUID;

public class UserService2Impl implements UserService2 {

    @Override
    public User createUser(String firstName, String lastName, String email, String password, String repeatPassword) {
        System.out.println("createUser method is triggered");

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("User's first name is empty");
        }

        User user = new User(UUID.randomUUID().toString(), firstName, lastName, email);
        System.out.println(user);
        return user;
    }

}
