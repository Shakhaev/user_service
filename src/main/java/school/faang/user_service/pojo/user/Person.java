package school.faang.user_service.pojo.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("yearOfBirth")
    private Integer yearOfBirth;

    @JsonProperty("group")
    private String group;

    @JsonProperty("studentID")
    private String studentID;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("street")
    private String street;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("postalCode")
    private String postalCode;

    @JsonProperty("faculty")
    private String faculty;

    @JsonProperty("yearOfStudy")
    private Integer yearOfStudy;

    @JsonProperty("major")
    private String major;

    @JsonProperty("GPA")
    private Double GPA;

    @JsonProperty("status")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("admissionDate")
    private String admissionDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("graduationDate")
    private String graduationDate;

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("institution")
    private String institution;

    @JsonProperty("completionYear")
    private Integer completionYear;

    @JsonProperty("scholarship")
    private Boolean scholarship;

    @JsonProperty("employer")
    private String employer;
}
