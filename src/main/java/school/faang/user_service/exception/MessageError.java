package school.faang.user_service.exception;

public enum MessageError {
    USER_NOT_FOUND_EXCEPTION("User by ID is not found"),
    USER_CANNOT_FOLLOW_TO_HIMSELF("User cannot be follower of himself!"),
    USER_CANNOT_UNFOLLOW_FROM_HIMSELF("User cannot to unfollow of himself!"),
    USER_ALREADY_HAS_THIS_FOLLOWER("User already has this follower");


    private final String message;

    MessageError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}