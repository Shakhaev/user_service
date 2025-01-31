package school.faang.user_service.model;

import lombok.Data;

@Data
public class Person {
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentId;
    private ContactInfo contactInfo;
    private Education education;
    private Status status;
    private String scholarship;
    private String employer;
}
