package school.faang.user_service.pojo;

import lombok.Data;

@Data
public class Education {
    public String faculty;
    public Integer yearOfStudy;
    public String major;
    public Double gpa;
}
