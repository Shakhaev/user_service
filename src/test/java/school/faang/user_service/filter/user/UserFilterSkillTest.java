package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.datatest.DataSubscription;
import school.faang.user_service.dto.UserFilterDto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserFilterSkillTest {

    @InjectMocks
    private UserFilterSkill filter;

    @Test
    void applyWithInitValueSuccessTest() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoWrongValues(null,null);
        userFilterDto.setSkillPattern(DataSubscription.getUserFilterDtoInitValues(null,null).getSkillPattern());

        assertTrue(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF001 - should be true");
    }

    @Test
    void applyWithNullValueSuccessTest() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoWrongValues(null,null);
        userFilterDto.setSkillPattern(null);

        assertTrue(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF002 - should be true");
    }

    @Test
    void applyWrongValueFailTest() {
        UserFilterDto userFilterDto = DataSubscription.getUserFilterDtoInitValues(null,null);
        userFilterDto.setSkillPattern(DataSubscription.getUserFilterDtoWrongValues(null,null).getSkillPattern());
        assertFalse(filter.apply(DataSubscription.getNewUser(1), userFilterDto),
                "EF003 - should be false");
    }
}