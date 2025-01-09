package school.faang.user_service.testing;

public interface UserService2 {

    User2 createUser(String firstName, String lastName, String email,
                     String password, String repeatPassword);

}
