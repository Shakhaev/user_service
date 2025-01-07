package school.faang.user_service.service;

public interface UserService2 {

    User createUser(String firstName, String lastName, String email,
                    String password, String repeatPassword);

}
