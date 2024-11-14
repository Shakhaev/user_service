package school.faang.user_service.model.search.user;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;

@Data
@Document(indexName = "users")
@Setting(settingPath = "elasticsearch/settings.json")
public class UserDocument {

    @Id
    private Long userId;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
            })
    private String userName;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
            })
    private String countryName;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
            })
    private String city;

    @Field(type = FieldType.Integer_Range)
    private Integer experience;


    @Field(type = FieldType.Nested)
    private List<GoalNested> goals;

    @Field(type = FieldType.Nested)
    private List<String> skillNames;

    @Field(type = FieldType.Nested)
    private List<EventNested> events;

    @Field(type = FieldType.Double_Range)
    private Double averageRating;

}
