package school.faang.user_service.pojo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
public class Person {
    public String firstName;
    public String lastName;
    public Integer yearOfBirth;
    public String group;
    public String studentID;
    @JsonUnwrapped
    public ContactInfo contactInfo;
    @JsonUnwrapped
    public Education education;
    public String status;
    public String admissionDate;
    public String graduationDate;
    public Boolean scholarship;
    public String employer;

}
