package school.faang.user_service.model.search.user;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import school.faang.user_service.model.jpa.event.EventStatus;
import school.faang.user_service.model.jpa.event.EventType;

import java.time.LocalDateTime;

public class EventNested {

    @Field(type = FieldType.Keyword)
    private Long id;

    @Field(type = FieldType.Text)
    private String tittle;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Date)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date)
    private LocalDateTime endDate;

    @Field(type = FieldType.Text)
    private String location;

    @Field(type = FieldType.Integer)
    private Integer maxAttendees;

    @Field(type = FieldType.Keyword)
    private String usernameOwner;

    @Field(type = FieldType.Keyword)
    private EventType eventType;

    @Field(type = FieldType.Keyword)
    private EventStatus status;
}
