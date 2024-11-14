package school.faang.user_service.model.search.user;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import school.faang.user_service.model.jpa.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public class GoalNested {

    @Field(type = FieldType.Keyword)
    private Long goalId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private GoalStatus status;

    @Field(type = FieldType.Date)
    private LocalDateTime deadline;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Nested)
    private List<String> skillsToAchieveNames;
}
