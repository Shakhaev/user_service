package school.faang.user_service.error;

import school.faang.user_service.enums.MessageError;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(MessageError error) {
        super(error.getMessage());
    }

}
