package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class ProcessResultDto {

    private int countSuccessfullySavedUsers;
    private List<String> errors;
}
