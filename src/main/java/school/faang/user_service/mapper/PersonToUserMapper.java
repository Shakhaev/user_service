package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonToUserMapper {

    // username генерируем из firstName/lastName
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", expression = "java(generateUsername(person.getFirstName(), person.getLastName()))")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(target = "password", ignore = true) // будет генерироваться в сервисе
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "aboutMe", expression = "java(buildAboutMe(person))")
    @Mapping(target = "country", ignore = true)   // присвоим в сервисе
    @Mapping(source = "city", target = "city")
    @Mapping(target = "experience", expression = "java(person.getYearOfStudy())")
    @Mapping(target = "banned", constant = "false")
/*    // Остальные связи и коллекции игнорируем
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followees", ignore = true)
    @Mapping(target = "ownedEvents", ignore = true)
    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "receivedMentorshipRequests", ignore = true)
    @Mapping(target = "sentMentorshipRequests", ignore = true)
    @Mapping(target = "sentGoalInvitations", ignore = true)
    @Mapping(target = "receivedGoalInvitations", ignore = true)
    @Mapping(target = "setGoals", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "participatedEvents", ignore = true)
    @Mapping(target = "recommendationsGiven", ignore = true)
    @Mapping(target = "recommendationsReceived", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "userProfilePic", ignore = true)
    @Mapping(target = "contactPreference", ignore = true)
    @Mapping(target = "premium", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)*/
    User personToUser(Person person);

    // Вспомогательный метод для генерации username
    default String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return "user" + System.currentTimeMillis();
        }
        return (firstName.toLowerCase() + "." + lastName.toLowerCase()).replaceAll("\\s+", "");
    }

    default String buildAboutMe(Person p) {
        StringBuilder sb = new StringBuilder();
        if (p.getFaculty() != null) sb.append("Faculty: ").append(p.getFaculty()).append("\n");
        if (p.getMajor() != null) sb.append("Major: ").append(p.getMajor()).append("\n");
        if (p.getDegree() != null) sb.append("Degree: ").append(p.getDegree()).append("\n");
        if (p.getInstitution() != null) sb.append("Institution: ").append(p.getInstitution()).append("\n");
        if (p.getEmployer() != null) sb.append("Employer: ").append(p.getEmployer()).append("\n");
        if (p.getStatus() != null) sb.append("Status: ").append(p.getStatus()).append("\n");
        if (p.getAdmissionDate() != null) sb.append("Admission Date: ").append(p.getAdmissionDate()).append("\n");
        if (p.getGraduationDate() != null) sb.append("Graduation Date: ").append(p.getGraduationDate()).append("\n");
        if (p.getScholarship() != null && p.getScholarship()) sb.append("Scholarship: Yes").append("\n");
        if (p.getGPA() != null) sb.append("GPA: ").append(p.getGPA()).append("\n");
        return sb.toString().trim();
    }
}