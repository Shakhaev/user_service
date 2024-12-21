package school.faang.user_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Student {
    private String firstName;
    private String lastName;
    private int yearOfBirth;
    private String group;
    private String studentID;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String faculty;
    private int yearOfStudy;
    private String major;
    @JsonProperty("GPA")
    private double gpa;
    private String status;
    private String admissionDate;
    private String graduationDate;
    private String degree;
    private String institution;
    private int completionYear;
    private boolean scholarship;
    private String employer;
}