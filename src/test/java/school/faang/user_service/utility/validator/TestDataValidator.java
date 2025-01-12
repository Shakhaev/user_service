package school.faang.user_service.utility.validator;

public class TestDataValidator extends AbstractDataValidator<Object> {

    @Override
    public void validate(Object data) {
        //just example how to create your own base validators
        checkNotNull(data, "Data cannot be null");
    }
}
